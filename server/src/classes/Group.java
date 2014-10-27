package classes;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {
  private int id;
  private String name;
  private String descr;
  private List<User> members = new ArrayList<User>()
  private User owner;
  private Map<String,String> otherProperties = new HashMap<String,String>();

  public String getName() {
    return null;
  }

  public String getDescr() {
    return null;
  }

  public void setDescr(String descr) {

  }

  public User getOwner() {
    return null;
  }

  private void setOwner() {

  }

  public List<User> getUsers() {
    return null;
  }

  public void addUser(User user) {

  }

  public void removeUser(User user) {

  }

  public String getOtherProperty(String key) {
    return null;
  }

  public void setOtherProperty(String key, String value) {

  }

}
