package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * The User class represents a user in the social Network.
 */
public class User extends Object implements Serializable {
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
  public User(String username, String email, String pw, Map<String,String> otherProperties) {
    this.username = username;
    this.email = email;
    this.passwd = pw;
    this.otherProperties = otherProperties;
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
