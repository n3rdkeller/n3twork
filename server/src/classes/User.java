package classes;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import javax.json.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * A User class object represents a user in the social n3twork.
 * @author johannes
 *
 */
public class User {
  final static Logger log = LogManager.getLogger(User.class);
  
  private int id;
  private String name;
  private String firstName;
  private String username = ""; //TODO change login and register methods, so these don't have to be initialized
  private String email = "";
  private String password = "";
  private String sessionID;
  private Map<String,String> otherProperties = new HashMap<String,String>();
  // date of birth, education, gender
  private Map<User,SimpleEntry<Long,Boolean>> friends = new HashMap<User,SimpleEntry<Long,Boolean>>();
  //  private List<User> friendRequests = new ArrayList<User>();
  private List<Group> groups = new ArrayList<Group>();
  private List<Post> posts = new ArrayList<Post>();
  private List<Message> messages = new ArrayList<Message>();
  
  /**
   * Old constructor for login and registration. Used for testing
   * @param  username
   * @param  email
   * @param  pw - password
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public User(String username, String email, String pw) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    this.username = username;
    this.email = email;
    this.password = md5(pw);

  }
  
  /**
   * Constructor for friends list. It just sets the params.
   * @param id
   * @param username
   * @param email
   * @param name
   * @param firstName
   */
  public User(int id, String username, String email, String name, String firstName) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.name = name;
    this.firstName = firstName;
  }
  
  /**
   * Constructor for login and registration
   * @param userAsJson
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public User(String userAsJson) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonReader jsonReader = Json.createReader(new StringReader(userAsJson));
    JsonObject userAsJsonObject = jsonReader.readObject();
    //userAsJsonObject = userAsJsonObject.getJsonObject("data");
    if (userAsJsonObject.containsKey("login")){
      String login = userAsJsonObject.getString("login");
      if (login.contains("@")) {
        this.email = login;
      } else {
        this.username = login;
      }
    } else {
      this.username = userAsJsonObject.getString("username");
      this.email = userAsJsonObject.getString("email");
    }
    this.password = md5(userAsJsonObject.getString("password"));
  }
  
  /**
   * Constructor for login with sessionID
   * @param sessionID - this is a char[] to differentiate this constructor from User(String userAsJson)
   */
  public User(char[] sessionID){
    this.sessionID = new String(sessionID);
  }

  /**
   * Constructor to look up user via id
   * @param id
   */
  public User(int id) {
    this.id = id;
  }
  
  /**
   * Empty constructor: always handy
   */
  public User() {
    // empty on purpose
  }
  
/**
 * Hashes the seed with md5
 * @param seed
 * @return hashed seed
 * @throws NoSuchAlgorithmException
 * @throws UnsupportedEncodingException
 */
  public static String md5(String seed) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest m = MessageDigest.getInstance("MD5");
    m.update(seed.getBytes("UTF-8"));
    byte[] digest = m.digest();
    BigInteger bigInt = new BigInteger(1,digest);
    String hashtext = bigInt.toString(16);
    // zero padding to get the full 32 chars
    while(hashtext.length() < 32 ){
      hashtext = "0" + hashtext;
    }
    return hashtext;
  }
  
  /**
   * Gets all data except password from the database if userid or sessionid is given.
   * TODO: otherProperties, groups, posts, messages
   * @return true if successful / user exists
   * @throws NoSuchAlgorithmException 
   * @throws UnsupportedEncodingException 
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public Boolean getFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    // get id from sessionID if only sessionID is given
    if (sessionID != null && this.id == 0) {
      log.debug("getFromDB with SessionID " + this.sessionID);
      List<ArrayList<String>> idList = DBConnector.selectQuery(conn, 
          "SELECT userID FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
      if (idList.size() == 2) {
        this.id = Integer.parseInt(idList.get(1).get(0));
      } else if (idList.size() == 1) {
        log.debug("SessionID doesnt exist");
        return false;
      } else {
        log.debug("This SessionID exists more than once");
        return false;
      }
    }

    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
    
    /* friends are a pain in the butt, because you can't return a result set. 
     * That's why I use the whole preparedStatement and ResultSet stuff which is normally hidden behind the selectQuery function
     */
    String sqlQuery = "SELECT Users.id,username,email,name,firstName FROM " + DBConnector.DATABASE + ".Friends JOIN " 
        + DBConnector.DATABASE + ".Users ON Users.id=Friends.friendID WHERE Friends.userID=" + this.id;
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    ResultSet friendsTable = pStmt.executeQuery();
    ResultSetMetaData friendsTableMD = friendsTable.getMetaData();
    int columnsNumber = friendsTableMD.getColumnCount();
    log.debug(sqlQuery);
    
    List<ArrayList<String>> friendRequestsTable = DBConnector.selectQuery(conn, 
        "SELECT userID FROM " + DBConnector.DATABASE + ".Friends WHERE Friends.friendID=" + this.id);
    if (userList.size() == 1) return false;
    Map<String,String> userMap = new HashMap<String,String>();
    List<HashMap<String, String>> friendsList = new ArrayList<HashMap<String,String>>();


    // fill up userMap
    ArrayList<String> keyRow = userList.get(0);
    ArrayList<String> dataRow = userList.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      userMap.put(keyRow.get(i), dataRow.get(i));
    }
    
    //  fill up bothWayFriendsList
    List<String> bothWayFriendsList = new ArrayList<String>();
    friendRequestsTable.remove(0);
    for (ArrayList<String> smallList : friendRequestsTable) {
      bothWayFriendsList.add(smallList.get(0));
    }
    
    // fill up friendsList
    keyRow = new ArrayList<String>();
    for (int i = 1;i <= columnsNumber; i++) {
      keyRow.add(friendsTable.getString(i));
    }
    while (friendsTable.next()){
      HashMap<String,String> userHelperMap = new HashMap<String,String>();
      for (int i = 1;i <= columnsNumber; i++) {
        userHelperMap.put(keyRow.get(i),friendsTable.getString(i));
      }
      friendsList.add(userHelperMap);
    }

    //setting attributes
    this.name = userMap.get("name");
    this.firstName = userMap.get("firstName");
    this.username = userMap.get("username");
    this.email = userMap.get("email");

    friendsTable.next();
    while (friendsTable.next()){
    /* adding every User in friendsList with the User(id, username, email, name, firstName) constructor
     * and saving the date, the friend request was made as well as the boolean value of being a true friend
     */
      if (bothWayFriendsList.contains(friendsTable.getString("id"))) {
        this.friends.put(new User(
            friendsTable.getInt("id"), 
            friendsTable.getString("username"), 
            friendsTable.getString("email"), 
            friendsTable.getString("name"), 
            friendsTable.getString("firstName")),
            new SimpleEntry<Long,Boolean>(friendsTable.getTimestamp("date").getTime(), true));
        
      } else {
        this.friends.put(new User(
            friendsTable.getInt("id"), 
            friendsTable.getString("username"), 
            friendsTable.getString("email"), 
            friendsTable.getString("name"), 
            friendsTable.getString("firstName")),
            new SimpleEntry<Long,Boolean>(friendsTable.getTimestamp("date").getTime(), false));
      }
    }
    conn.close();
    friendsTable.close();
    pStmt.close();
    return true;

  }
  
  /**
   * Minimalist method to get only name, firstName, username and email from db with less SQL queries
   * @return true if successful / user exists
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean getFromDBMin() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    // get id from sessionID if only sessionID is given
    if (sessionID != null && this.id == 0) {
      log.debug("getFromDB with SessionID " + this.sessionID);
      List<ArrayList<String>> idList = DBConnector.selectQuery(conn, 
          "SELECT userID FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
      if (idList.size() == 2) {
        this.id = Integer.parseInt(idList.get(1).get(0));
      } else if (idList.size() == 1) {
        log.debug("SessionID doesnt exist");
        return false;
      } else {
        log.debug("This SessionID exists more than once");
        return false;
      }
    }
    
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
    conn.close();
    if (userList.size() == 1) return false;
    Map<String,String> userMap = new HashMap<String,String>();
    
    ArrayList<String> keyRow = userList.get(0);
    ArrayList<String> dataRow = userList.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      userMap.put(keyRow.get(i), dataRow.get(i));
    }
    
    //setting attributes
    this.name = userMap.get("name");
    this.firstName = userMap.get("firstName");
    this.username = userMap.get("username");
    this.email = userMap.get("email");
    return true;

  }
  
  /**
   * Inserts the User object into the database if there is no entry with the same username or email.
   * @return Boolean - true if the registration was successful; false if either, neither username nor email are given, or the user already exists.
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public Boolean registerInDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
    
    if(this.email.equals("") && this.username.equals("")) { // neither given //TODO make it work with null
      log.debug("neither username nor email are given");
      return false;
      
    } else {
      log.debug("logging in: " + this.username + " " + this.email);
      userList = DBConnector.selectQuery(conn, 
          "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "' OR username='" + this.username + "'");
    }
    
    if (userList.size() == 1) {
      List<Integer> ids = DBConnector.executeUpdate(conn, 
          "INSERT INTO " + DBConnector.DATABASE + ".Users(username,email,password) VALUES('" + this.username + "','" + this.email + "','" + this.password + "')"); 
      this.id = ids.get(0);
      return true;
    } else {
      log.debug("User already exists");
      return false;
    }
  }
  
  /**
   * Method to get the User object as a Json dictionary
   * @return JsonObject converted to String
   */
  public String getAsJson() {
    JsonObjectBuilder otherProperties = Json.createObjectBuilder();
    for (Entry<String, String> e : this.otherProperties.entrySet()) {
      otherProperties.add(e.getKey(), e.getValue());
    }
    JsonObject userJson = Json.createObjectBuilder()
      .add("id", this.id)
      .add("username", this.username)
      .add("email", this.email)
      .add("name", this.name)
      .add("firstName", this.firstName)
      .add("session", this.sessionID)
      .add("otherProperties", otherProperties)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(userJson);
    return jsonString;
  }

  /**
   * Gets all data out of the database if the password is correct
   * @return Boolean - true if login was successful; false if not
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException 
   */
  public Boolean login() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
    
    if(this.email.equals("") && this.username.equals("")) { // neither given
      log.debug("neither username nor email are given");
      return false;
      
    } else {
      log.debug("logging in: " + this.username + " " + this.email);
      userList = DBConnector.selectQuery(conn, 
          "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "' OR username='" + this.username + "'");
    }
    conn.close();

    if(userList.size() == 2) {
      if(userList.get(1).get(1).equals(this.password)) {

        this.id = Integer.parseInt(userList.get(1).get(0));
        getFromDBMin();
        log.debug("login successful");
        return true;

      } else log.debug("wrong password");

    } else if(userList.size() == 1) {
      log.debug("user doesn't exist");
    } else {
      log.debug("email and username dont match or someone doesnt have an email");
    }
    // if one of the previous if-conditions returns false
    log.debug("login failed");
    return false;
  }
  
  /**
   * Deletes the sessionID out of the db if it exists
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void logout() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    if (DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'").size() != 1) {
      DBConnector.executeUpdate(conn, 
          "DELETE FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
    }
  }
  
  /**
   * Produces a list of all usernames. Should not be accessible directly in the api!
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @return list of all users
   */
  public static List<String> getUserList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
        "SELECT username FROM " + DBConnector.DATABASE + ".Users");
    List<String> usernameList = new ArrayList<String>();
    for(ArrayList<String> list : userList){
      usernameList.add(list.get(0));
    }
    usernameList.remove(0);
    return usernameList;
  }
  
  /**
   * Simple getter for the attribute id
   * @return id
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * Simple getter for the attribute name
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Simple setter for the attribute name. Also sets value in db.
   * @param name
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException 
   */
  public void setName(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET name=" + name + " WHERE id=" + this.id);
    conn.close();
    this.name = name;
  }

  /**
   * Simple getter for the attribute firstName
   * @return firstName
   */
  public String getFirstName() {
    return this.firstName;
  }
  
  /**
   * Simple setter for the attribute firstName. Also sets value in db.
   * @param firstName
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void setFirstName(String firstName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET firstName=" + firstName + " WHERE id=" + this.id);
    conn.close();
    this.firstName = firstName;
  }

  /**
   * Simple getter for the attribute username
   * @return username
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Simple getter for the attribute email
   * @return email
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * Simple setter for the attribute email. Also sets value in db.
   * @param email
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void setEmail(String email) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET email='" + email + "' WHERE id=" + this.id);
    this.email = email;
  }

  /**
   * Simple setter for the attribute password. Also sets value in db.
   * @param pw - password 
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void setPassword(String pw) throws NoSuchAlgorithmException, UnsupportedEncodingException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.password = md5(pw);
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET password='" + this.password + "' WHERE id=" + this.id);
  }

  /**
   * Gets property by name. e.g. getOtherProperty("gender")
   * @param key
   * @return property
   */
  public String getOtherProperty(String key) {
    return this.otherProperties.get(key);
  }
  
  /**
   * Setter for otherProperties. Set the whole Map at once. TODO Also sets values in db.
   * @param otherProperties
   */
  public void setOtherProperties(HashMap<String,String> otherProperties) {
    this.otherProperties = otherProperties;
  }
  
  /**
   * Creates a sessionID as an md5 of the username and the current time and saves it in the db
   * @return sessionID
   * @throws UnsupportedEncodingException
   * @throws NoSuchAlgorithmException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public String createSessionID() throws UnsupportedEncodingException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
    String seed = this.username + System.currentTimeMillis();
    this.sessionID = md5(seed);
    // save in db
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
    if(userList.size() > 1) { //sessionID already exists
      this.createSessionID();
    } else {
      DBConnector.executeUpdate(conn, 
          "INSERT INTO " + DBConnector.DATABASE + ".SessionIDs(userID,sessionID) VALUES(" + this.id + ",'" + this.sessionID + "')"); 
    }
    conn.close();
    return this.sessionID;
  }
  
  /**
   * Simple getter for sessionID
   * @return sessionID
   */
  public String getSessionID() {
    return this.sessionID;
  }

//  not used
//  public Boolean checkSessionID() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
//    Connection conn = DBConnector.getConnection();
//    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
//        "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
//    if(userList.size() == 1) {
//      return false;
//    } else {
//      this.id = Integer.parseInt(userList.get(1).get(0));
//      return true;
//    }
//  }

  /**
   * Puts the friends attribute in a nice Json String
   * @return '{"friends":[{"id":"id","username":"username",...},...],"successful":true}'
   */
  public String getFriendsAsJson() {
    JsonArrayBuilder friendList = Json.createArrayBuilder();
    for (Entry<User, SimpleEntry<Long,Boolean>> friend : this.friends.entrySet()) {
      friendList.add(Json.createObjectBuilder()
          .add("id", friend.getKey().getId())
          .add("username", friend.getKey().getUsername())
          .add("email", friend.getKey().getEmail())
          .add("name", friend.getKey().getName())
          .add("firstName", friend.getKey().getFirstName())
          .add("trueFriend", friend.getValue().getValue())
          .add("date", friend.getValue().getKey()));
    }
    
    JsonObject friendsObject = Json.createObjectBuilder()
      .add("friends", friendList)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(friendsObject);
    return jsonString;
  }

  /**
   * Inserts a user with given userID into the Friends table on the db. 
   * There is no need to also put them in the friends attribute, because every time the friends list is requested getFromDB() is called, which does the same.
   * @param friendID id of the added friend
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void addFriendToDB(int friendID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "INSERT INTO " + DBConnector.DATABASE + ".Friends(userID,friendID) VALUES (" + this.id + "," + friendID + ")");
  }

  public List<Group> getGroups() {
    return null;
  }

  public void addGroup(Group group) {

  }

  public List<Post> getPosts() {
    return null;
  }

  public void addPost(Post post) {

  }

  public void votePost(Post post) {

  }

  public List<Message> getMessages() {
    return null;
  }

  public void sendMessage(Message Message) {

  }

  public void deleteMessage(Message Message) {

  }

  public void readMessage(Message Message) {

  }
  
}
