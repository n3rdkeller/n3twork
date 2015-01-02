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
  private List<User> receiver = new ArrayList<User>();
  private int numberOfReceivers;
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
   *      "numberOfReceivers":0,
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
  public static JsonValue convertMessageListToJson(List<Message> messageList) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder jsonMessageList = Json.createArrayBuilder();
    List<User> senderList = new ArrayList<User>();
    for(Message message: messageList) {
      if(!senderList.contains(message.getSender())) {
        senderList.add(message.getSender());
      }
      JsonObjectBuilder jsonMessage = Json.createObjectBuilder()
          .add("sender", Json.createObjectBuilder()
              .add("id", message.getSender().getId())
              .add("username", message.getSender().getUsername())
              .add("lastname", message.getSender().getName())
              .add("firstname", message.getSender().getFirstName())
              .add("email", message.getSender().getEmail())
              .add("emailhash", User.md5(message.getSender().getEmail().toLowerCase())))
          .add("numberOfReceivers", message.getNumberOfReceivers())
          .add("content", message.getContent())
          .add("date", message.getSendDate().getTime())
          .add("read", message.getRead())
          .add("id", message.getID());
      jsonMessageList.add(jsonMessage);
    }
    JsonArrayBuilder jsonSender = Json.createArrayBuilder();
    for(User sender: senderList) {
      jsonSender.add(Json.createObjectBuilder()
          .add("id", sender.getId())
          .add("username", sender.getUsername())
          .add("lastname", sender.getName())
          .add("firstname", sender.getFirstName())
          .add("email", sender.getEmail())
          .add("emailhash", User.md5(sender.getEmail())));
    }
    JsonObject output = Json.createObjectBuilder()
        .add("messageList", jsonMessageList)
        .add("senderList", jsonSender)
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

  public List<User> getReceiver() {
    return this.receiver;
  }
  
  public Message setReceiver(List<User> receiver) {
    this.receiver = receiver;
    return this;
  }
  
  public int getNumberOfReceivers() {
    return this.numberOfReceivers;
  }
  
  public Message setNumberOfReceivers( int numberOfReceivers) {
    this.numberOfReceivers = numberOfReceivers;
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
  
  public Message setRead(Boolean read) {
    this.read = read;
    return this;
  }
}
