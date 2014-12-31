package classes;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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

public class Message {
  final static Logger log = LogManager.getLogger(Message.class);
  
  private int id;
  private User sender;
  private List<User> reciever = new ArrayList<User>();
  private int numberOfRecievers;
  private String content;
  private Date sendDate;
  private Map<User,Boolean> readList = new HashMap<User,Boolean>();
  private Boolean read;

  public Message() {
    //empty
  }

  /**
   * 
   * @param messageList
   * @return<pre><code>{
   *  "messageList": [
   *    {
   *      "sender" : {
   *        "id":0,
   *        "username":"username",
   *        "lastname":"lastname",
   *        "firstname":"firstname",
   *        "email":"email",
   *        "emailhash":"emailhash"
   *      }
   *      "numberOfRecievers":0,
   *      "content":"content",
   *      "date":123412312123,
   *      "read":true,
   *      "id":0
   *    },
   *  ]
   *  "successful":true
   *}</code></pre>
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  public JsonValue convertMessageListToJson(List<Message> messageList) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder jsonMessageList = Json.createArrayBuilder();
    for(Message message: messageList) {
      if (message.getContent() == null) message.setContent("");
      JsonObjectBuilder jsonMessage = Json.createObjectBuilder()
          .add("sender", Json.createObjectBuilder()
              .add("id", message.getSender().getId())
              .add("username", message.getSender().getUsername())
              .add("lastname", message.getSender().getName())
              .add("firstname", message.getSender().getFirstName())
              .add("email", message.getSender().getEmail())
              .add("emailhash", User.md5(message.getSender().getEmail().toLowerCase())))
          .add("numberOfRecievers", message.getNumberOfRecievers())
          .add("content", message.getContent())
          .add("date", message.getSendDate().getTime())
          .add("read", message.getRead())
          .add("id", message.getID());
      jsonMessageList.add(jsonMessage);
    }
    JsonObject output = Json.createObjectBuilder()
        .add("messageList", jsonMessageList)
        .add("successful", true)
        .build();
    log.debug(output);
    return output;
  }
  
  public int getID() {
    return this.id;
  }
  
  public Message setID(int id) {
    this.id = id;
    return this;
  }
  public User getSender() {
    return this.sender;
  }
  
  public Message setSender(User sender) {
    this.sender = sender;
    return this;
  }

  public List<User> getReciever() {
    return this.reciever;
  }
  
  public Message setReciever(List<User> reciever) {
    this.reciever = reciever;
    return this;
  }
  
  public int getNumberOfRecievers() {
    return this.numberOfRecievers;
  }
  
  public Message setNumberOfRecievers( int numberOfRecievers) {
    this.numberOfRecievers = numberOfRecievers;
    return this;
  }

  public String getContent() {
    if (this.content == null) {
      return "";
    }
    return this.content;
  }
  
  public Message setContent(String content) {
    this.content = content;
    return this;
  }

  public Date getSendDate() {
    return this.sendDate;
  }
  
  public Message setSendDate(Date sendDate) {
    this.sendDate = sendDate;
    return this;
  }

  public Map<User,Boolean> getReadList() {
    return this.readList;
  }
  
  public Message setReadList(Map<User,Boolean> readList) {
    this.readList = readList;
    return this;
  }

  public Boolean getRead() {
    return read;
  }
 
  public Message setRead(User user) {
    for (Entry<User,Boolean> entry : this.readList.entrySet()) {
      if (entry.getKey().getId() == user.getId()) {
        entry.setValue(!entry.getValue());
      }
    }
    return this;
  }
  
  public Message setRead() {
    this.read = !this.read;
    return this;
  }
}
