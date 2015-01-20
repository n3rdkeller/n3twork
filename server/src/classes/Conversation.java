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

import jersey.repackaged.org.objectweb.asm.Type;

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
  public static JsonValue convertConversationListToJson(List<Conversation> conList) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
      JsonObjectBuilder jsonCon = Json.createObjectBuilder()
          .add("receiverList", jsonReceiverList)
          .add("id", con.getID());
      if(con.getName() != "") {
        jsonCon.add("name", con.getName());
      }
      jsonConList.add(jsonCon);
    }
    JsonObject jsonConObject = Json.createObjectBuilder()
        .add("conversationList", jsonConList)
        .add("successful", true)
        .build();
    return jsonConObject;
  }
  
  /**
   * 
   * @return
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  public JsonValue getAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder jsonMessageList = Json.createArrayBuilder();
    for(Message message : this.getMessageList()) {
      jsonMessageList.add(Json.createObjectBuilder()
          .add("content", message.getContent())
          .add("sendDate", message.getSendDate().getTime())
          .add("senderID", message.getSender().getId())
          .add("read", message.getID() <= this.getLastRead().getID()));
    }
    JsonObjectBuilder jsonCon = Json.createObjectBuilder()
        .add("messageList", jsonMessageList)
        .add("successful", true);
    return jsonCon.build();
  }
  
  /**
   * 
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Conversation getConversationFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "SELECT Users.id as uid, Messages.id, Messages.content, Messages.date FROM " + DBConnector.DATABASE + ".Messages "
        + "JOIN Users ON Messages.senderID = Users.id WHERE conversationID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, this.getID());
    ResultSet messageTable = pStmt.executeQuery();
    List<Message> messageList = new ArrayList<Message>();
    while(messageTable.next()) {
      messageList.add(new Message()
      .setID(messageTable.getInt("id"))
      .setContent(messageTable.getString("content"))
      .setSendDate(messageTable.getTimestamp("date"))
      .setSender(new User(
          messageTable.getInt("uid"))));
    }
    conn.close();
    return this;
  }
  
  /**
   * 
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Conversation addConversationToDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "INSERT INTO " + DBConnector.DATABASE + ".Conversations(name) VALUE(?)";
    PreparedStatement pStmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
    if(this.getName() != "") {
      pStmt.setString(1, this.getName());
    } else {
      pStmt.setNull(1, Type.CHAR);
    }
    this.setID(pStmt.executeUpdate());
    sql = "INSERT INTO " + DBConnector.DATABASE + ".Receivers(conversationID, receiverID) VALUES(?,?)";
    for(User receiver: this.getReceivers()) {
      pStmt = conn.prepareStatement(sql);
      pStmt.setInt(1, this.getID());
      pStmt.setInt(2, receiver.getId());
      pStmt.execute();
    }
    conn.close();
    return this;
  }
  
  /**
   * 
   * @param receiver
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Conversation archiveConversation(User receiver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "UPDATE " + DBConnector.DATABASE + ".Receivers SET deleted = 1 WHERE receiverID = ? AND conversationID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, receiver.getId());
    pStmt.setInt(2, this.getID());
    pStmt.execute();
    return this;
  }
  
  /**
   * 
   * @param message - needs id, content, and id of the sender set
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Conversation sendMessage(Message message) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Messages(content, senderID) VALUES(?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    pStmt.setString(1, message.getContent());
    pStmt.setInt(2, message.getSender().getId());
    int id = pStmt.executeUpdate();
    sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receivers SET deleted = 0 WHERE conversationID = ?";
    pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.getID());
    pStmt.execute();
    sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receivers SET lastreadID = ? WHERE receiverID = ? AND conversationID = ?";
    pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, id);
    pStmt.setInt(2, message.getSender().getId());
    pStmt.setInt(3, this.getID());
    conn.close();
    return this; 
  }

  /**
   * Changes the lastreadID in the db for a specific receiver
   * @param receiver - only id has to be set
   * @param lastMessage - only id has to be set
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
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
