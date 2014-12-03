package classes;
import java.sql.Connection;
import java.sql.Date;
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
import javax.json.JsonValue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Group {
  final static Logger log = LogManager.getLogger(Group.class);
  
  private int id;
  private String name = "";
  private String descr = "";
  private List<User> members = new ArrayList<User>();
  private Map<String,String> otherProperties = new HashMap<String,String>();
  private List<Post> posts;
  private int memberCount;

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
   * Get a list of all groups
   * @param searchString
   * @return list of all groups
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static List<Group> findGroup(String searchString) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<Group> groupList = new ArrayList<Group>();
    List<ArrayList<String>> groupTable = DBConnector.selectQuery(conn, 
        "SELECT id,name, (" 
            + "SELECT COUNT(*) FROM Members "
            + "WHERE Members.groupID = Groups.id) as membercount "
            + "FROM " + DBConnector.DATABASE + ".Groups"
            + "WHERE id != 0");
    groupTable.remove(0); // remove column names
    for (ArrayList<String> groupTableRow : groupTable) {
      if (groupTableRow.get(1).toLowerCase().contains(searchString.toLowerCase())) {
        Group group = new Group(
            Integer.parseInt(groupTableRow.get(0)))
            .setName(groupTableRow.get(1))
            .setMemberCount(Integer.parseInt(groupTableRow.get(3)));
        groupList.add(group);
      }
    }
    return groupList;
  }
  
  /**
   * Gets simple group stats as json
   * @return jsonString
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static String getSimpleGroupStats() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> counterTable = DBConnector.selectQuery(conn, 
        "SELECT id FROM " + DBConnector.DATABASE + ".Groups");
    counterTable.remove(0); //remove column titles
    int groups = counterTable.size();
    String returnString = String.valueOf(Json.createObjectBuilder()
        .add("groups", groups)
        .build());
    return returnString;
  }
  
  /**
   * Converts any list of groups to json String
   * @param groupList any list of groups
   * @return jsonString
   */
  public static String convertGroupListToJson(List<Group> groupList) {
    JsonArrayBuilder groupJsonList = Json.createArrayBuilder();
    for (Group group : groupList) {
      JsonObjectBuilder otherProperties = Json.createObjectBuilder();
      for (Entry<String, String> e : group.getOtherProperties().entrySet()) {
        if (e.getValue() == null) e.setValue("");
        otherProperties.add(e.getKey(), e.getValue());
      }
      groupJsonList.add(Json.createObjectBuilder()
          .add("groupID", group.getId())
          .add("groupName", group.getName())
          .add("groupDescr", group.getDescr())
          .add("memberCount", group.getMemberCount()));
    }
    
    return String.valueOf(Json.createObjectBuilder()
        .add("groupList",groupJsonList)
        .add("successful", true)
        .build());
  }
  
  /**
   * Removes current user from DB
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void removeFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    if(this.id != 0) {
      Connection conn = DBConnector.getConnection();
      DBConnector.executeUpdate(conn, "DELETE FROM " + DBConnector.DATABASE + ".Groups WHERE id=" + this.id);
    }
  }
  
  /**
   * Gets description and name from db
   * @return true if successful / group exists
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean getBasicsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    if(this.id == 0) return false;
    Connection conn = DBConnector.getConnection();    
    List<ArrayList<String>> groupList = DBConnector.selectQuery(conn, 
        "SELECT *, (" 
            + "SELECT COUNT(*) FROM " + DBConnector.DATABASE + ".Members "
            + "WHERE Members.groupID = Groups.id) AS membercount "
            + "FROM " + DBConnector.DATABASE + ".Groups WHERE id=" + this.id);
    conn.close();
    if (groupList.size() == 1) return false;
    
    //fill up groupMap
    Map<String,String> groupMap = new HashMap<String,String>();
    ArrayList<String> keyRow = groupList.get(0);
    ArrayList<String> dataRow = groupList.get(1);
    for (int i = 0; i < keyRow.size(); i++) {
      groupMap.put(keyRow.get(i), dataRow.get(i));
    }
    
    //setting attributes
    this.name = groupMap.get("name");
    this.descr = groupMap.get("descr");
    this.memberCount = Integer.parseInt(groupMap.get("membercount"));
    return true;
  }
  
  /**
   * Register the group in the db by writing name and description
   * @return true if successful
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
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
  
  /**
   * Gets group object as json object
   * @return {"id":"groupID",...,"otherProperties":{...}}
   */
  public JsonValue getAsJson(){
    JsonObjectBuilder otherProperties = Json.createObjectBuilder();
    for (Entry<String, String> e : this.otherProperties.entrySet()) {
      if (e.getValue() == null) e.setValue("");
      otherProperties.add(e.getKey(), e.getValue());
    }
    if(this.descr == null) this.descr = "";
    if(this.name == null) this.name = "";
    JsonObject groupJson = Json.createObjectBuilder()
      .add("id", this.id)
      .add("name", this.name)
      .add("descr", this.descr)
      .add("otherProperties", otherProperties)
      .add("successful", true)
      .add("memberCount", this.memberCount)
      .build();
    return groupJson;
  }
  
  /**
   * Gets all groups in a Json list
   * @return {"groupList":[{"groupID":"groupID",...},...]}
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static String getAllAsJson() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> groupTable = DBConnector.selectQuery(conn, 
        "SELECT id,name,descr FROM " + DBConnector.DATABASE + ".Groups");
    groupTable.remove(0);
    JsonArrayBuilder groupJsonList = Json.createArrayBuilder();
    for (ArrayList<String> row : groupTable) {
      groupJsonList.add(Json.createObjectBuilder()
          .add("groupID", Integer.parseInt(row.get(0)))
          .add("groupName", row.get(1))
          .add("groupDescr", row.get(2)));
    }
    
    return String.valueOf(Json.createObjectBuilder()
        .add("groupList",groupJsonList)
        .add("successful", true)
        .build());
  }
  
  /**
   * Simple getter for id
   * @return id
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * Simple getter for name
   * @return
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Simple setter for name
   * @param name
   * @return this
   */
  public Group setName(String name) {
    this.name = name;
    return this;
  }
  
  /**
   * Simple getter for descr
   * @return descr
   */
  public String getDescr() {
    return this.descr;
  }

  /**
   * Simple setter for descr
   * @param descr
   * @return this
   */
  public Group setDescr(String descr) {
    this.descr = descr;
    return this;
  }

  /**
   * Gets all members from db
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void getMembersFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> membersTable = DBConnector.selectQuery(conn, 
        "SELECT Users.id,username,email,name,firstName FROM " + DBConnector.DATABASE + ".Members JOIN " 
            + DBConnector.DATABASE + ".Users ON Users.id=Members.memberID WHERE Members.groupID=" + this.id);
    conn.close();
    
    // fill up membersList
    List<HashMap<String, String>> membersList = new ArrayList<HashMap<String,String>>();
    ArrayList<String> keyRow = membersTable.get(0);
    membersTable.remove(0);
    for (ArrayList<String> data : membersTable){
      HashMap<String,String> userHelperMap = new HashMap<String,String>();
      for (int i = 0; i < keyRow.size(); i++){
        userHelperMap.put(keyRow.get(i), data.get(i));
      }
      membersList.add(userHelperMap);
    }
    
    for (HashMap<String, String> user : membersList){
      // add every User in membersList with the User(id, username, email, name, firstName) constructor
      this.members.add(new User(
          Integer.parseInt(user.get("id")), 
          user.get("username"), 
          user.get("email"), 
          user.get("name"), 
          user.get("firstName")));
    }
  }

  /**
   * Gets members as Json list
   * @return {"memberList":[{"id":"userID",...},...]}
   */
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
      .add("memberList", memberList)
      .add("name", this.name)
      .add("successful", true)
      .build();
    String jsonString = String.valueOf(membersObject);
    return jsonString;
  }
  
  /**
   * Check if a user is in the group
   * @param user
   * @return true if true (duh)
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean isMember(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    ArrayList<String> listWithUserId = new ArrayList<String>();
    listWithUserId.add(String.valueOf(user.getId()));
    if (DBConnector.selectQuery(conn, 
        "SELECT memberID FROM " + DBConnector.DATABASE + ".Members WHERE groupID=" + this.id).contains(listWithUserId)) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Inserts the members userID into the Members table
   * @param user
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void addMember(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.members.add(user);
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "INSERT INTO " + DBConnector.DATABASE + ".Members(groupID,memberID) VALUES(" + this.id + "," + user.getId() +")"); 
  }

  /**
   * Deletes a member from the Members table
   * @param user
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void removeMember(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "DELETE FROM " + DBConnector.DATABASE + ".Members WHERE memberID=" + user.getId() + " AND groupID=" + this.id);
    if (this.members.size() == 0) {
      this.getMembersFromDB();
    }
    if (this.members.size() == 0) {
      this.removeFromDB();
    }
  }

  /**
   * Simple getter for one Property of otherProperties
   * @param key
   * @return value
   */
  public String getOtherProperty(String key) {
    return this.otherProperties.get(key);
  }
  
  /**
   * Simple getter for otherProperties
   * @return  otherProperties
   */
  public Map<String,String> getOtherProperties() {
    return this.otherProperties;
  }

  /**
   * Simple setter for one Property of otherProperties
   * @param key
   * @param value
   */
  public void setOtherProperty(String key, String value) {
    this.otherProperties.put(key, value);
  }
  
  /**
   * Sets otherProperties and inserts them into db. If there is no column for a property, the method will create it.
   * @param properties
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void setOtherProperties(Map<String,String> properties) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.otherProperties.putAll(properties);
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> groupList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Groups WHERE id=" + this.id);
    String insertQueryHead = "INSERT INTO " + DBConnector.DATABASE + ".Groups(";
    String insertQueryTail = ") VALUES(";
    List<String> toBeAdded = new ArrayList<String>();
    
    for (Entry<String,String> prop: this.otherProperties.entrySet()) {
      // check if key is a column
      if (!groupList.get(0).contains(prop.getKey())) {
        toBeAdded.add(prop.getKey());
      }
      
      // prepare insert statement
      if (insertQueryHead.endsWith(".Groups(")) {
        insertQueryHead = insertQueryHead + prop.getKey();
      } else {
        insertQueryHead = insertQueryHead + "," + prop.getKey();
      }
      
      if (insertQueryTail.endsWith("VALUES(")) {
        insertQueryTail = insertQueryTail + "'" + prop.getValue() + "'";
      } else {
        insertQueryTail = insertQueryTail + ",'" + prop.getValue() + "'";
      }
    }
    
    if (toBeAdded.size() > 0) {
      // prepare alter table statement
      String alterTable = "ALTER TABLE " + DBConnector.DATABASE + ".Groups ADD COLUMN ";
      for (int i = 0; i < toBeAdded.size(); i++) {
        if (i == 0) {
          alterTable = toBeAdded.get(i) + " VARCHAR(45) NULL DEFAULT NULL";
        } else {
          alterTable = "," + toBeAdded.get(i) + " VARCHAR(45) NULL DEFAULT NULL";
        }
      }
      DBConnector.executeUpdate(conn, alterTable);
    }
    
    DBConnector.executeUpdate(conn, insertQueryHead + insertQueryTail + ")");
  }

  public List<Post> getPosts() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> postTable = DBConnector.selectQuery(conn, 
        "SELECT GroupPosts.id, title, content, visibility, date, Users.id, Users.email, Users.username, Users.name, Users.firstName " + DBConnector.DATABASE + ".GroupPosts "
        + "JOIN " + DBConnector.DATABASE + ".Users ON GroupPosts."
        + "WHERE ownerID="+ this.id);
    postTable.remove(0);
    for(ArrayList<String> row : postTable) {
      this.posts.add(new Post()
        .setId(Integer.parseInt(row.get(0)))
        .setTitle(row.get(1))
        .setContent(row.get(2))
        .setPrivatePost(Integer.parseInt(row.get(3)) != 0)
        .setOwner(this)
        .setPostDate(new Date(Long.parseLong(row.get(4)))) 
        .setAuthor(new User(
            Integer.parseInt(row.get(5)),
            row.get(6),
            row.get(7),
            row.get(8),
            row.get(9))));
    }
    return this.posts;
  }

  public Group addPost(Post post, User author) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    post.setOwner(new Group(0));
    post.setAuthor(author);
    post.setGroupPost(true);
    post.setPrivatePost(false);
    post.createInDB();
    return this;    
  }
  
  private int getMemberCount() {
    return this.memberCount;
  }
  
  public Group setMemberCount(int count) {
    this.memberCount = count;
    return this;
  }


}
