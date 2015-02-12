package classes;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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

/**
 * An object of the class represents a group.
 * @author johannes
 *
 */
public class Group {
  final static Logger log = LogManager.getLogger(Group.class);
  
  private int id;
  private String name = "";
  private String descr = "";
  private List<User> members = new ArrayList<User>();
  private Map<String,String> otherProperties = new HashMap<String,String>();
  private List<Post> posts = new ArrayList<Post>();
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
  public static List<Group> findGroup() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<Group> groupList = new ArrayList<Group>();
    List<ArrayList<String>> groupTable = DBConnector.selectQuery(conn, 
        "SELECT Groups.id,name,descr, (" 
            + "SELECT COUNT(*) FROM " + DBConnector.DATABASE + ".Members "
            + "WHERE Members.groupID = Groups.id) as membercount "
            + "FROM " + DBConnector.DATABASE + ".Groups "
            + "WHERE Groups.id != 0");
    groupTable.remove(0); // remove column names
    for (ArrayList<String> groupTableRow : groupTable) {
        Group group = new Group(
          Integer.parseInt(groupTableRow.get(0)))
          .setName(groupTableRow.get(1))
          .setDescr(groupTableRow.get(2))
          .setMemberCount(Integer.parseInt(groupTableRow.get(3)));
        groupList.add(group);
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
      String sqlQuery = "DELETE FROM " + DBConnector.DATABASE + ".Groups WHERE id=?";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setInt(1, this.id);
      pStmt.execute();
      log.debug(pStmt);
      pStmt.close();
      conn.close();
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
    String sqlQuery = "SELECT *, (" 
        + "SELECT COUNT(*) FROM " + DBConnector.DATABASE + ".Members "
        + "WHERE Members.groupID = Groups.id) AS membercount "
        + "FROM " + DBConnector.DATABASE + ".Groups WHERE id=?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    ResultSet groupTable = pStmt.executeQuery();

    if (!groupTable.next()) return false;
    //setting attributes
    this.name = groupTable.getString("name");
    this.descr = groupTable.getString("descr");
    this.memberCount = groupTable.getInt("membercount");
    groupTable.close();
    pStmt.close();
    conn.close();
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
    ResultSet groupList;
    
    if(this.name.equals("")) { // name not given
      log.debug("group name is not given");
      return false;
      
    } else {
      log.debug("checking for group by name: " + this.name);
      String sqlQuery = "SELECT * FROM " + DBConnector.DATABASE + ".Groups WHERE name=?";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setString(1, this.name);
      log.debug(pStmt);
      groupList = pStmt.executeQuery();
    }
    
    if (!groupList.next()) {
      String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Groups(name,descr) VALUES(?,?)";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery,Statement.RETURN_GENERATED_KEYS);
      pStmt.setString(1, this.name);
      pStmt.setString(2, this.descr);
      pStmt.executeUpdate();
      ResultSet ids = pStmt.getGeneratedKeys();
      if (ids.next()) this.id = ids.getInt(1);
      groupList.close();
      ids.close();
      pStmt.close();
      conn.close();
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
    if (this.name == null) return "";
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
    if (this.descr == null) return "";
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
    String sqlQuery = "SELECT Users.id,username,email,name,firstName FROM " + DBConnector.DATABASE + ".Members JOIN " 
        + DBConnector.DATABASE + ".Users ON Users.id=Members.memberID WHERE Members.groupID=?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    ResultSet membersTable = pStmt.executeQuery();
    
    while (membersTable.next()){
      // add every User in memberTable with the User(id, username, email, name, firstName) constructor
      this.members.add(new User(
          membersTable.getInt("id"), 
          membersTable.getString("username"), 
          membersTable.getString("email"), 
          membersTable.getString("name"), 
          membersTable.getString("firstName")));
    }
    conn.close();
  }

  /**
   * Gets members as Json list
   * @return {"memberList":[{"id":"userID",...},...]}
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public String getMembersAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder memberList = Json.createArrayBuilder();
    for (User member : this.members) {
      memberList.add(Json.createObjectBuilder()
          .add("id", member.getId())
          .add("username", member.getUsername())
          .add("email", member.getEmail())
          .add("emailhash", User.md5(member.getEmail().toLowerCase()))
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
  public Group setOtherProperties(Map<String,String> properties) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    this.otherProperties.putAll(properties);
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> groupList = DBConnector.selectQuery(conn, 
        "SELECT * FROM " + DBConnector.DATABASE + ".Groups WHERE id=" + this.id);
    String updateQueryHead = "UPDATE " + DBConnector.DATABASE + ".Groups SET ";
    List<String> values = new ArrayList<String>();
    List<String> keys = new ArrayList<String>();
    for (Entry<String,String> prop: this.otherProperties.entrySet()) {
      // check if key is a column
      if (groupList.get(0).contains(prop.getKey())) {
        // prepare insert statement
        if (updateQueryHead.endsWith("SET ")) {
          updateQueryHead = updateQueryHead + "`" + prop.getKey() + "`=?";
        } else {
          updateQueryHead = updateQueryHead + ",`" + prop.getKey() + "`=?";
        }
        values.add(prop.getValue());
        keys.add(prop.getKey());
      }
    }
    if (!updateQueryHead.endsWith("SET ")) {
      String updateQuery = updateQueryHead + " WHERE id=" + this.id;
      PreparedStatement pStmt = conn.prepareStatement(updateQuery);
      for (int i = 0; i < keys.size(); i++) {
        pStmt.setString(i + 1, values.get(i));
      }
      log.debug(pStmt);
      pStmt.execute();
    }
    return this;
  }

  /**
   * Gets all posts in the group from the db
   * @param lookingUser - checks if they voted on a post
   * @return this.posts
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public List<Post> getPosts(User lookingUser) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "SELECT Posts.id as postid, content, visibility, date, Users.id, Users.email, Users.username, Users.name, Users.firstName, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.postID = Posts.id) as votes, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Comments WHERE Comments.postID = Posts.id) as comments, "
        + "(SELECT count(*) FROM " + DBConnector.DATABASE + ".Votes WHERE Votes.voterID = ? AND Votes.postID = Posts.id) as didIVote "
        + "FROM " + DBConnector.DATABASE + ".Posts "
        + "JOIN " + DBConnector.DATABASE + ".Users ON Posts.authorID = Users.id "
        + "WHERE ownerID=?";
    log.debug(sqlQuery);
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, lookingUser.getId());
    pStmt.setInt(2, this.id);
    ResultSet postsTable = pStmt.executeQuery();
    while (postsTable.next()) {
      this.posts.add(new Post()
        .setId(postsTable.getInt("postid"))
        .setContent(postsTable.getString("content"))
        .setPrivatePost(postsTable.getBoolean("visibility"))
        .setOwner(this)
        .setPostDate(postsTable.getTimestamp("date")) 
        .setAuthor(new User(
            postsTable.getInt("id"),
            postsTable.getString("username"),
            postsTable.getString("email"),
            postsTable.getString("name"),
            postsTable.getString("firstName")))
        .setNumberOfUpVotes(postsTable.getInt("votes"))
        .setDidIVote(postsTable.getBoolean("didIVote"))
        .setNumberOfComments(postsTable.getInt("comments"))
        );
    };
    return this.posts;
  }

  /**
   * Adds a new post to the group in the db.
   * @param post - author and owner don't need to be set
   * @param author
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Group addPost(Post post, User author) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    post.setOwner(this);
    post.setAuthor(author);
    post.setGroupPost(true);
    post.setPrivatePost(false);
    post.createInDB();
    return this;    
  }
  
  /**
   * Simple getter for memberCount
   * @return this.memberCount
   */
  public int getMemberCount() {
    return this.memberCount;
  }
  
  /**
   * Simple setter for memberCount
   * @param count - New Value for this.memberCount
   * @return
   */
  public Group setMemberCount(int count) {
    this.memberCount = count;
    return this;
  }
}
