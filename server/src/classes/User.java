package classes;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import classes.*;

public class User {
  private int id;
  public String name;
  public String uname;
  public String email;
  public String passwd;
  public Map<String,String> otherProperties = new HashMap<String,String>();
  public List<Integer> sessionIDs = new ArrayList<Integer>();
  public List<User> friends = new ArrayList<User>();
  public List<Group> groups = new ArrayList<Group>();
  public List<Post> posts = new ArrayList<Post>();
  public List<Message> messages = new ArrayList<Message>();

}
