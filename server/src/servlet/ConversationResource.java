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
   * Options request for /
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/")
  public Response corsShowMessage() {
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
  public Response showMessage(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/message returns:" + entity);
        return Helper.okResponse(entity);
      } 
      String entity = String.valueOf(Conversation.convertConversationListToJson(user.getConversationsFromDB()));
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
   *  "receiverList":[
   *    {
   *      "username":"username"
   *    },
   *  ]
   *}</code></pre>
   * @return <pre><code>{
   *  "successful":true
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
        log.debug("/message returns:" + entity);
        return Helper.okResponse(entity);
      } 
      JsonArray JsonReceiver = input.getJsonArray("receiverList");
      List<User> receiver = new ArrayList<User>();
      for (int i = 0; i < JsonReceiver.size(); i++){
        receiver.add(new User().setUsername(JsonReceiver.getString(i)));
      }
      Message message = new Message()
        .setContent(input.getString("content"))
        .setSender(user);
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
