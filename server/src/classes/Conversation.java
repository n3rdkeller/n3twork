package classes;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class Conversation {
  final static Logger log = LogManager.getLogger(Conversation.class);
  
  private int id;
  private List<Message> messageList = new ArrayList<Message>();
  private List<User> receivers = new ArrayList<User>();
  private Message lastRead;
  private String name;

  public Conversation() {
    //empty
  }

  public Conversation sendMessage(Message message) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Messages(content, authorID) VALUES(?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    pStmt.setString(1, message.getContent());
    pStmt.setInt(2, message.getSender().getId());
    int id = pStmt.executeUpdate();
    for(User receiver: this.getReceivers()) {
      sqlQuery = "SELECT id FROM " + DBConnector.DATABASE + ".Users WHERE username LIKE ?";
      pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setString(1, receiver.getUsername());
      ResultSet idTable = pStmt.executeQuery();
      idTable.next();
      int receiverID = idTable.getInt("id");
      sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Receiver(messageID,receiverID) VALUES(?,?)";
      pStmt = conn.prepareStatement(sqlQuery);
      pStmt.setInt(1, id);
      pStmt.setInt(2, receiverID);
      pStmt.execute();
    }
    conn.close();
    return this;
  }

  public Message deleteMessage(User receiver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receiver SET deleted = 1 WHERE receiverID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, receiver.getId());
    pStmt.execute();
    conn.close();
    return this;
  }

  public Message readMessage(User receiver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receiver SET read = 1 WHERE receiverID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, receiver.getId());
    pStmt.execute();
    conn.close();
    return this;
  }

  public Conversation setID(int id) {
    this.id = id;
    return this;
  }
  
  public int getID() {
    return this.id;
  }

  public Conversation setMessageList(List<Message> messageList) {
    this.messageList = messageList;
    return this;
  }
  
  public List<Message> getMessageList() {
    return this.messageList;
  }
  
  public Conversation setReceivers(List<User> receivers) {
    this.receivers = receivers;
    return this;
  }
  
  public List<User> getReceivers() {
    return this.receivers;
  }
  
  public Conversation setLastRead(Message lastRead) {
    this.lastRead = lastRead;
    return this;
  }
  
  public Message getLastRead() {
    return this.lastRead;
  }
  
  public Conversation setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getName() {
    if(this.name == null) return "";
    return this.name;
  }
}
