package classes;

import java.util.AbstractMap.SimpleEntry;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Objects of this class represents posts of users on their profil or in a group. 
 * Posts have an owner and an author, where the owner is a group and the author is a user. 
 * If the user didn't post within a group the flag <code>groupPost</code> is false. 
 * @author johannes
 *
 */
public class Post {
  final static Logger log = LogManager.getLogger(Post.class);
  
  private int id;
  private Group owner;
  private User author;
  private String content;
  private Date postDate;
  private Boolean privatePost;
  private Boolean groupPost;
  private Map<User,Date> upVotes = new HashMap<User,Date>();
  private int numberOfUpVotes;
  private Boolean didIVote;
  private Map<SimpleEntry<User,Integer>,SimpleEntry<String,Date>> comments = new HashMap<SimpleEntry<User,Integer>,SimpleEntry<String,Date>>();
  private int numberOfComments;

  /**
   * Empty constructor
   */
  public Post(){
    //empty
  }
  
  /**
   * Converts any list of posts to a json string
   * @param postList any list of posts
   * @return <pre><code> {
   *   "postList": [
   *     {
   *       "content":"content text",
   *       "id":postID number,
   *       "owner":owenerID number,
   *       "postDate":timestamp number,
   *       "upVotes": [
   *         {
   *           "date":timestamp number,
   *           "voter":voterID number
   *         },
   *       ],
   *       "visibility":true/false
   *     },
   *   ],
   *   "successful":true
   * } </code></pre>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public static JsonValue convertPostListToJson(List<Post> postList, Boolean newsfeed) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder jsonPostList = Json.createArrayBuilder();
    for(Post post: postList) {
      if (post.getContent() == null) post.setContent("");
      JsonObjectBuilder jsonPost = Json.createObjectBuilder()
          .add("id", post.getId())
          .add("content", post.getContent())
          .add("postDate", post.getPostDate().getTime())
          .add("private", post.getPrivatePost())
          .add("numberOfVotes", post.getNumberOfUpVotes())
          .add("didIVote", post.getDidIVote())
          .add("numberOfComments", post.getNumberOfComments());
      if (newsfeed) {
        jsonPost
            .add("owner", Json.createObjectBuilder()
                .add("id", post.getOwner().getId())
                .add("name", (post.getOwner().getName() == null) ? "" : post.getOwner().getName())
                .add("descr", (post.getOwner().getDescr() == null) ? "" : post.getOwner().getDescr())
                .add("membercount", post.getOwner().getMemberCount()))
            .add("author", Json.createObjectBuilder()
              .add("id", post.getAuthor().getId())
              .add("username", post.getAuthor().getUsername())
              .add("lastName", post.getAuthor().getName())
              .add("firstName", post.getAuthor().getFirstName())
              .add("email", post.getAuthor().getEmail())
              .add("emailhash", User.md5(post.getAuthor().getEmail().toLowerCase())));
      }      
      jsonPostList.add(jsonPost);
    }
    JsonObject output = Json.createObjectBuilder()
        .add("postList", jsonPostList)
        .add("successful", true)
        .build();
    return output;
  }

  /**
   * Inserts a post with ownerID, authorID, content, and privacy status into db. 
   * this.author and this.owner with at least the id value, this.content and this.privatePost needs to be given.
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void createInDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    int privatePost = (this.privatePost) ? 1 : 0;
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Posts(ownerID,authorID,content,visibility) "
        + "VALUES(?,?,?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1,this.owner.getId());
    pStmt.setInt(2, this.author.getId());
    pStmt.setString(3, this.content);
    pStmt.setInt(4, privatePost);
    pStmt.execute();
  }
  
  /**
   * Deletes this object from the db
   * @return false if this.id is not given
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean deleteFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    if (this.id == 0) return false;
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "DELETE FROM " + DBConnector.DATABASE + ".Posts "
        + "WHERE id=" + this.id);
    return true;
  }
  
  /**
   * Updates content or privacy status of this post. If a attribute wasn't changed, it must have the value null.
   * @return false if no changes where made
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Boolean updateDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    String sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Posts SET ";
    List<Object> valueList = new ArrayList<Object>();

    if (this.content != null) {
      valueList.add(this.content);
      sqlQuery = sqlQuery + "content=?";
    }
    if (this.privatePost != null) {
      valueList.add((this.privatePost) ? 1 : 0);
      if (sqlQuery.endsWith("SET ")) {
        sqlQuery = sqlQuery + "visibility=?";
      } else {
        sqlQuery = sqlQuery + ",visibility=?";
      }
    }
    if (valueList.size() > 0) {
      Connection conn = DBConnector.getConnection();
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery + " WHERE id=" + this.id);
      for (int i = 0; i < valueList.size(); i++) {
        pStmt.setObject(i + 1, valueList.get(i));
      }
      pStmt.execute();
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Simple getter for id
   * @return this.id
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * Simple setter for id
   * @param id - New Value for this.id
   * @return this
   */
  public Post setId(int id) {
    this.id = id;
    return this;
  }
  
  /**
   * Simple getter for owner
   * @return this.owner
   */
  public Group getOwner() {
    return this.owner;
  }

  /**
   * Simple setter for owner
   * @param owner - New Value for this.owner
   * @return this
   */
  public Post setOwner(Group owner) {
    this.owner = owner;
    return this;
  }

  /**
   * Simple getter for content
   * @return this.content
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Simple setter for content
   * @param content - New value for this.content
   * @return this
   */
  public Post setContent(String content) {
    this.content = content;
    return this;
  }

  /**
   * Simple getter for postDate
   * @return this.postDate
   */
  public Date getPostDate() {
    return this.postDate;
  }

  /**
   * Simple setter for postDate
   * @param postDate - New value for this.postDate
   * @return this
   */
  public Post setPostDate(Date postDate) {
    this.postDate = postDate;
    return this;
  }

  /**
   * Simple getter for visibility
   * @return this.visibility
   */
  public Boolean getPrivatePost() {
    return this.privatePost;
  }

  /**
   * Simple setter for visibility
   * @param visibility - New value for this.visibility
   * @return this
   */
  public Post setPrivatePost(Boolean privatePost) {
    this.privatePost = privatePost;
    return this;
  }
  
  /**
   * Simple getter for the flag groupPost
   * @return this.groupPost
   */
  public Boolean getGroupPost() {
    return this.groupPost;
  }
  
  /** 
   * Simple setter for groupPost
   * @param groupPost - New value for this.groupPost
   * @return this
   */
  public Post setGroupPost(Boolean groupPost) {
    this.groupPost = groupPost;
    return this;    
  }
  
  /**
   * Simple getter for author
   * @return this.author
   */
  public User getAuthor() {
    return this.author;
  }
  
  /**
   * Simple setter for author
   * @param author - New value for this.author
   * @return this
   */
  public Post setAuthor(User author) {
    this.author = author;
    return this;
  }
  
  /**
   * Simple getter for numberOfUpVotes
   * @return this.numberOfUpVotes
   */
  public int getNumberOfUpVotes() {
    return this.numberOfUpVotes;
  }
  
  /**
   * Simple setter for numberOfUpVotes
   * @param votesNum - New value for this.numberOfUpVotes
   * @return
   */
  public Post setNumberOfUpVotes(int votesNum) {
    this.numberOfUpVotes = votesNum;
    return this;
  }
  
  /**
   * Simple getter for didIVote. <code>didIVote is used in relation to the spectateing user</code>
   * @return this.didIVote
   */
  public Boolean getDidIVote() {
    return this.didIVote;
  }
  
  /**
   * Simple setter for didIVote. <code>didIVote is used in relation to the spectateing user</code>
   * @param didIVote - New value for this.didIVote
   * @return this
   */
  public Post setDidIVote(Boolean didIVote) {
    this.didIVote = didIVote;
    return this;
  }
  
  /**
   * Simple getter for upVotes
   * @return this.upVotes
   */
  public Map<User,Date> getUpVotes() {
    return this.upVotes;
  }

  /**
   * Gets all upVotes of <code>this</code> from the db
   * @return this
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Post getUpVotesFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "Select Users.username, Users.name, Users.firstName, Votes.date From " + DBConnector.DATABASE + ".Votes "
          + "JOIN " + DBConnector.DATABASE + ".Posts ON Posts.id = Votes.postID "
          + "JOIN " + DBConnector.DATABASE + ".Users ON Users.id = Votes.voterID "
          + "WHERE Posts.id = " + this.id;
    log.debug(sqlQuery);
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    ResultSet votesTable = pStmt.executeQuery();
    while (votesTable.next()) {
      this.upVotes.put(new User()
        .setUsername(votesTable.getString("username"))
        .setName(votesTable.getString("name"))
        .setFirstName(votesTable.getString("firstName")), 
        votesTable.getTimestamp("date"));
    }
    pStmt.close();
    votesTable.close();
    conn.close();
    return this;
  }
  
  /**
   * Gets upVotes as Json
   * @return <pre><code> {
   *   "voteList": [
   *     {
   *       "date":voteDate number,
   *       "voter":{
   *         "firstName":firstName text,
   *         "name":name text,
   *         "username":username text
   *       }
   *     },
   *   ],
   *   "successful":true
   * } </code></pre>
   */
  public JsonValue getUpVotesAsJson() {
    JsonArrayBuilder voteList = Json.createArrayBuilder();
    for (Entry<User,Date> upVote : this.upVotes.entrySet()) {
      voteList.add(Json.createObjectBuilder()
          .add("date", upVote.getValue().getTime())
          .add("voter", Json.createObjectBuilder()
              .add("username", upVote.getKey().getUsername())
              .add("lastName", upVote.getKey().getName())
              .add("firstName", upVote.getKey().getFirstName()))
          );
    }
    JsonObject voteUps = Json.createObjectBuilder()
        .add("voteList", voteList)
        .add("successful", true)
        .build();
    return voteUps;
  }
  
  /**
   * Add a new up vote to upVotes
   * @param voter - The voter only needs the User.id attribute set
   * @return this
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Post addUpVote(User voter) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Votes(postID,voterID) VALUES(?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    pStmt.setInt(2, voter.getId());
    pStmt.execute();
    return this;
  }
  
  /**
   * Remove a vote from the upVotes
   * @param voter
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Post removeUpVote(User voter) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "DELETE FROM " + DBConnector.DATABASE + ".Votes WHERE postID=? AND voterID=?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    pStmt.setInt(2, voter.getId());
    pStmt.execute();
    return this;
  }

  /**
   * Simple getter for numberOfComments
   * @return this.numberOfComments
   */
  public int getNumberOfComments() {
    return this.numberOfComments;
  }
  
  /**
   * Simple setter for numberOfComments
   * @param noc - New value for this.numberOfComments
   * @return this
   */
  public Post setNumberOfComments(int noc) {
    this.numberOfComments = noc;
    return this;
  }
  
  /**
   * Simple getter for comments. Comments should've been a seperate class
   * <UL>
   * <LI>the key of the key is the commentator 
   * <LI>the value of the key is the comment id (from `Comments` table in db) 
   * <LI>the key of the value is the content 
   * <LI>the value of the key is the date of creation
   * </UL>
   * @return this.comments
   */
  public Map<SimpleEntry<User,Integer>,SimpleEntry<String,Date>> getComments() {
    return this.comments;
  }
  
  /**
   * Gets all comments of this from the db. 
   * @return this
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Post getCommentsFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "Select Users.id as userid, Users.username, Users.name, Users.firstName, Users.email, Comments.content, Comments.date, Comments.id From " + DBConnector.DATABASE + ".Comments "
          + "JOIN " + DBConnector.DATABASE + ".Posts ON Posts.id = Comments.postID "
          + "JOIN " + DBConnector.DATABASE + ".Users ON Users.id = Comments.authorID "
          + "WHERE Posts.id = " + this.id;
    log.debug(sqlQuery);
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    ResultSet commentsTable = pStmt.executeQuery();
    while (commentsTable.next()) {
      this.comments.put(new SimpleEntry<User,Integer>(
          new User(commentsTable.getInt("userid"),
                   commentsTable.getString("username"),
                   commentsTable.getString("email"),
                   commentsTable.getString("name"),
                   commentsTable.getString("firstName")),
        commentsTable.getInt("id")), 
        new SimpleEntry<String,Date>(
            commentsTable.getString("content"),
            commentsTable.getTimestamp("date")));
    }
    pStmt.close();
    commentsTable.close();
    conn.close();
    return this;
  }
  
  /**
   * Gets comments as Json
   * @return <pre><code> {
   *   "commentList": [
   *     {
   *       "id":commentID,
   *       "date":commentDate number,
   *       "author":{
   *         "firstName":"firstName text",
   *         "lastName":"name text",
   *         "username":"username text"
   *       },
   *       "content":"content text"
   *     },
   *   ],
   *   "successful":true
   * } </code></pre>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public JsonValue getCommentsAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder commentList = Json.createArrayBuilder();
    for (Entry<SimpleEntry<User,Integer>,SimpleEntry<String,Date>> comment : this.comments.entrySet()) {
      commentList.add(Json.createObjectBuilder()
          .add("date", comment.getValue().getValue().getTime())
          .add("author", Json.createObjectBuilder()
              .add("id", comment.getKey().getKey().getId())
              .add("username", comment.getKey().getKey().getUsername())
              .add("lastName", comment.getKey().getKey().getName())
              .add("firstName", comment.getKey().getKey().getFirstName())
              .add("emailhash", User.md5(comment.getKey().getKey().getEmail().toLowerCase())))
          .add("content", comment.getValue().getKey())
          .add("id", comment.getKey().getValue())
          );
    }
    JsonObject voteUps = Json.createObjectBuilder()
        .add("commentList", commentList)
        .add("successful", true)
        .build();
    return voteUps;
  }
  
  /**
   * Add a new comment to comments
   * @param author The author only needs the User.id value
   * @param content any String
   * @return this
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Post addComment(User author, String content) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Comments(postID,authorID,content) VALUES(?,?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.id);
    pStmt.setInt(2, author.getId());
    pStmt.setString(3, content);
    pStmt.execute();
    return this;
  }
  
  /**
   * Remove a comment. Uses 1 select and 1 delete query
   * @param commentID
   * @param user - the spectating user. Needed to check, if he is allowed to delete the comment. A comment can be deleted by its author or the author of the post.
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Post removeComment(int commentID, User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    List<ArrayList<String>> idList = DBConnector.selectQuery(conn, "SELECT Posts.authorID FROM " + DBConnector.DATABASE + ".Posts "
        + "JOIN `n3twork-dev`.Comments on Comments.postID=Posts.id WHERE Comments.id=" + commentID);
    if(idList.size() == 2 && idList.get(1).contains(Integer.toString(user.getId()))) {
      String sqlQuery = "DELETE FROM " + DBConnector.DATABASE + ".Comments WHERE id=?";
      PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setInt(1, commentID);
      pStmt.execute();
    }
    String sqlQuery = "DELETE FROM " + DBConnector.DATABASE + ".Comments WHERE id=? AND authorID=?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, commentID);
    pStmt.setInt(2, user.getId());
    pStmt.execute();
    conn.close();
    return this;
  }
}