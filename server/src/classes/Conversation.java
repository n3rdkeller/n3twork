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

  /**
   * 
   * @param conList - Any list of conversations which have at least id and receiverList set
   * @return <pre><code>{
   *  "conversationList": [
   *    {
   *      "receiverList": [
   *        {
   *          "username":"",
   *          "firstName":"",
   *          "lastName":"",
   *          "email":"",
   *          "emailhash":""
   *        },
   *      ]
   *      "name":"",
   *      "id":0
   *    },
   *  ],
   *  "successful":true
   *}<code><pre>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public static JsonValue getConversationListAsJson(List<Conversation> conList) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder jsonConList = Json.createArrayBuilder();
    for(Conversation con: conList) {
      JsonArrayBuilder jsonReceiverList = Json.createArrayBuilder();
      for(User receiver : con.getReceivers()) {
        jsonReceiverList.add(Json.createObjectBuilder()
            .add("username", receiver.getUsername())
            .add("firstname", receiver.getFirstName())
            .add("lastname", receiver.getName())
            .add("email", receiver.getEmail())
            .add("emailhash", User.md5(receiver.getEmail())));
      }
      jsonConList.add(Json.createObjectBuilder()
          .add("receiverList", jsonReceiverList)
          .add("id", con.getID()));
      if(con.getName() != "") {
        jsonConList.add(con.getName());
      }
    }
    JsonObject jsonConObject = Json.createObjectBuilder()
        .add("conversationList", jsonConList)
        .add("successful", true)
        .build();
    return jsonConObject;
  }
  
  public Conversation sendMessage(Message message) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Messages(content, senderID) VALUES(?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    pStmt.setString(1, message.getContent());
    pStmt.setInt(2, message.getSender().getId());
    int id = pStmt.executeUpdate();
    sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receivers SET lastreadID = ?, deleted = 0 WHERE ";
    for(int i = 0; i < receivers.size(); i++) {
      if(i == receivers.size() - 1) {
        sqlQuery = sqlQuery + "username LIKE ?";
      } else {
        sqlQuery = sqlQuery + "username LIKE ? OR ";
      }
    }
    pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, id);
    for(int i = 0; i < receivers.size(); i++) {
      pStmt.setInt(i + 2, receivers.get(i).getId());
    }
    pStmt.execute();
    conn.close();
    return this;
  }

  public Conversation deleteConversation(User receiver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receivers SET deleted = 1 WHERE receiverID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, receiver.getId());
    pStmt.execute();
    conn.close();
    return this;
  }

  public Conversation readMessage(User receiver, Message lastMessage) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receivers SET lastreadID = ? WHERE receiverID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, lastMessage.getID());
    pStmt.setInt(2, receiver.getId());
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
