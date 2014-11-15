package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
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

import servlet.ServletResource;

/**
 * The User class represents a user in the social n3twork.
 */
public class User {
  final static Logger log = LogManager.getLogger(User.class);
  
  private int id;
  private String name;
  private String firstName;
  private String username;
  private String email;
  private String password;
  private String sessionID;
  private Map<String,String> otherProperties = new HashMap<String,String>();
  private List<User> friends = new ArrayList<User>();
  private List<Group> groups = new ArrayList<Group>();
  private List<Post> posts = new ArrayList<Post>();
  private List<Message> messages = new ArrayList<Message>();
  
  /**
   * Old constructor for login and registration
   * @param  username
   * @param  email
   * @param  pw - password
   */
  public User(String username, String email, String pw) {
    this.username = username;
    this.email = email;
    this.password = pw;

  }
  
  /**
   * Constructor for login and registration
   * @param userAsJson
   */
  public User(String userAsJson) {
    JsonReader jsonReader = Json.createReader(new StringReader(userAsJson));
    JsonObject userAsJsonObject = jsonReader.readObject();
    //userAsJsonObject = userAsJsonObject.getJsonObject("data");
    this.username = userAsJsonObject.getString("username");
    this.email = userAsJsonObject.getString("email");
    this.password = userAsJsonObject.getString("password");
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
    // empty
  }
  
  /**
   * Method to get all user data except the password from the database.
   * TODO: friends, otherProperties, groups, posts, messages
   * @return true if successful
   * @throws NoSuchAlgorithmException 
   * @throws UnsupportedEncodingException 
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException 
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
  
  public Boolean getFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE id=" + this.id);
    Map<String,String> userMap = new HashMap<String,String>();
    conn.close();
    if (userList.size() == 1) return false;

    ArrayList<String> keyRow = userList.get(0);
    ArrayList<String> dataRow = userList.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      userMap.put(keyRow.get(i),dataRow.get(i));
    }

    //this.name = userMap.get("name");
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
    
    if(this.email.equals("") && this.username.equals("")) { // neither given
      log.debug("neither username nor email are given");
      return false;
      
    } else {
      log.debug("logging in: " + this.username + " " + this.email);
      userList = DBConnector.selectQuery(conn, "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "' OR username='" + this.username + "'");
    }
    
    if (userList.size() == 1) {
      List<Integer> ids = DBConnector.executeUpdate(conn, "INSERT INTO " + DBConnector.DATABASE + ".Users(username,email,password) VALUES('" + this.username + "','" + this.email + "','" + this.password + "')"); 
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
    JsonObject userJson = Json.createObjectBuilder()
      .add("id", this.id)
      .add("username", this.username)
      .add("email", this.email)
      .add("session", this.sessionID)
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
      userList = DBConnector.selectQuery(conn, "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "' OR username='" + this.username + "'");
    }
    conn.close();

    if(userList.size() == 2) {
      if(userList.get(1).get(1).equals(this.password)) {

        this.id = Integer.parseInt(userList.get(1).get(0));
        getFromDB();
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
  
/*//probably a bullshit function!
  public Boolean logout() {
	  this.id = 0;
	  this.name = null;
	  this.firstName = null;
	  this.username = null;
	  this.email = null;
	  this.password = null;
	  this.otherProperties = null;
	  this.sessionIDs = null;
	  this.friends = null;
	  this.groups = null;
	  this.posts = null;
	  this.messages = null;
    return true;
  }*/
  
  /**
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * 
   */
  public static List<String> getUserList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, "SELECT username FROM " + DBConnector.DATABASE + ".Users");
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
    return 0;
  }
  
  /**
   * Simple getter for the attribute name
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Simple setter for the attribute name
   * @param name
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException 
   */
  public void setName(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, "UPDATE " + DBConnector.DATABASE + ".Users SET name=" + name + " WHERE id=" + this.id);
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
   * Simple setter for the attribute firstName
   * @param firstName
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void setFirstName(String firstName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, "UPDATE " + DBConnector.DATABASE + ".Users SET firstName=" + firstName + " WHERE id=" + this.id);
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
   * Simple setter for the attribute email
   * @param email
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void setEmail(String email) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, "UPDATE " + DBConnector.DATABASE + ".Users SET email=" + email + " WHERE id=" + this.id);
    this.email = email;
  }

  /**
   * Simple setter for the attribute password
   * @param pw - password 
   */
  public void setPassword(String pw) {

  }

  public String getOtherProperty(String key) {
    return null;
  }

  public void setOtherProperty(String key, String value) {

  }
  
  public String createSessionID() throws UnsupportedEncodingException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
    Random randomGenerator = new Random();
    String seed = this.username + randomGenerator.nextInt(1000);
    this.sessionID = md5(seed);
    // save in db
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
    if(userList.size() > 1) { //sessionID already exists
      this.createSessionID();
    } else {
      DBConnector.executeUpdate(conn, "INSERT INTO " + DBConnector.DATABASE + ".SessionIDs(userID,sessionID) VALUES(" + this.id + ",'" + this.sessionID + "')"); 
    }
    conn.close();
    return this.sessionID;
  }
  
  public String getSessionID() {
    return this.sessionID;
  }

  public Boolean checkSessionID() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnector.selectQuery(conn, "SELECT * FROM " + DBConnector.DATABASE + ".SessionIDs WHERE sessionID='" + this.sessionID + "'");
    if(userList.size() == 1) {
      return false;
    } else {
      this.id = Integer.parseInt(userList.get(1).get(0));
      return true;
    }
  }

  public List<User> getFriends() {
    return null;
  }

  public void addFriend(User friend) {

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
