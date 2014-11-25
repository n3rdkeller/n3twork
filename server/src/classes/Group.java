package classes;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Group {
  final static Logger log = LogManager.getLogger(Group.class);
  
  private int id;
  private String name;
  private String descr;
  private List<User> members = new ArrayList<User>();
  private Map<String,String> otherProperties = new HashMap<String,String>();

  /**
   * Basic Constructor
   * @param name
   * @param descr
   * @param owner
   * @param otherProperties
   */
  public Group(String name, String descr, Map<String,String> otherProperties) {
    this.name = name;
    this.descr = descr;
    this.otherProperties = otherProperties;
  }
  
  /**
   * Constructor used to set up a new group
   * @param name
   * @param descr
   */
  public Group(String name, String descr){
    this.name = name;
    this.descr = descr;
  }
  
  /**
   * Constructor needed to get Group from db
   * @param id
   */
  public Group(int id) {
    this.id = id;
  }
  
  /**
   * Empty Constructor
   */
  public Group() {
    // empty
  }
  
  /**
   * Gets all data from db
   * @return true if successful / group exists
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean getFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();    
    List<ArrayList<String>> groupList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Groups WHERE id=" + this.id);
    if (groupList.size() == 1) return false;
    
    List<ArrayList<String>> membersTable = DBConnector.selectQuery(conn, 
        "SELECT Users.id,username,email,name,firstName FROM " + DBConnector.DATABASE + ".Members JOIN " 
            + DBConnector.DATABASE + ".Users ON Users.id=Members.memberID WHERE Members.groupID=" + this.id);
    conn.close();
    
    //fill up groupMap
    Map<String,String> groupMap = new HashMap<String,String>();
    ArrayList<String> keyRow = groupList.get(0);
    ArrayList<String> dataRow = groupList.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      groupMap.put(keyRow.get(i), dataRow.get(i));
    }
    
    // fill up friendsList
    List<HashMap<String, String>> membersList = new ArrayList<HashMap<String,String>>();
    keyRow = membersTable.get(0);
    membersTable.remove(0);
    for (ArrayList<String> data : membersTable){
      HashMap<String,String> userHelperMap = new HashMap<String,String>();
      for (int i = 0; i < keyRow.size(); i++){
        userHelperMap.put(keyRow.get(i), data.get(i));
      }
      membersList.add(userHelperMap);
    }
    
    // set attributes
    this.name = groupMap.get("name");
    this.descr = groupMap.get("descr");
    
    for (HashMap<String, String> user : membersList){
      // add every User in membersList with the User(id, username, email, name, firstName) constructor
      this.members.add(new User(
          Integer.parseInt(user.get("id")), 
          user.get("username"), 
          user.get("email"), 
          user.get("name"), 
          user.get("firstName")));
    }
    return true;  
  }
  
  /**
   * Gets description and name from db
   * @return true if successful / group exists
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean getFromDBMin() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();    
    List<ArrayList<String>> groupList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Groups WHERE id=" + this.id);
    conn.close();
    if (groupList.size() == 1) return false;
    Map<String,String> groupMap = new HashMap<String,String>();
    
    ArrayList<String> keyRow = groupList.get(0);
    ArrayList<String> dataRow = groupList.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      groupMap.put(keyRow.get(i), dataRow.get(i));
    }
    
    //setting attributes
    this.name = groupMap.get("name");
    this.descr = groupMap.get("descr");
    return true;
  }
  
  public Boolean registerInDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> groupList = new ArrayList<ArrayList<String>>();
    
    if(this.name.equals("")) { // name not given
      log.debug("group name is not given");
      return false;
      
    } else {
      log.debug("checking for group by name: " + this.name);
      groupList = DBConnector.selectQuery(conn, 
          "SELECT * FROM " + DBConnector.DATABASE + ".Groups WHERE name='" + this.name + "'");
    }
    
    if (groupList.size() == 1) {
      List<Integer> ids = DBConnector.executeUpdate(conn, 
          "INSERT INTO " + DBConnector.DATABASE + ".Groups(name,descr) VALUES('" + this.name + "','" + this.descr +"')"); 
      this.id = ids.get(0);
      return true;
    } else {
      log.debug("Group already exists");
      return false;
    }
  }
  
  public String getAsJson(){
    JsonObjectBuilder otherProperties = Json.createObjectBuilder();
    for (Entry<String, String> e : this.otherProperties.entrySet()) {
      otherProperties.add(e.getKey(), e.getValue());
    }
    JsonObject userJson = Json.createObjectBuilder()
      .add("id", this.id)
      .add("name", this.name)
      .add("descr", this.descr)
      .add("otherProperties", otherProperties)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(userJson);
    return jsonString;
  }
  
  public String getName() {
    return this.name;
  }

  public String getDescr() {
    return this.descr;
  }

  public void setDescr(String descr) {

  }

  public String getMembersAsJson() {
    JsonArrayBuilder memberList = Json.createArrayBuilder();
    for (User member : this.members) {
      memberList.add(Json.createObjectBuilder()
          .add("id", member.getId())
          .add("username", member.getUsername())
          .add("email", member.getEmail())
          .add("name", member.getName())
          .add("firstName", member.getFirstName()));
    }
    
    JsonObject membersObject = Json.createObjectBuilder()
      .add("members", memberList)
      .build();
    String jsonString = String.valueOf(membersObject);
    return jsonString;
  }

  public void addMember(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.members.add(user);
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "INSERT INTO " + DBConnector.DATABASE + ".Members(groupID,userID) VALUES(" + this.id + "," + user.getId() +")"); 
  }

  public void removeMember(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    if (DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Members WHERE memberID=" + user.getId() + " AND groupID=" + this.id).size() != 1) {
      DBConnector.executeUpdate(conn, 
          "DELETE FROM " + DBConnector.DATABASE + ".Members WHERE memberID=" + user.getId() + " AND groupID=" + this.id);
    }
  }

  public String getOtherProperty(String key) {
    return null;
  }

  public void setOtherProperty(String key, String value) {
    this.otherProperties.put(key, value);
  }

}
