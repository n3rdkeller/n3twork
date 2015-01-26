package classes;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
  private String content;
  private Date sendDate;
  private User sender;
  private Boolean read;
  /**
   * 
   * @param messageList
   * @return<pre><code>{
   *  "messageList": [
   *    {
   *      "sender" : {
   *        "id":0,
   *        "username":"username",
   *        "lastName":"lastName",
   *        "firstName":"firstName",
   *        "email":"email",
   *        "emailhash":"emailhash"
   *      }
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
              .add("lastName", message.getSender().getName())
              .add("firstName", message.getSender().getFirstName())
              .add("email", message.getSender().getEmail())
              .add("emailhash", User.md5(message.getSender().getEmail().toLowerCase())))
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

  public Message setID(int id) {
    this.id = id;
    return this;
  }
  
  public int getID() {
    return this.id;
  }
  
  public Message setContent(String content) {
    this.content = content;
    return this;
  }
  
  public String getContent() {
    if(this.content == null) return "";
    return this.content;
  }
  
  public Message setSendDate(Date sendDate) {
    this.sendDate = sendDate;
    return this;
  }
  
  public Date getSendDate() {
    return this.sendDate;
  }
  
  public Message setSender(User sender) { 
    this.sender = sender;
    return this;
  }
  
  public User getSender() { 
    return this.sender;
  }
  
  public Message setRead(Boolean read) {
    this.read = read;
    return this;
  }
  
  public Boolean getRead() {
    if(this.read == null) return true;
    return this.read;
  }
}
