package classes;

public class Msg {
  private int id;
  private User sender;
  private List<User> reciever = new ArrayList<User>();
  private String subject;
  private Date sendDate;
  private Map<User,Boolean> read = new HashMap<User,Boolean>();

  public User getSender() {
    return null;
  }

  public List<User> getReciever() {
    return null;
  }

  public String getSubject() {
    return null;
  }

  public String getContent() {
    return null;
  }

  public Date getSendDate() {
    return null;
  }

  public Map<User,Boolean> getRead() {
    return null;
  }

  public void setRead(User user) {

  }
}
