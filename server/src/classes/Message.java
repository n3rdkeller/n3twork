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

/**
 * Message within a Conversation. Used to easier save important data of a message.
 * @author johannes
 * @see Conversation
 */
public class Message {
  final static Logger log = LogManager.getLogger(Message.class);
  
  private int id;
  private String content;
  private Date sendDate;
  private User sender;
  private Boolean read;

  /**
   * Simple setter for id
   * @param id
   * @return this
   */
  public Message setID(int id) {
    this.id = id;
    return this;
  }
  
  /**
   * Simple getter for id
   * @return this.id
   */
  public int getID() {
    return this.id;
  }
  
  /**
   * Simple setter for content
   * @param content
   * @return this
   */
  public Message setContent(String content) {
    this.content = content;
    return this;
  }
  
  /**
   * Simple getter for content
   * @return "" if this.content is null, this.content otherwise
   */
  public String getContent() {
    if(this.content == null) return "";
    return this.content;
  }
  
  /**
   * Simple setter for sendDate
   * @param sendDate
   * @return this
   */ 
  public Message setSendDate(Date sendDate) {
    this.sendDate = sendDate;
    return this;
  }
  
  /**
   * Simple getter for sendDate
   * @return this
   */
  public Date getSendDate() {
    return this.sendDate;
  }
  
  /**
   * Simple setter for sender
   * @param sender
   * @return this
   */
  public Message setSender(User sender) { 
    this.sender = sender;
    return this;
  }
  
  /**
   * Simple getter for sender
   * @return this.sender
   */
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
