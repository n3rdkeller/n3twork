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
  private String name="";
  private String firstName="";
  private String username = ""; //TODO change login and register methods, so these don't have to be initialized
  private String email = "";
  private String password = "";
  private String sessionID;
  private Map<String,String> otherProperties = new HashMap<String,String>();
  // date of birth, education, gender
  private Map<User,SimpleEntry<Long,Boolean>> friends = new HashMap<User,SimpleEntry<Long,Boolean>>();
  private Map<User,SimpleEntry<Long,Boolean>> friendRequests = new HashMap<User,SimpleEntry<Long,Boolean>>();
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
      if (userAsJsonObject.containsKey("email")) {
        this.email = userAsJsonObject.getString("email");
      }
    }
    if (userAsJsonObject.containsKey("password")) {
      this.password = md5(userAsJsonObject.getString("password"));
    }
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
   * Produces a list of all usernames. Should not be accessible directly in the api!
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @return list of all users
   */
  public static List<String> getUsernameList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
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
   * Returns a list of all users, which match the search string in name, firstName, username or email
   * @param searchString
   * @return List of matched users
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static List<User> getAllUsers() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userTable = new ArrayList<ArrayList<String>>();
      userTable = DBConnector.selectQuery(conn, 
          "SELECT * FROM " + DBConnector.DATABASE + ".Users ");
    conn.close();
    List<HashMap<String,String>> userMapList = new ArrayList<HashMap<String,String>>();
    
    ArrayList<String> keyRow = userTable.remove(0);
    for (ArrayList<String> dataRow : userTable) {
      HashMap<String,String> userMap = new HashMap<String,String>();
      for (int i = 0; i < keyRow.size(); i++) {
        userMap.put(keyRow.get(i), dataRow.get(i));
      }
      userMapList.add(userMap);
    }
    List<User> userList = new ArrayList<User>();
    for (HashMap<String,String> userMap: userMapList) {
      User user = new User();
      //setting attributes
      user.id = Integer.parseInt(userMap.remove("id"));
      user.name = userMap.remove("name");
      user.firstName = userMap.remove("firstName");
      user.username = userMap.remove("username");
      user.email = userMap.remove("email");
      userMap.remove("password");
      user.otherProperties.putAll(userMap); 
      userList.add(user);
    }
    return userList;
  }
  
  /**
   * Gets total number of users and number of users currently online
   * @return { "users":x, "usersOnline" }
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static String getSimpleUserStats() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> counterTable = DBConnector.selectQuery(conn, 
        "SELECT Users.id,SessionIDs.sessionID FROM " + DBConnector.DATABASE + ".Users "
        + "LEFT JOIN " + DBConnector.DATABASE + ".SessionIDs "
        + "ON Users.id=SessionIDs.userID");
    counterTable.remove(0); //remove column titles
    int usersOnline = 0;
    String lastID = "";
    List<Integer> toBeRemoved = new ArrayList<Integer>();
    for (ArrayList<String> row : counterTable) {
      String currentID = row.get(0);
      if (lastID.equals(currentID)){
        toBeRemoved.add(counterTable.indexOf(row));
      } else {
        if (row.get(1) != null) {
          usersOnline++;
        }
      }
      lastID = currentID;
    }
    for (int i=0; i < toBeRemoved.size(); i++) {
      counterTable.remove(toBeRemoved.size() - i);
    }
    
    int users = counterTable.size();
    String returnString = String.valueOf(Json.createObjectBuilder()
        .add("users", users)
        .add("usersOnline", usersOnline)
        .build());
    return returnString;
  }
  
  /**
   * Converts any list of users to a json string
   * @param users
   * @return {"userList":[{"id":userID,...},...],"successful":true}
   */
  public static String convertUserListToJson(List<User> users) {
    JsonArrayBuilder userList = Json.createArrayBuilder();
    for (User user : users) {
      JsonObjectBuilder otherProperties = Json.createObjectBuilder();
      for (Entry<String, String> e : user.otherProperties.entrySet()) {
        if (e.getValue() == null) e.setValue("");
        if (e.getValue() != "") otherProperties.add(e.getKey(), e.getValue());
      }
      userList.add(Json.createObjectBuilder()
        .add("id", user.getId())
        .add("username", user.getUsername())
        .add("email", user.getEmail())
        .add("lastname", user.getName())
        .add("firstname", user.getFirstName())
        .add("otherProperties", otherProperties));
    }
    return String.valueOf(Json.createObjectBuilder()
        .add("userList", userList)
        .add("successful", true)
        .build());
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
   * Removes the current user from db
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void removeFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, "DELETE FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
  }
  
//  /**
//   * Gets all data except password from the database if userid or sessionid is given.
//   * TODO: otherProperties, groups, posts, messages
//   * @return true if successful / user exists
//   * @throws NoSuchAlgorithmException 
//   * @throws UnsupportedEncodingException 
//   * @throws SQLException
//   * @throws ClassNotFoundException
//   * @throws IllegalAccessException
//   * @throws InstantiationException
//   */
//  public Boolean getFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
//    Connection conn = DBConnector.getConnection();
//    // get id from sessionID if only sessionID is given
//    if (sessionID != null && this.id == 0) {
//      log.debug("getFromDB with SessionID " + this.sessionID);
//      List<ArrayList<String>> idList = DBConnector.selectQuery(conn, 
//          "SELECT userID FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
//      if (idList.size() == 2) {
//        this.id = Integer.parseInt(idList.get(1).get(0));
//      } else if (idList.size() == 1) {
//        log.debug("SessionID doesnt exist");
//        return false;
//      } else {
//        log.debug("This SessionID exists more than once");
//        return false;
//      }
//    }
//
//    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
//        "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
//    
//    /* friends are a pain in the butt, because you can't return a result set. 
//     * That's why I use the whole preparedStatement and ResultSet stuff which is normally hidden behind the selectQuery function
//     */
//    String sqlQuery = "SELECT Users.id,username,email,name,firstName FROM " + DBConnector.DATABASE + ".Friends JOIN " 
//        + DBConnector.DATABASE + ".Users ON Users.id=Friends.friendID WHERE Friends.userID=" + this.id;
//    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
//    ResultSet friendsTable = pStmt.executeQuery();
//    ResultSetMetaData friendsTableMD = friendsTable.getMetaData();
//    int columnsNumber = friendsTableMD.getColumnCount();
//    log.debug(sqlQuery);
//    
//    List<ArrayList<String>> friendRequestsTable = DBConnector.selectQuery(conn, 
//        "SELECT userID FROM " + DBConnector.DATABASE + ".Friends WHERE Friends.friendID=" + this.id);
//    if (userList.size() == 1) return false;
//    Map<String,String> userMap = new HashMap<String,String>();
//    List<HashMap<String, String>> friendsList = new ArrayList<HashMap<String,String>>();
//
//
//    // fill up userMap
//    ArrayList<String> keyRow = userList.get(0);
//    ArrayList<String> dataRow = userList.get(1);
//    for (int i = 0; i < keyRow.size(); i++) {
//      userMap.put(keyRow.get(i), dataRow.get(i));
//    }
//    
//    //  fill up bothWayFriendsList
//    List<String> bothWayFriendsList = new ArrayList<String>();
//    friendRequestsTable.remove(0);
//    for (ArrayList<String> smallList : friendRequestsTable) {
//      bothWayFriendsList.add(smallList.get(0));
//    }
//    
//    // fill up friendsList
//    keyRow = new ArrayList<String>();
//    for (int i = 1;i <= columnsNumber; i++) {
//      keyRow.add(friendsTable.getString(i));
//    }
//    while (friendsTable.next()){
//      HashMap<String,String> userHelperMap = new HashMap<String,String>();
//      for (int i = 1;i <= columnsNumber; i++) {
//        userHelperMap.put(keyRow.get(i),friendsTable.getString(i));
//      }
//      friendsList.add(userHelperMap);
//    }
//
//    //setting attributes
//    this.name = userMap.get("name");
//    this.firstName = userMap.get("firstName");
//    this.username = userMap.get("username");
//    this.email = userMap.get("email");
//
//    friendsTable.next();
//    while (friendsTable.next()){
//    /* adding every User in friendsList with the User(id, username, email, name, firstName) constructor
//     * and saving the date, the friend request was made as well as the boolean value of being a true friend
//     */
//      if (bothWayFriendsList.contains(friendsTable.getString("id"))) {
//        this.friends.put(new User(
//            friendsTable.getInt("id"), 
//            friendsTable.getString("username"), 
//            friendsTable.getString("email"), 
//            friendsTable.getString("name"), 
//            friendsTable.getString("firstName")),
//            new SimpleEntry<Long,Boolean>(friendsTable.getTimestamp("date").getTime(), true));
//        
//      } else {
//        this.friends.put(new User(
//            friendsTable.getInt("id"), 
//            friendsTable.getString("username"), 
//            friendsTable.getString("email"), 
//            friendsTable.getString("name"), 
//            friendsTable.getString("firstName")),
//            new SimpleEntry<Long,Boolean>(friendsTable.getTimestamp("date").getTime(), false));
//      }
//    }
//    conn.close();
//    friendsTable.close();
//    pStmt.close();
//    return true;
//
//  }
  
  
  /**
   * Gets only name, firstName, username, email and otherProperties from db with 1 select query. Works if sessionID or id is given.
   * @return true if successful / user exists
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean getBasicsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userTable = new ArrayList<ArrayList<String>>();
    // use sessionID or id to get userTable
    if (sessionID != null) {
      userTable = DBConnector.selectQuery(conn,
          "SELECT Users.* FROM " + DBConnector.DATABASE + ".SessionIDs "
              + "JOIN " + DBConnector.DATABASE + ".Users "
              + "ON Users.id = SessionIDs.userID "
              + "WHERE SessionIDs.sessionID='" + this.sessionID + "'");
    } else {
      userTable = DBConnector.selectQuery(conn, 
          "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id + " OR username='" + this.username + "'");
    }
    conn.close();
    if (userTable.size() == 1) return false;
    Map<String,String> userMap = new HashMap<String,String>();
    
    ArrayList<String> keyRow = userTable.get(0);
    ArrayList<String> dataRow = userTable.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      userMap.put(keyRow.get(i), dataRow.get(i));
    }
    
    //setting attributes
    this.id = Integer.parseInt(userMap.remove("id"));
    this.name = userMap.remove("name");
    this.firstName = userMap.remove("firstName");
    this.username = userMap.remove("username");
    this.email = userMap.remove("email");
    userMap.remove("password");
    this.otherProperties.putAll(userMap); 
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
          "SELECT * FROM " + DBConnector.DATABASE + ".Users "
              + "WHERE email='" + this.email + "' OR username='" + this.username + "'");
    }
    
    if (userList.size() == 1) {
      List<Integer> ids = DBConnector.executeUpdate(conn, 
          "INSERT INTO " + DBConnector.DATABASE + ".Users(username,email,password) "
              + "VALUES('" + this.username + "','" + this.email + "','" + this.password + "')"); 
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
    JsonObjectBuilder userJson = Json.createObjectBuilder();
    JsonObjectBuilder otherProperties = Json.createObjectBuilder();
    for (Entry<String, String> e : this.otherProperties.entrySet()) {
      if (e.getValue() == null) e.setValue("");
      if (e.getValue() != "") otherProperties.add(e.getKey(), e.getValue());
    }
    if (this.name == null) this.name="";
    if (this.firstName ==  null) this.firstName="";
    if (this.sessionID != null && this.sessionID != "") {
      userJson.add("session", this.sessionID);
    }
    userJson
      .add("id", this.id)
      .add("username", this.username)
      .add("email", this.email)
      .add("lastname", this.name)
      .add("firstname", this.firstName)
      .add("otherProperties", otherProperties)
      .add("successful", true);
    String jsonString = String.valueOf(userJson.build());
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
          "SELECT id,password FROM " + DBConnector.DATABASE + ".Users "
              + "WHERE email='" + this.email + "' OR username='" + this.username + "'");
    }
    conn.close();

    if(userList.size() == 2) {
      if(userList.get(1).get(1).equals(this.password)) {

        this.id = Integer.parseInt(userList.get(1).get(0));
        getBasicsFromDB();
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
        "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs "
            + "WHERE sessionID='" + this.sessionID + "'").size() != 1) {
      DBConnector.executeUpdate(conn, 
          "DELETE FROM " + DBConnector.DATABASE + ".SessionIDs "
              + "WHERE sessionID='" + this.sessionID + "'");
    }
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
    if (this.name == null) return "";
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
  public User setName(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET name='" + name + "' WHERE id=" + this.id);
    conn.close();
    this.name = name;
    return this;
  }

  /**
   * Simple getter for the attribute firstName
   * @return firstName
   */
  public String getFirstName() {
    if (this.firstName == null) return "";
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
  public User setFirstName(String firstName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET firstName='" + firstName + "' WHERE id=" + this.id);
    conn.close();
    this.firstName = firstName;
    return this;
  }

  /**
   * Simple getter for the attribute username
   * @return username
   */
  public String getUsername() {
    if (this.username == null) return "";
    return this.username;
  }

  public User setUsername(String username) {
    this.username = username;
    return this;
  }
  /**
   * Simple getter for the attribute email
   * @return email
   */
  public String getEmail() {
    if (this.email == null) return "";
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
  public User setEmail(String email) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET email='" + email + "' WHERE id=" + this.id);
    this.email = email;
    return this;
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
  public User setPassword(String pw) throws NoSuchAlgorithmException, UnsupportedEncodingException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.password = md5(pw);
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "UPDATE " + DBConnector.DATABASE + ".Users SET password='" + this.password + "' WHERE id=" + this.id);
    return this;
  }

  public User setSessionID(String session) {
    this.sessionID = session;
    return this;
  }
  
  /**
   * Gets property by name. e.g. getOtherProperty("gender")
   * @param key
   * @return property
   */
  public String getOtherProperty(String key) {
    if (this.otherProperties.get(key) == null) return "";
    return this.otherProperties.get(key);
  }
  
  /**
   * Setter for otherProperties. Also sets values in db. If a column doesn't exist the method creates it. This method uses 1 select query, 1 insert query and maybe 1 alter table query.
   * @param otherProperties
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public User setOtherProperties(HashMap<String,String> properties) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.otherProperties.putAll(properties);
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
    String updateQueryHead = "UPDATE " + DBConnector.DATABASE + ".Users SET ";
    List<String> toBeAdded = new ArrayList<String>();
    
    for (Entry<String,String> prop: this.otherProperties.entrySet()) {
      // check if key is a column
      if (!userList.get(0).contains(prop.getKey())) {
        toBeAdded.add(prop.getKey());
      }
      
      // prepare insert statement
      if (updateQueryHead.endsWith("SET ")) {
        updateQueryHead = updateQueryHead + prop.getKey() + "='" + prop.getValue() + "'";
      } else {
        updateQueryHead = updateQueryHead + "," + prop.getKey() + "='" + prop.getValue() + "'";
      }
    }
    
    if (toBeAdded.size() > 0) {
      // prepare alter table statement
      String alterTable = "ALTER TABLE " + DBConnector.DATABASE + ".Users ADD COLUMN ";
      for (int i = 0; i < toBeAdded.size(); i++) {
        if (i == 0) {
          alterTable = alterTable + "`" +  toBeAdded.get(i) + "` VARCHAR(45) NULL DEFAULT NULL";
        } else {
          alterTable = alterTable + ",`" + toBeAdded.get(i) + "` VARCHAR(45) NULL DEFAULT NULL";
        }
      }
      DBConnector.executeUpdate(conn, alterTable);
    }
    String insertQuery = updateQueryHead + " WHERE id=" + this.id;
    log.debug(insertQuery);
    DBConnector.executeUpdate(conn, insertQuery);
    return this;
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

//  not used TODO: use this method to modulize getFromDB
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
          .add("lastname", friend.getKey().getName())
          .add("firstname", friend.getKey().getFirstName())
          .add("trueFriend", friend.getValue().getValue())
          .add("date", friend.getValue().getKey()));
    }
    
    JsonObject friendsObject = Json.createObjectBuilder()
      .add("friendList", friendList)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(friendsObject);
    return jsonString;
  }
  
  /**
   * Gets friends list from db using 2 select queries
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User getFriendsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    
    /* friends are a pain in the butt, because you can't return a ResultSet in DBConnector.selectQuery and I need it to extract the timestamp. 
     * That's why I use the whole preparedStatement and ResultSet stuff which is normally hidden behind the selectQuery function
     */
    String sqlQuery = 
        "SELECT Users.id,username,email,name,firstName,Friends.date FROM " + DBConnector.DATABASE + ".Friends "
        + "JOIN " + DBConnector.DATABASE + ".Users "
        + "ON Users.id=Friends.friendID "
        + "WHERE Friends.userID=" + this.id;
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    ResultSet friendsTable = pStmt.executeQuery();
    log.debug(sqlQuery);
    
    List<ArrayList<String>> friendRequestsTable = DBConnector.selectQuery(conn, 
        "SELECT userID FROM " + DBConnector.DATABASE + ".Friends WHERE Friends.friendID=" + this.id);
    
    //  fill up bothWayFriendsList
    List<String> bothWayFriendsList = new ArrayList<String>();
    friendRequestsTable.remove(0);
    for (ArrayList<String> smallList : friendRequestsTable) {
      bothWayFriendsList.add(smallList.get(0));
    }
    
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
    return this;
  }
  
  public Map<User,SimpleEntry<Long,Boolean>> getFriends() {
    return this.friends;
  }
  
  /**
   * Puts the friends attribute in a nice Json String
   * @return '{"friends":[{"id":"id","username":"username",...},...],"successful":true}'
   */
  public String getFriendRequestsAsJson() {
    JsonArrayBuilder friendRequestList = Json.createArrayBuilder();
    for (Entry<User, SimpleEntry<Long,Boolean>> friendRequest : this.friendRequests.entrySet()) {
      friendRequestList.add(Json.createObjectBuilder()
          .add("id", friendRequest.getKey().getId())
          .add("username", friendRequest.getKey().getUsername())
          .add("email", friendRequest.getKey().getEmail())
          .add("lastname", friendRequest.getKey().getName())
          .add("firstname", friendRequest.getKey().getFirstName())
          .add("trueFriend", friendRequest.getValue().getValue())
          .add("date", friendRequest.getValue().getKey()));
    }
    
    JsonObject friendRequestsObject = Json.createObjectBuilder()
      .add("friendRequests", friendRequestList)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(friendRequestsObject);
    return jsonString;
  }
  
  /**
   * Gets friends list from db using 2 select queries
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User getFriendRequestsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    
    /* friends are a pain in the butt, because you can't return a ResultSet in DBConnector.selectQuery and I need it to extract the timestamp. 
     * That's why I use the whole preparedStatement and ResultSet stuff which is normally hidden behind the selectQuery function
     */
    String sqlQuery = 
        "SELECT Users.id,username,email,name,firstName,Friends.date FROM " + DBConnector.DATABASE + ".Friends "
        + "JOIN " + DBConnector.DATABASE + ".Users "
        + "ON Users.id=Friends.userID "
        + "WHERE Friends.friendID=" + this.id;
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    ResultSet friendRequestsTable = pStmt.executeQuery();
    log.debug(sqlQuery);
    
    List<ArrayList<String>> friendRequestsTable1 = DBConnector.selectQuery(conn, 
        "SELECT friendID FROM " + DBConnector.DATABASE + ".Friends WHERE Friends.userID=" + this.id);
    
    //  fill up bothWayFriendsList
    List<String> bothWayFriendsList = new ArrayList<String>();
    friendRequestsTable1.remove(0);
    for (ArrayList<String> smallList : friendRequestsTable1) {
      bothWayFriendsList.add(smallList.get(0));
    }
    
    while (friendRequestsTable.next()){
    /* adding every User in friendsList with the User(id, username, email, name, firstName) constructor
     * and saving the date, the friend request was made as well as the boolean value of being a true friend
     */
      if (!bothWayFriendsList.contains(friendRequestsTable.getString("id"))) {
        this.friendRequests.put(new User(
            friendRequestsTable.getInt("id"), 
            friendRequestsTable.getString("username"), 
            friendRequestsTable.getString("email"), 
            friendRequestsTable.getString("name"), 
            friendRequestsTable.getString("firstName")),
            new SimpleEntry<Long,Boolean>(friendRequestsTable.getTimestamp("date").getTime(), false));
      }
    }
    log.debug(this.friendRequests);
    conn.close();
    friendRequestsTable.close();
    pStmt.close();
    return this;
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
  public User addFriendToDB(int friendID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "INSERT INTO " + DBConnector.DATABASE + ".Friends(userID,friendID) VALUES (" + this.id + "," + friendID + ")");
    return this;
  }
  
  /**
   * Deletes given friend
   * @param friendID to be deleted friend
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User removeFriend(int friendID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, "DELETE FROM " + DBConnector.DATABASE + ".Friends WHERE userID=" + this.id + " AND friendID=" + friendID);
    return this;
  }
  
  /**
   * Gets all groups the user is in from the db.
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User getGroupsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> groupTable = DBConnector.selectQuery(conn, 
        "SELECT Groups.id, Groups.name, Groups.descr FROM " + DBConnector.DATABASE + ".Members "
            + "JOIN "+ DBConnector.DATABASE + ".Groups "
            + "ON Groups.id=Members.groupID "
            + "JOIN " + DBConnector.DATABASE + ".Users "
            + "ON Members.memberID=Users.id "
            + "WHERE Users.id=" + this.id
            + " OR Users.username='" + this.username + "'");
    groupTable.remove(0); // remove column names
    for (ArrayList<String> groupTableRow : groupTable) {
      Group group = new Group(Integer.parseInt(groupTableRow.get(0)))
                          .setName(groupTableRow.get(1))
                          .setDescr(groupTableRow.get(2));
      this.groups.add(group);
    }
    return this;
  }
  
  /**
   * Get the list of groups of the current user
   * @return list of groups of the current user
   */
  public List<Group> getGroups() {
    return this.groups;
  }
  
  /**
   * Get the list of groups of the current user as Json
   * @return jsonString
   */
  public String getGroupsAsJson() {
    return Group.convertGroupListToJson(this.groups);
  }

  /**
   * Add a group to the group list of the current user to the db
   * @param group
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User addGroup(Group group) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.groups.add(group);
    group.addMember(this);
    return this;
  }
  
  /**
   * removes mapping of a group to the current user
   * @param group
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User removeGroup(Group group) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    for (Group groupEle : this.groups) {
      if (groupEle.getId() == group.getId()) {
        this.groups.remove(groupEle);
      }
    }
    group.removeMember(this);
    return this;
  }

  public List<Post> getPosts() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> postTable = DBConnector.selectQuery(conn, 
        "SELECT id, title, content, visibility, date " + DBConnector.DATABASE + ".Posts "
        + "WHERE ownerID="+ this.id + " AND type = 0");
    postTable.remove(0);
    for(ArrayList<String> row : postTable) {
      this.posts.add(new Post()
        .setId(Integer.parseInt(row.get(0)))
        .setTitle(row.get(1))
        .setContent(row.get(2))
        .setVisibility(Integer.parseInt(row.get(3)) != 0)
        .setOwner(this.id)
        .setPostDate(new Date(Long.parseLong(row.get(4)))));
    }
    return this.posts;
  }

  public User addPost(Post post) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    post.setOwner(this.id);
    post.setType(false);
    post.createInDB();
    return this;
  }

  public User votePost(Post post) {
    return this;
  }

  public List<Message> getMessages() {
    return null;
  }

  public User sendMessage(Message Message) {
    return this;
  }

  public void deleteMessage(Message Message) {

  }

  public void readMessage(Message Message) {

  }
  
}
