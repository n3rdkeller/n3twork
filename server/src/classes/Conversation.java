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

/**
 * Implementation of the messaging system. A Conversation holds information of the conversation partners and the messages within.
 * @author johannes
 *
 */
public class Conversation {
  final static Logger log = LogManager.getLogger(Conversation.class);
  
  private int id;
  private List<Message> messageList = new ArrayList<Message>();
  private List<User> receivers = new ArrayList<User>();
  private Message lastRead;
  private int unread;
  private String name;

  /**
   * Empty Constructor
   */
  public Conversation() {
    //empty
  }

  /**
   * Converts any list of conversations to json
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
            .add("id", receiver.getId())
            .add("username", receiver.getUsername())
            .add("firstName", receiver.getFirstName())
            .add("lastName", receiver.getName())
            .add("email", receiver.getEmail())
            .add("emailhash", User.md5(receiver.getEmail())));
      }
      jsonConList.add(Json.createObjectBuilder()
          .add("receiverList", jsonReceiverList)
          .add("unreadCount", con.getUnread())
          .add("name", con.getName())
          .add("id", con.getID()));
    }
    JsonObject jsonConObject = Json.createObjectBuilder()
        .add("conversationList", jsonConList)
        .add("successful", true)
        .build();
    return jsonConObject;
  }
  
  /**
   * Gets the messages of a conversation as json
   * @return <pre><code>{
   *  "messageList": [
   *    {
   *      "id":0,
   *      "content":"asdf",
   *      "sendDate":00000000000,
   *      "senderID":0
   *    },
   *  ],
   *  "successful": true
   *}</code></pre>
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  public JsonValue getAsJson() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder jsonMessageList = Json.createArrayBuilder();
    for(Message message : this.getMessageList()) {
      jsonMessageList.add(Json.createObjectBuilder()
          .add("id", message.getID())
          .add("content", message.getContent())
          .add("sendDate", message.getSendDate().getTime())
          .add("senderID", message.getSender().getId()));
    }
    JsonObjectBuilder jsonCon = Json.createObjectBuilder()
        .add("messageList", jsonMessageList)
        .add("successful", true);
    return jsonCon.build();
  }
  
  /**
   * Gets messages of the conversation from db
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Conversation getConversationFromDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "SELECT Messages.senderID, Messages.id, Messages.content, Messages.date "
        + "FROM " + DBConnector.DATABASE + ".Messages WHERE conversationID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, this.getID());
    ResultSet messageTable = pStmt.executeQuery();
    while(messageTable.next()) {
      this.messageList.add(new Message()
      .setID(messageTable.getInt("id"))
      .setContent(messageTable.getString("content"))
      .setSendDate(messageTable.getTimestamp("date"))
      .setSender(new User(
          messageTable.getInt("senderID"))));
    }
    conn.close();
    return this;
  }
  
  /**
   * Add this conversation to the db. Only receivers need to be set.
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
      pStmt.setString(1, null);
    }
    pStmt.executeUpdate();
    ResultSet ids = pStmt.getGeneratedKeys();
    ids.next();
    this.setID(ids.getInt(1));
    sql = "INSERT INTO " + DBConnector.DATABASE + ".Receivers(conversationID, receiverID) VALUES(?,"
        + "(SELECT id FROM " + DBConnector.DATABASE + ".Users WHERE username = ?))";
    for(User receiver: this.getReceivers()) {
      pStmt = conn.prepareStatement(sql);
      pStmt.setInt(1, this.getID());
      pStmt.setString(2, receiver.getUsername());
      pStmt.execute();
    }
    conn.close();
    return this;
  }
  
  /**
   * Archive the conversation (Set the deleted flag in the db) for a specific receiver
   * @param receiver - user, who wants to archive the conversation
   * @return this
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
   * Saves a message to the db
   * @param message - content and id of the sender set
   * @return id of the message
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public int sendMessage(Message message) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "INSERT INTO " + DBConnector.DATABASE + ".Messages(content, senderID, conversationID) VALUES(?,?,?)";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    pStmt.setString(1, message.getContent());
    pStmt.setInt(2, message.getSender().getId());
    pStmt.setInt(3, this.getID());
    pStmt.executeUpdate();
    ResultSet ids = pStmt.getGeneratedKeys();
    ids.next();
    int id = ids.getInt(1);
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
    return id; 
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
  public Conversation readConversation(User receiver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sqlQuery = "UPDATE " + DBConnector.DATABASE + ".Receivers "
        + "SET lastreadID = (SELECT MAX(id) FROM `n3twork-dev`.Messages WHERE conversationID = ?) "
        + "WHERE receiverID = ? AND conversationID = ?";
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    pStmt.setInt(1, this.getID());
    pStmt.setInt(2, receiver.getId());
    pStmt.setInt(3, this.getID());
    pStmt.execute();
    conn.close();
    return this;
  }
  
  /**
   * Gets the number of unread conversations from the db for a specific user and returns it in a jsonValue
   * @param user - querying user
   * @return<pre><code>{
   *  "unread":0,
   *  "successful":true
   *}</code></pre>
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static JsonValue getUnreadConversations(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "SELECT COUNT(*) as unread FROM " + DBConnector.DATABASE + ".Receivers "
        + "WHERE receiverID = ? "
        + "AND lastreadID < (SELECT MAX(id) FROM " + DBConnector.DATABASE + ".Messages "
            + "WHERE conversationID = Receivers.conversationID)";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, user.getId());
    ResultSet unreadTable = pStmt.executeQuery();
    unreadTable.next();
    JsonValue jsonUnread = Json.createObjectBuilder()
        .add("unread", unreadTable.getInt("unread"))
        .add("successful", true)
        .build();
    return jsonUnread;
  }
  
  /**
   * Update conversation name in db
   * @param name
   * @return this
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public Conversation rename(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "UPDATE " + DBConnector.DATABASE + ".Conversations "
        + "SET name = ? WHERE id = ?";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setString(1, name);
    pStmt.setInt(2, this.getID());
    conn.close();
    return this;
  }

  /**
   * Simple setter for id
   * @param id
   * @return this
   */
  public Conversation setID(int id) {
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
   * Simple setter for messageList
   * @param messageList
   * @return this
   */
  public Conversation setMessageList(List<Message> messageList) {
    this.messageList = messageList;
    return this;
  }
  
  /** 
   * Simple getter for messageList
   * @return this.messageList
   */
  public List<Message> getMessageList() {
    return this.messageList;
  }
  
  /**
   * Simple setter for receivers
   * @param receivers
   * @return this
   */
  public Conversation setReceivers(List<User> receivers) {
    this.receivers = receivers;
    return this;
  }
  
  /**
   * Simple getter for receivers
   * @return this.receivers
   */
  public List<User> getReceivers() {
    return this.receivers;
  }
  
  /**
   * Simple setter for lastRead, the message last read by the spectating user.
   * @param lastRead
   * @return this
   */
  public Conversation setLastRead(Message lastRead) {
    this.lastRead = lastRead;
    return this;
  }
  
  /**
   * Simple getter for lastRead, the message last read by the spectating user.
   * @return this.lastRead
   */
  public Message getLastRead() {
    return this.lastRead;
  }
  
  /**
   * Simple setter for name
   * @param name
   * @return this
   */
  public Conversation setName(String name) {
    this.name = name;
    return this;
  }
  
  /**
   * Simple getter for name.
   * @return <code>""</code> if this.name is <code>null</code>, this.name otherwise
   */
  public String getName() {
    if(this.name == null) return "";
    return this.name;
  }
  
  /**
   * Simple setter for unread, the number of unread messages by the spectating user
   * @param unread
   * @return this
   */
  public Conversation setUnread(int unread) {
    this.unread = unread;
    return this;
  }
  
  /**
   * Simple getter for uread, the number of unread messages by the spectating user
   * @return this.unread
   */
  public int getUnread() {
    return this.unread;
  }
}
