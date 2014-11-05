package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;

/**
 * The User class represents a user in the social Network.
 */
public class User {
  //Attributes:
  private int id;
  private String name;
  private String firstName;
  private String username;
  private String email;
  private String passwd;
  private Map<String,String> otherProperties = new HashMap<String,String>();
  private List<Integer> sessionIDs = new ArrayList<Integer>();
  private List<User> friends = new ArrayList<User>();
  private List<Group> groups = new ArrayList<Group>();
  private List<Post> posts = new ArrayList<Post>();
  private List<Message> messages = new ArrayList<Message>();
  //Methods:
  /**
   * Constructor
   * @param  username                    username
   * @param  email                       Email
   * @param  pw                          Password
   * @param  Map<String,otherProperties> Other Properties
   */
  public User(String username, String email, String pw) {
    this.username = username;
    this.email = email;
    this.passwd = pw;

  }
  public Boolean getUserFromDB() {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> userList = DBConnect.executeQuery(conn, "SELECT * FROM Users WHERE id=" + this.id);
    Map<String,String> userMap = new HashMap<String,String>();

    if (userList.size() = 1) return false;

    ArrayList<String> keyRow = userList.get(0);
    ArrayList<String> dataRow = userList.get(1);
    for (int i = 0, i < keyRow.size(), i++) {
      userMap.put(keyRow.get(i),dataRow.get(i));

    }

    this.name = userMap.get("name");
    this.username = userMap.get("username");
    this.email = userMap.get("email");
    this.otherProperties = userMap("otherProperties");

    return true;

  }
  public JsonObject getUserAsJson() {
    JsonObject userJson = new JsonObject();
    userJson.put("id", this.id);
    userJson.put("username", this.username);
    userJson.put("name", this.name);
    userJson.put("email", this.email);

    return userJson;
  }

  public String login() {
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

  public Boolean checkPasswd(String pw) {
    return null;
  }

  public void setPasswd(String pw) {

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
