package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import javax.json.*;

/**
 * The User class represents a user in the social n3twork.
 */
public class User {
  private int id;
  private String name;
  private String firstName;
  private String username;
  private String email;
  private String password;
  private Map<String,String> otherProperties = new HashMap<String,String>();
  private List<Integer> sessionIDs = new ArrayList<Integer>();
  private List<User> friends = new ArrayList<User>();
  private List<Group> groups = new ArrayList<Group>();
  private List<Post> posts = new ArrayList<Post>();
  private List<Message> messages = new ArrayList<Message>();
  
  /**
   * Constructor for login and registration
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
   * @return
   * @throws Exception
   */
  public Boolean getFromDB() throws Exception {
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

    this.name = userMap.get("name");
    this.username = userMap.get("username");
    this.email = userMap.get("email");

    return true;

  }
  
  /**
   * Inserts the User object into the database if there is no entry with the same username or email.
   * @return Boolean - true if the registration was successful; false if either, neither username nor email are given, or the user already exists.
   * @throws Exception mostly SQLExceptions
   */
  public Boolean registerInDB() throws Exception {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
    if(this.username.equals("")) {
      userList = DBConnector.selectQuery(conn, "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "'");
    } else if(this.email.equals("")) {
      System.out.println("neither username nor email are given");
      return false;
    } else {
      userList = DBConnector.selectQuery(conn, "SELECT * FROM " + DBConnector.DATABASE + ".Users WHERE username='" + this.username + "'");
    }
    
    if (userList.size() == 1) {
      List<Integer> ids = DBConnector.executeUpdate(conn, "INSERT INTO " + DBConnector.DATABASE + ".Users(username,email,password) VALUES(" + this.username + "," + this.email + "," + this.password + ")"); 
      this.id = ids.get(0);
      return true;
    } else {
      System.out.println("User already exists");
      return false;
    }
  }
  
  public Boolean updateDB() throws Exception {
    if (this.id == 0) return false;
    Connection conn = DBConnector.getConnection();
    List<Integer> emptyList = DBConnector.executeUpdate(conn, "UPDATE " + DBConnector.DATABASE + ".Users SET username=" + this.username + ", email=" + this.email);
    return null;
  }

  /**
   * Method to get the User object as a Json dictionary
   * @return JsonObject converted to String
   */
  public String getAsJson() {
    JsonObject userJson = Json.createObjectBuilder()
      .add("id", this.id)
      .add("username", this.username)
      .add("name", this.name)
      .add("email", this.email)
      .build();
    String jsonString = String.valueOf(userJson);
    return jsonString;
  }

  /**
   * Gets all data out of the database if the password is correct
   * @return Boolean - true if login was successful; false if not
   * @throws Exception forwarded exceptions
   */
  public Boolean login() throws Exception{
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
    if(this.username.equals("")) {
    	userList = DBConnector.selectQuery(conn, "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "'");
    } else if(this.email.equals("")) {
    	System.out.println("neither username nor email are given");
    	return false;
    } else {
    	userList = DBConnector.selectQuery(conn, "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE username='" + this.username + "'");
    }
    conn.close();

    if(userList.size() > 1) {
      if(userList.get(1).get(1).equals(this.password)) {

        this.id = Integer.parseInt(userList.get(1).get(0));
        getFromDB();
        System.out.println("login successful");
        return true;

      } else System.out.println("wrong password");

    }
    // if one of the previous if-conditions returns false
    System.out.println("login failed");
    return false;
  }
/*//propably a bullshit function!
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
   */
  public void setName(String name) {
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
   */
  public void setFirstName(String firstName) {
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
   */
  public void setEmail(String email) {

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

  public List<Integer> getSessionIDs() {
    return null;
  }

  public Boolean checkSessionID(int id) {
    return null;
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
