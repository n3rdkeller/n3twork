package classes;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * This class is central to n3twork, which is to be expected from a social network. Most of the db queries are found here. An object of this class represents a user in the network.
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
  private Map<User,Long> friendRequests = new HashMap<User,Long>();
  private List<Group> groups = new ArrayList<Group>();
  private List<Post> posts = new ArrayList<Post>();
  private List<Conversation> conversations = new ArrayList<Conversation>();
  
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
   * Gets total number of users and number of users currently online as a String in json form
   * @return <pre><code>{
   *  "users": totalNumberOfUsers,
   *  "usersOnline":numberOfUsersOnline,
   *  "successful":true
   *}</code></pre>
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
        .add("successful", true)
        .build());
    return returnString;
  }
  
  /**
   * Converts any list of users to a json string
   * @param users - users in the list don't necessarily need to have it's attributes set 
   * @return <pre><code>{
   *  "userList":[
   *    {
   *      "id":userID,
   *      "username":"username",
   *      "email":"em@il",
   *      "emailhash":"hash of email",
   *      "lastName":"name",
   *      "firstName":"firstName",
   *      "otherProperties:{
   *        "property1":"value",
   *        "property2":"value",
   *      }
   *    },
   *  ],
   *  successful":true
   *}</code></pre>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public static String convertUserListToJson(List<User> users) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
        .add("emailhash", md5(user.getEmail().toLowerCase()))
        .add("lastName", user.getName())
        .add("firstName", user.getFirstName())
        .add("otherProperties", otherProperties));
    }
    return String.valueOf(Json.createObjectBuilder()
        .add("userList", userList)
        .add("successful", true)
        .build());
  }
  
/**
 * Hashes the seed with md5
 * @param seed - Any String
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
   * Removes the current user from db. References to this user will be deleted automatically.
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void removeFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, "DELETE FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
  }
  
  
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
    ResultSet userTable;
    // use sessionID or id to get userTable
    if (sessionID != null) {
      String sqlQuery = "SELECT Users.* FROM " + DBConnector.DATABASE + ".SessionIDs "
          + "JOIN " + DBConnector.DATABASE + ".Users "
          + "ON Users.id = SessionIDs.userID "
          + "WHERE SessionIDs.sessionID=?";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setString(1, this.sessionID);
      log.debug(pStmt);
      userTable = pStmt.executeQuery();
    } else {
      String sqlQuery = "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=? OR username LIKE ?";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setInt(1, this.id);
      pStmt.setString(2, this.username);
      userTable = pStmt.executeQuery();
      log.debug(pStmt);
    }
    ResultSetMetaData userTableMd = userTable.getMetaData();
    int columnsNumber = userTableMd.getColumnCount();
    if (!userTable.next()) return false;
    Map<String,String> userMap = new HashMap<String,String>();
    
    List<String> keyRow = new ArrayList<String>();
    for (int i = 1;i <= columnsNumber; i++) {
      keyRow.add(userTableMd.getColumnName(i));
    }
    for (int i = 1; i <= keyRow.size(); i++) {
      userMap.put(keyRow.get(i - 1), userTable.getString(i));
    }
    
    //setting attributes
    this.id = Integer.parseInt(userMap.remove("id"));
    this.name = userMap.remove("name");
    this.firstName = userMap.remove("firstName");
    this.username = userMap.remove("username");
    this.email = userMap.remove("email");
    userMap.remove("password");
    this.otherProperties.putAll(userMap); 
    conn.close();
    return true;

  }
  
  /**
   * Inserts the User object into the database if there is no entry with the same username or email.
   * @return true if the registration was successful; false if either, neither username nor email are given, or the user already exists.
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public Boolean registerInDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    ResultSet userList;
    if(this.email.equals("") && this.username.equals("")) { // neither given
      log.debug("neither username nor email are given");
      return false;
      
    } else {
      log.debug("logging in: " + this.username + " " + this.email);
      String sqlQuery = "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE email LIKE ? OR username LIKE ?";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setString(1, this.email);
      pStmt.setString(2, this.username);
      log.debug(pStmt);
      userList = pStmt.executeQuery();
    }
    
    if (!userList.next()) {
      String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Users(username,email,password) VALUES(?,?,?)";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery,PreparedStatement.RETURN_GENERATED_KEYS);
      pStmt.setString(1, this.username);
      pStmt.setString(2, this.email);
      pStmt.setString(3, this.password);
      log.debug(pStmt);
      pStmt.executeUpdate();
      ResultSet ids = pStmt.getGeneratedKeys();
      if (ids.next()) this.id = ids.getInt(1);
      return true;
    } else {
      log.debug("User already exists");
      return false;
    }
  }
  
  /**
   * Method to get the User object as a Json object
   * @return <pre><code>{
   *  "id":userID,
   *  "username":"username",
   *  "email":"email",
   *  "lastName":"last name",
   *  "firstName":"first name",
   *  "otherProperties":{
   *    "propertie1":"value",
   *    "propertie2":"value",
   *  },
   *  "successful":true
   *}</pre></code>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public JsonValue getAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
      .add("emailhash", md5(this.email.toLowerCase()))
      .add("lastName", this.name)
      .add("firstName", this.firstName)
      .add("otherProperties", otherProperties)
      .add("successful", true);
    return userJson.build();
  }
  
  /**
   * Gets all data out of the database, after comparing the password hashes.
   * @return true if login was successful; false if not
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException 
   */
  public Boolean login() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    ResultSet userList;
    
    if(this.email.equals("") && this.username.equals("")) { // neither given
      log.debug("neither username nor email are given");
      return false;
      
    } else {
      log.debug("logging in: " + this.username + " " + this.email);
      PreparedStatement pStmt = conn.prepareStatement(
          "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE email LIKE ? OR username LIKE ?");
      pStmt.setString(1, this.email);
      pStmt.setString(2, this.username);
      userList = pStmt.executeQuery();
    }
    if(userList.next()) {
      if(userList.getString("password").equals(this.password)) {
        this.id = userList.getInt("id");
        this.getBasicsFromDB();
        log.debug("login successful");
        return true;
      } else {
        log.debug("wrong password");
      }

    } else {
      log.debug("user doesn't exist");
    }
    // if one of the previous if-conditions returns false
    log.debug("login failed");
    return false;
  }
  
  /**
   * Deletes the sessionID out of the db if it exists.
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void logout() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    PreparedStatement pStmt = conn.prepareStatement( 
        "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID=?");
    pStmt.setString(1, this.sessionID);
    if (pStmt.executeQuery().next()) {
      pStmt = conn.prepareStatement(
          "DELETE FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID=?");
      pStmt.setString(1, this.sessionID);
      pStmt.execute();
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
   * Simple setter for the attribute id
   * @param id
   * @return this
   */
  public User setId(int id) {
    this.id = id;
    return this;    
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
   * Sets name attribute in db and in the object. Needs id to be set.
   * @param name
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException 
   */
  public User setName(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(
        "UPDATE " + DBConnector.DATABASE + ".Users SET name=? WHERE id=?");
    pStmt.setString(1, name);
    pStmt.setInt(2, this.id);
    pStmt.execute();
    pStmt.close();
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
   * Sets firstName attribute in db and in the object. Needs id to be set.
   * @param firstName
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public User setFirstName(String firstName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(
        "UPDATE " + DBConnector.DATABASE + ".Users SET firstName=? WHERE id=?");
    pStmt.setString(1, firstName);
    pStmt.setInt(2, this.id);
    pStmt.execute();
    pStmt.close();
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

  /**
   * Simple setter for username
   * @param username
   * @return this
   */
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
    PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(
        "UPDATE " + DBConnector.DATABASE + ".Users SET email=? WHERE id=?");
    pStmt.setString(1, email);
    pStmt.setInt(2, this.id);
    pStmt.execute();
    pStmt.close();
    this.email = email;
    return this;
  }

  /**
   * Sets password in db and in the object. Needs id to be set.
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
    PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(
        "UPDATE " + DBConnector.DATABASE + ".Users SET password=? WHERE id=?");
    pStmt.setString(1, this.password);
    pStmt.setInt(2, this.id);
    pStmt.execute();
    pStmt.close();
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
  
  public Map<String, String> getOtherProperties() {
    return this.otherProperties;
  }
  
  /**
   * Setter for otherProperties. Also sets values in db. This method uses 1 select query and 1 insert query.
   * @param properties - appends properties to otherProperties.
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
    List<String> values = new ArrayList<String>();
    List<String> keys = new ArrayList<String>();
    for (Entry<String,String> prop: this.otherProperties.entrySet()) {
      // check if key is a column
      if (userList.get(0).contains(prop.getKey())) {
        // prepare insert statement
        if (updateQueryHead.endsWith("SET ")) {
          updateQueryHead = updateQueryHead + "`" + prop.getKey() + "`=?";
        } else {
          updateQueryHead = updateQueryHead + ",`" + prop.getKey() + "`=?";
        }
        values.add(prop.getValue());
        keys.add(prop.getKey());
      }
    }
    if (!updateQueryHead.endsWith("SET ")) {
      String updateQuery = updateQueryHead + " WHERE id=" + this.id;
      PreparedStatement pStmt = conn.prepareStatement(updateQuery);
      for (int i = 0; i < keys.size(); i++) {
        pStmt.setString(i + 1, values.get(i));
      }
      log.debug(pStmt);
      pStmt.execute();
    }
    return this;
  }
  
  /**
   * Creates a sessionID as an md5 of the username and the current time and saves it in the db
   * @return sessionID - <code>md5(this.username + System.currentTimeMillis())</code>
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
    PreparedStatement pStmt = conn.prepareStatement(
        "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID=?");
    pStmt.setString(1, this.sessionID);
    ResultSet userList = pStmt.executeQuery();
    if(userList.next()) { //sessionID already exists
      this.createSessionID();
    } else {
      pStmt = conn.prepareStatement(
          "INSERT INTO " + DBConnector.DATABASE + ".SessionIDs(userID,sessionID) VALUES(?,?)"); 
      pStmt.setInt(1, this.id);
      pStmt.setString(2, this.sessionID);
      pStmt.execute();
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

  /** 
   * Simple setter for sessionID
   * @param session
   * @return this
   */
  public User setSessionID(String session) {
    this.sessionID = session;
    return this;
  }
  
  /**
   * Puts the friends attribute in a nice Json String
   * @return <pre><code>{
   *  "friendList":[
   *    {
   *      "id":"id",
   *      "username":"username",
   *      "lastName":"last name",
   *      "firstName":"first name",
   *      "email":"email",
   *      "trueFriend":true/false,
   *      "date":timestamp of adding      
   *    },
   *  ],
   *  "successful":true
   *}</pre></code>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public String getFriendsAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder friendList = Json.createArrayBuilder();
    for (Entry<User, SimpleEntry<Long,Boolean>> friend : this.friends.entrySet()) {
      friendList.add(Json.createObjectBuilder()
          .add("id", friend.getKey().getId())
          .add("username", friend.getKey().getUsername())
          .add("email", friend.getKey().getEmail())
          .add("emailhash", md5(friend.getKey().getEmail().toLowerCase()))
          .add("lastName", friend.getKey().getName())
          .add("firstName", friend.getKey().getFirstName())
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
    friendsTable.close();
    pStmt.close();
    conn.close();
    return this;
  }
  
  public Map<User,SimpleEntry<Long,Boolean>> getFriends() {
    return this.friends;
  }
  
  /**
   * Puts the friendRequests attribute in a nice Json String
   * @return <pre><code>{
   *  "friendRequests":[
   *    {
   *      "id":"id",
   *      "username":"username",
   *      "lastName":"last name",
   *      "firstName":"first name",
   *      "email":"email",
   *      "date":timestamp of adding 
   *    },
   *  ],
   *  "successful":true
   *}</pre></code>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public String getFriendRequestsAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder friendRequestList = Json.createArrayBuilder();
    for (Entry<User, Long> friendRequest : this.friendRequests.entrySet()) {
      friendRequestList.add(Json.createObjectBuilder()
          .add("id", friendRequest.getKey().getId())
          .add("username", friendRequest.getKey().getUsername())
          .add("email", friendRequest.getKey().getEmail())
          .add("emailhash", md5(friendRequest.getKey().getEmail().toLowerCase()))
          .add("lastName", friendRequest.getKey().getName())
          .add("firstName", friendRequest.getKey().getFirstName())
          .add("trueFriend", false)
          .add("date", friendRequest.getValue()));
    }
    
    JsonObject friendRequestsObject = Json.createObjectBuilder()
      .add("friendRequests", friendRequestList)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(friendRequestsObject);
    return jsonString;
  }
  
  /**
   * Gets friendRequests list from db using 2 select queries
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
            friendRequestsTable.getTimestamp("date").getTime());
      }
    }
    conn.close();
    friendRequestsTable.close();
    pStmt.close();
    return this;
  }

  /**
   * Inserts a user with given userID into the Friends table on the db. 
   * There is no need to also put them in the friends attribute, because every time the friends list is requested getFromDB() is called, which does the same.
   * @param friendID - id of the added friend
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
   * Deletes given friend <pre>
   * <code>DELETE FROM Friends WHERE userID=this.id AND friendID=friendID</code></pre>
   * @param friendID - id of the to be deleted friend
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
    PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(
        "SELECT Groups.id, Groups.name, Groups.descr, (" 
            + "SELECT COUNT(*) FROM " + DBConnector.DATABASE + ".Members "
            + "WHERE Members.groupID = Groups.id) as membercount "
            + "FROM " + DBConnector.DATABASE + ".Members "
            + "JOIN "+ DBConnector.DATABASE + ".Groups "
            + "ON Groups.id=Members.groupID "
            + "JOIN " + DBConnector.DATABASE + ".Users "
            + "ON Members.memberID=Users.id "
            + "WHERE Users.id=?"
            + " OR Users.username LIKE ?");
    pStmt.setInt(1, this.id);
    pStmt.setString(2, this.username);
    ResultSet groupTable = pStmt.executeQuery();
    while (groupTable.next()) {
      Group group = new Group(groupTable.getInt("id"))
                          .setName(groupTable.getString("name"))
                          .setDescr(groupTable.getString("descr"))
                          .setMemberCount(groupTable.getInt("membercount"));
      this.groups.add(group);
    }
    return this;
  }
  
  /**
   * Get the list of groups of the current user
   * @return this.groups
   */
  public List<Group> getGroups() {
    return this.groups;
  }
  
  /**
   * Get the list of groups of the current user as Json
   * @return <code>Group.convertGroupListToJson(this.groups)</code>
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
   * @see Group.addMember
   */
  public User addGroup(Group group) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.groups.add(group);
    group.addMember(this);
    return this;
  }
  
  /**
   * removes mapping of a group to the current user in the db and the object
   * @param group - to be deleted group
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

  /**
   * Gets all posts of a user for the profile page from db
   * @return this.posts
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public List<Post> getPosts(User lookingUser) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "SELECT id, content, visibility, date, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.postID = Posts.id) as votes, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Comments WHERE Comments.postID = Posts.id) as comments, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes "
        + "WHERE Votes.voterID = " + lookingUser.getId() + " AND Votes.postID = Posts.id) as didIVote FROM " + DBConnector.DATABASE + ".Posts "
        + "WHERE authorID="+ this.id + " AND ownerID=0";
    log.debug(sqlQuery);
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    ResultSet postsTable = pStmt.executeQuery();
    while(postsTable.next()) {
      this.posts.add(new Post()
        .setId(postsTable.getInt("id"))
        .setContent(postsTable.getString("content"))
        .setPrivatePost(postsTable.getBoolean("visibility"))
        .setOwner(new Group(0))
        .setAuthor(this)
        .setPostDate(postsTable.getTimestamp("date"))
        .setNumberOfUpVotes(postsTable.getInt("votes"))
        .setDidIVote(postsTable.getInt("didIVote") >= 1)
        .setNumberOfComments(postsTable.getInt("comments")));
    }
    return this.posts;
  }

  /**
   * Add a post to db
   * @param post - doesn't need to have owner and author set
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public User addPost(Post post) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    post.setOwner(new Group(0));
    post.setAuthor(this);
    post.createInDB();
    return this;
  }
  
  /**
   * Gets all posts needed for the news feed with 3 select querys
   * @return postList
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public List<Post> getNewsFeedFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "SELECT Groups.id as groupid, Groups.name as groupname, Groups.descr, "
          + "Users.id as userid, Users.username, Users.email, Users.name, Users.firstName, "
          + "Posts.id as postid, Posts.content, Posts.visibility, Posts.date, "
          + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.postID = Posts.id) as votes, "
          + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Comments WHERE Comments.postID = Posts.id) as comments, "
          + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.voterID = ? AND Votes.postID = Posts.id) as didIVote "
          + "FROM " + DBConnector.DATABASE + ".Posts "
          + "JOIN " + DBConnector.DATABASE + ".Friends ON Friends.friendID = Posts.authorID "
          + "JOIN " + DBConnector.DATABASE + ".Users ON Users.id = Posts.authorID "
          + "JOIN " + DBConnector.DATABASE + ".Groups ON Posts.ownerID = Groups.id "
          + "WHERE Friends.userID = ? AND Posts.ownerID = 0";
    log.debug(sqlQuery);
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    pStmt.setInt(2, this.id);
    ResultSet UserPostsTable = pStmt.executeQuery();
    
    List<ArrayList<String>> friendRequestsTable = DBConnector.selectQuery(conn, 
        "SELECT userID FROM " + DBConnector.DATABASE + ".Friends WHERE Friends.friendID=" + this.id);    
    //  fill up bothWayFriendsList
    List<Integer> bothWayFriendsList = new ArrayList<Integer>();
    friendRequestsTable.remove(0);
    for (ArrayList<String> smallList : friendRequestsTable) {
      bothWayFriendsList.add(Integer.parseInt(smallList.get(0)));
    }
    
    List<Post> postList = new ArrayList<Post>();
    // read UserPostsTable
    while (UserPostsTable.next()) {
      if(bothWayFriendsList.contains(UserPostsTable.getInt("userid"))) {
        // private posts of trueFriends™
        postList.add(new Post()
            .setId(UserPostsTable.getInt("postid"))
            .setContent(UserPostsTable.getString("content"))
            .setPrivatePost(UserPostsTable.getBoolean("visibility"))
            .setPostDate(UserPostsTable.getTimestamp("date"))
            .setOwner(new Group(UserPostsTable.getInt("groupid"))
                .setName(UserPostsTable.getString("groupname")))
            .setAuthor(new User(
                UserPostsTable.getInt("userid"),
                UserPostsTable.getString("username"),
                UserPostsTable.getString("email"),
                UserPostsTable.getString("name"),
                UserPostsTable.getString("firstName")))
            .setNumberOfUpVotes(UserPostsTable.getInt("votes"))
            .setDidIVote(UserPostsTable.getInt("didIVote") >= 1)
            .setNumberOfComments(UserPostsTable.getInt("comments")));
      } else if (!UserPostsTable.getBoolean("visibility")){
        // all public posts
        postList.add(new Post()
            .setId(UserPostsTable.getInt("postid"))
            .setContent(UserPostsTable.getString("content"))
            .setPrivatePost(false)
            .setPostDate(UserPostsTable.getTimestamp("date"))
            .setOwner(new Group(UserPostsTable.getInt("groupid"))
                .setName(UserPostsTable.getString("groupname")))
            .setAuthor(new User(
                UserPostsTable.getInt("userid"),
                UserPostsTable.getString("username"),
                UserPostsTable.getString("email"),
                UserPostsTable.getString("name"),
                UserPostsTable.getString("firstName")))
            .setNumberOfUpVotes(UserPostsTable.getInt("votes"))
            .setDidIVote(UserPostsTable.getInt("didIVote") >= 1)
            .setNumberOfComments(UserPostsTable.getInt("comments")));
      }
    }
    
    UserPostsTable.close();
    pStmt.close();
    
    sqlQuery = "SELECT Groups.id as groupid, Groups.name as groupname, Groups.descr, "
          + "Users.id as userid, Users.username, Users.email, Users.name, Users.firstName, "
          + "Posts.id as postid, Posts.content, Posts.visibility, Posts.date, "
          + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.postID = Posts.id) as votes, "
          + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Comments WHERE Comments.postID = Posts.id) as comments, "
          + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.voterID = ? AND Votes.postID = Posts.id) as didIVote "
          + "FROM " + DBConnector.DATABASE + ".Posts "
          + "JOIN " + DBConnector.DATABASE + ".Users ON Users.id = Posts.authorID "
          + "JOIN " + DBConnector.DATABASE + ".Groups ON Posts.ownerID = Groups.id "
          + "JOIN " + DBConnector.DATABASE + ".Members ON Members.groupID = Posts.ownerID "
          + "WHERE Members.memberID = ?";
    log.debug(sqlQuery);
    pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    pStmt.setInt(2, this.id);
    ResultSet GroupPostsTable = pStmt.executeQuery();
    
    // read GroupPostsTable
    while (GroupPostsTable.next()) {
      postList.add(new Post()
      .setId(GroupPostsTable.getInt("postid"))
      .setContent(GroupPostsTable.getString("content"))
      .setPrivatePost(GroupPostsTable.getBoolean("visibility"))
      .setPostDate(GroupPostsTable.getTimestamp("date"))
      .setOwner(new Group(GroupPostsTable.getInt("groupid"))
          .setName(GroupPostsTable.getString("groupname")))
      .setAuthor(new User(
          GroupPostsTable.getInt("userid"),
          GroupPostsTable.getString("username"),
          GroupPostsTable.getString("email"),
          GroupPostsTable.getString("name"),
          GroupPostsTable.getString("firstName")))
      .setNumberOfUpVotes(GroupPostsTable.getInt("votes"))
      .setDidIVote(GroupPostsTable.getInt("didIVote") >= 1)
      .setNumberOfComments(GroupPostsTable.getInt("comments")));
    }
    
    GroupPostsTable.close();
    pStmt.close();
    conn.close();
    postList.addAll(this.getPosts(this));
    // sort List by postDate
    Collections.sort(postList, new PostComparator());
    return postList;
  }

  /** 
   * Simple getter for conversations
   * @return this.conversations
   */
  public List<Conversation> getConversations() {
    return this.conversations;
  }
  
  /**
   * Gets all conversations the user is associated with, which are not marked as archived
   * @return this.conversations
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public List<Conversation> getConversationsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "SELECT Conversations.id, Conversations.name, "
        + "Users.id as userid, Users.username, Users.name as lastName, Users.firstName, Users.email, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Messages WHERE conversationID = Conversations.id "
        + "AND id > (SELECT lastreadID FROM " + DBConnector.DATABASE + ".Receivers "
            + "WHERE receiverID = ? AND conversationID = Conversations.id)) as unread "
        + "FROM " + DBConnector.DATABASE + ".Conversations "
        + "JOIN " + DBConnector.DATABASE + ".Receivers ON Receivers.conversationID = Conversations.id "
        + "JOIN " + DBConnector.DATABASE + ".Users ON Receivers.receiverID = Users.id "
        + "WHERE Conversations.id IN (select Conversations.id "
          + "FROM " + DBConnector.DATABASE + ".Conversations "
          + "JOIN " + DBConnector.DATABASE + ".Receivers ON Receivers.conversationID = Conversations.id "
          + "WHERE Receivers.receiverID = ? AND Receivers.deleted = 0) "
        + "ORDER BY Conversations.id";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    pStmt.setInt(2, this.id);
    ResultSet messageTable = pStmt.executeQuery();
    Conversation con = new Conversation();
    List<User> receivers = new ArrayList<User>();
    
    while(messageTable.next()) {
      if(con.getID() != messageTable.getInt("id")) {
        if(receivers.size() != 0) {
          this.conversations.add(con.setReceivers(receivers));
        }
        receivers = new ArrayList<User>();
        con = new Conversation()
           .setID(messageTable.getInt("id"))
           .setName(messageTable.getString("name"))
           .setUnread(messageTable.getInt("unread"));
      }
      if(messageTable.getInt("userid") != this.id) {
        receivers.add(new User(messageTable.getInt("userid"),
                               messageTable.getString("username"),
                               messageTable.getString("email"),
                               messageTable.getString("lastName"),
                               messageTable.getString("firstName")));
      }
    }
    this.conversations.add(con.setReceivers(receivers));
    conn.close();
    return this.conversations;
  }
  
}
