package classes;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post {
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
  public Post setTitle(String tile) {
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
   * @param visi New value for this.visibility
   * @return this
   */
  public Post setVisibility(Boolean visi) {
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
   * @param voter User for new upVote
   * @return this
   */
  public Post addUpVote(User voter) {
    return this;
  }
}
