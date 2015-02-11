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
