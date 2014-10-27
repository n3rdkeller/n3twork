package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
/**
 * The User class represents a user in the social Network.
 */
public class User {
  //Attributes:
  private int id;
  private String name;
  private String firstName;
  private String uname;
  private String email;
  private String passwd;
  private Map<String,String> otherProperties = new HashMap<String,String>();
  private List<Integer> sessionIDs = new ArrayList<Integer>();
  private List<User> friends = new ArrayList<User>();
  private List<Group> groups = new ArrayList<Group>();
  private List<Post> posts = new ArrayList<Post>();
  private List<Msg> messages = new ArrayList<Msg>();
  //Methods:
  /**
   * Constructor
   */
  User(String uname, String email, String pw, Map<String,String> otherProperties) {
    this.uname = uname;
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
    return null;
  }

  public void setName(String name) {

  }

  public String getFirstName() {
    return null;
  }

  public void setFirstName() {

  }

  public String getUname() {
    return null;
  }

  public String getEmail() {
    return null;
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

  public List<Msg> getMsgs() {
    return null;
  }

  public void sendMsg(Msg msg) {

  }

  public void deleteMsg(Msg msg) {

  }

  public void readMessage(Msg msg) {

  }

  public Boolean delUser() {

  }
}
