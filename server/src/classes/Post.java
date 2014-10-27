package classes;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post {
  private int id;
  private User owner;
  private String content;
  private Date postDate;
  private Boolean visibility;
  private Map<User,Date> upVotes = new HashMap<User,Date>();

  public Post(User owner, String title, Boolean visibility) {
    this.owner = owner;
    this.title = title;
    this.visibility = visibility;
  }

  public User getOwner() {
    return null;
  }

  public String getTitle() {
    return null;
  }

  public void setTitle(String tile) {

  }

  public String getContent() {
    return null;
  }

  public void setContent(String content) {

  }

  public Date getPostDate() {
    return null;
  }

  private void setPostDate() {

  }

  public Boolean getVisibility() {
    return null;
  }

  public void setVisibility(Boolean visi) {

  }

  public Map<User,Date> getUpVotes() {
    return null;
  }

  public void addUpVote(User voter) {

  }
}
