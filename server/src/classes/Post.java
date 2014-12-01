package classes;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Post {
  final static Logger log = LogManager.getLogger(Post.class);
  
  private int id;
  private User owner;
  private String content;
  private String title;
  private Date postDate;
  private Boolean visibility;
  private Map<User,Date> upVotes = new HashMap<User,Date>();

  /**
   * Simple constructor
   * @param owner Value for this.owner
   * @param title Value for this.title
   * @param visibility Value for this.visibility
   */
  public Post(User owner, String title, Boolean visibility) {
    this.owner = owner;
    this.title = title;
    this.visibility = visibility;
  }
  
  /**
   * Empty constructor
   */
  public Post(){
    //empty
  }
  
  /**
   * Converts any list of posts to a json string
   * @param postList any list of posts
   * @return <pre><code>{
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
  public static String convertPostListToJson(List<Post> postList) {
    JsonArrayBuilder jsonPostList = Json.createArrayBuilder();
    for(Post post: postList) {
      JsonArrayBuilder jsonUpVotes = Json.createArrayBuilder();
      for(Entry<User,Date> upVote : post.getUpVotes().entrySet()) {
        jsonUpVotes.add(Json.createObjectBuilder()
            .add("voter", upVote.getKey().getId())
            .add("date", upVote.getValue().getTime()));
      }
      jsonPostList.add(Json.createObjectBuilder()
          .add("id", post.getId())
          .add("owner", post.getOwner().getId())
          .add("title", post.getTitle())
          .add("content", post.getContent())
          .add("postDate", post.getPostDate().getTime())
          .add("visibility", post.getVisibility())
          .add("upVotes", jsonUpVotes));
    }
    JsonObject output = Json.createObjectBuilder()
        .add("postList", jsonPostList)
        .add("successful", true)
        .build();
    return String.valueOf(output);
  }
  
  /**
   * Sets standard values in db
   * @return true if successful
   */
  public Boolean createInDB() {
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
   * Simple getter for owner
   * @return this.owner
   */
  public User getOwner() {
    return this.owner;
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
    return this;
  }

  /**
   * Simple getter for visibility
   * @return this.visibility
   */
  public Boolean getVisibility() {
    return this.visibility;
  }

  /**
   * Simple setter for visibility
   * @param visibility New value for this.visibility
   * @return this
   */
  public Post setVisibility(Boolean visibility) {
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
    return this;
  }
}
