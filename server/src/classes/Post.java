package classes;
import java.sql.Connection;
import java.sql.SQLException;
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

public class Post {
  final static Logger log = LogManager.getLogger(Post.class);
  
  private int id;
  private Group owner;
  private User author;
  private String content;
  private String title;
  private Date postDate;
  private Boolean privatePost;
  private Boolean groupPost;
  private Map<User,Date> upVotes = new HashMap<User,Date>();

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
   *       "title":"title text",
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
   */
  public static JsonValue convertPostListToJson(List<Post> postList) {
    JsonArrayBuilder jsonPostList = Json.createArrayBuilder();
    for(Post post: postList) {
      JsonArrayBuilder jsonUpVotes = Json.createArrayBuilder();
      for(Entry<User,Date> upVote : post.getUpVotes().entrySet()) {
        jsonUpVotes.add(Json.createObjectBuilder()
            .add("voter", upVote.getKey().getId())
            .add("date", upVote.getValue().getTime()));
      }
      if (post.getTitle() == null) post.setTitle("");
      if (post.getContent() == null) post.setContent("");
      jsonPostList.add(Json.createObjectBuilder()
          .add("id", post.getId())
          .add("owner", post.getOwner().getAsJson())
          .add("author", post.getAuthor().getAsJson())
          .add("title", post.getTitle())
          .add("content", post.getContent())
          .add("postDate", post.getPostDate().getTime())
          .add("private", post.getPrivatePost())
          .add("upVotes", jsonUpVotes));
    }
    JsonObject output = Json.createObjectBuilder()
        .add("postList", jsonPostList)
        .add("successful", true)
        .build();
    return output;
  }

  /**
   * Sets standard values in db
   * @return true if successful
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Boolean createInDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    // int myInt = (myBoolean) ? 1 : 0
    Connection conn = DBConnector.getConnection();
    DBConnector.executeUpdate(conn, 
        "INSERT INTO " + DBConnector.DATABASE + ".Posts(ownerID,authorID,type,title,content,visibility) "
            + "VALUES(" + this.owner.getId() + "," 
            + this.author.getId() + ","
            + (this.groupPost) != null ? "1" : "0" + ",'" 
            + this.title + "','"
            + this.content + "',"
            + (this.privatePost) != null ? "1" : "0");
    return true;
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
   * @param id New Value for this.id
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
   * @param owner New Value for this.owner
   * @return this
   */
  public Post setOwner(Group owner) {
    this.owner = owner;
    return this;
  }
  /**
   * Simple getter for title
   * @return this.title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Simple setter for title
   * @param tile New value for this.title
   * @return this
   */
  public Post setTitle(String title) {
    this.title = title;
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
   * @param content New value for this.content
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
   * @param postDate New value for this.postDate
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
   * @param visibility New value for this.visibility
   * @return this
   */
  public Post setPrivatePost(Boolean privatePost) {
    this.privatePost = privatePost;
    return this;
  }
  
  public Boolean getGroupPost() {
    return this.groupPost;
  }
  
  public Post setGroupPost(Boolean groupPost) {
    this.groupPost = groupPost;
    return this;    
  }
  
  public User getAuthor() {
    return this.author;
  }
  
  public Post setAuthor(User author) {
    this.author = author;
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
   * Add a new up vote to upVotes
   * @param voter The voter only needs the User.id value
   * @return this
   */
  public Post addUpVote(User voter) {
    // TODO
    return this;
  }

}
