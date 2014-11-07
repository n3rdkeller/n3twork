package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.sql.*;
import javax.json.*;

/**
 * The User class represents a user in the social Network.
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
   * Constructor
   * @param  username                    username
   * @param  email                       Email
   * @param  pw                          Password
   */
  public User(String username, String email, String pw) {
    this.username = username;
    this.email = email;
    this.password = pw;

  }


  public User(int id) {
    this.id = id;
  }

  public User() {
    // empty
  }

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
   * @return
   * @throws Exception forwarded exceptions
   */
  public String login() throws Exception{
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
    if(this.username.equals("")) {
    	userList = DBConnector.selectQuery(conn, "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE email='" + this.email + "'");
    } else if(this.email.equals("")) {
    	System.out.println("neither username nor email are given");
    	return"";
    } else {
    	userList = DBConnector.selectQuery(conn, "SELECT id,password FROM " + DBConnector.DATABASE + ".Users WHERE username='" + this.username + "'");
    }
    conn.close();

    if(userList.size() > 1) {
      if(userList.get(1).get(1).equals(this.password)) {

        this.id = Integer.parseInt(userList.get(1).get(0));
        getFromDB();
        System.out.println("login successful");
        return "";

      } else System.out.println("wrong password");

    }
    // if one of the previous if-conditions returns false
    System.out.println("login failed");
    return null;
  }

  public Boolean logout() {
    return null;
  }

  public int getId() {
    return 0;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;

  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {

  }

  public String getUsername() {
    return this.username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {

  }

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

  public Boolean delUser() {
    return null;
  }
}
