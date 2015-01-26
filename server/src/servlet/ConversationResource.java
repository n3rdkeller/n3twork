/**
 * 
 */
package servlet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.Conversation;
import classes.Group;
import classes.Post;
import classes.User;
import classes.Message;

/**
 * @author johannes
 *
 */
@Path("/conversation")
public class ConversationResource {
  final static Logger log = LogManager.getLogger(PostResource.class);
  
  /**
   * Options request for / (All options requests are identical, therefore all following are without docstring)
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/")
  public Response corsShowConversations() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post request to get messages
   * @param jsonInput <pre><code>{
   *  "session":"sessionID"
   *}
   * @return  <pre><code>{
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
   */
  @POST @Path("/")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showConversations(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation returns:" + entity);
        return Helper.okResponse(entity);
      } 
      String entity = String.valueOf(Conversation.convertConversationListToJson(user.getConversationsFromDB()));
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
  @OPTIONS @Path("/show")
  public Response corsShowConversation() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post request to get messages
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "conversationID":0, 
   *  "lastread":0
   *}
   * @return  <pre><code>{
   *  "messageList":[
   *    {
   *      "content":"content",
   *      "senderDate":456456465465,
   *      "senderID":0
   *    },
   *  ],
   *  "successful":true
   *}<code><pre>
   */
  @POST @Path("/show")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showConversation(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation returns:" + entity);
        return Helper.okResponse(entity);
      } 
      Conversation con = new Conversation()
        .setID(input.getInt("conversationID"))
        .setLastRead(new Message()
          .setID(input.getInt("lastread")))
        .getConversationFromDB()
        .readConversation(user);
      String entity = String.valueOf(con
            .getAsJson());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
  @OPTIONS @Path("/send")
  public Response corsSendMessage() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "content":"asdfasdf",
   *  "conversationID":0
   *  ]
   *}</code></pre>
   * @return <pre><code>{
   *  "successful":true,
   *  "id":0
   *}</code></pre>
   */
  @POST @Path("/send")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response sendMessage(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation/send returns:" + entity);
        return Helper.okResponse(entity);
      } 
      Message message = new Message()
        .setContent(input.getString("content"))
        .setSender(user);
      Conversation con = new Conversation()
        .setID(input.getInt("conversationID"));
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("id", con.sendMessage(message))
          .add("successful", true)
          .build());
      con.readConversation(user);
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
  @OPTIONS @Path("/new")
  public Response corsNewConversation() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "name":"conName", //optional
   *  "receiverList":[
   *    {
   *      "username":"username"
   *    },
   *  ]
   *}</code></pre>
   * @return <pre><code>{
   *  "successful":true,
   *  "conversationID":0
   *}</code></pre>
   */
  @POST @Path("/new")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response newConversation(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation/new returns:" + entity);
        return Helper.okResponse(entity);
      } 
      JsonArray jsonReceiver = input.getJsonArray("receiverList");
      List<User> receivers = new ArrayList<User>();
      for (int i = 0; i < jsonReceiver.size(); i++){
        receivers.add(new User().setUsername(jsonReceiver.getJsonObject(i).getString("username")));
      }
      receivers.add(user);
      Conversation con = new Conversation()
        .setReceivers(receivers);
      if(input.containsKey("name")) {
        con.setName(input.getString("name"));
      }
      con.addConversationToDB();
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("conversationID", con.getID())
          .add("successful", true)
          .build());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }  
  
  @OPTIONS @Path("/archive")
  public Response corsArchiveConversation() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "conversationID":0
   *}</code></pre>
   * @return <pre><code>{
   *  "successful":true
   *}</code></pre>
   */
  @POST @Path("/archive")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response archiveConversation(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation/archive returns:" + entity);
        return Helper.okResponse(entity);
      } 
      new Conversation()
        .setID(input.getInt("conversationID"))
        .archiveConversation(user);
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }  
  
  @OPTIONS @Path("/unread")
  public Response corsNumberOfUnreadConversations() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "session":"sessionID"
   *}</code></pre>
   * @return <pre><code>{
   *  "unread":0,
   *  "successful":true
   *}</code></pre>
   */
  @POST @Path("/unread")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response numberOfUnreadConversations(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation/archive returns: " + entity);
        return Helper.okResponse(entity);
      } 
      String entity = String.valueOf(Conversation.getUnreadConversations(user));
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }  
  
  @OPTIONS @Path("/rename")
  public Response corsRenameConversation() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "id":0,
   *  "name":"new name"
   *}</code></pre>
   * @return <pre><code>{
   *  "successful":true
   *}</code></pre>
   */
  @POST @Path("/rename")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response renameConversation(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation/archive returns: " + entity);
        return Helper.okResponse(entity);
      } 
      Conversation con = new Conversation().setID(input.getInt("id"));
      con.rename(input.getString("name"));
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
}
