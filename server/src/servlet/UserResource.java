package servlet;

import java.io.StringReader;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.User;

/**
 * Strictly user related part of the api
 * @author johannes@n3rdkeller.de
 *
 */
@Path("/user")
public class UserResource {
  final static Logger log = LogManager.getLogger(UserResource.class);
  
  /**
   * Options request for settings
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/settings")
  public Response corsUpdateUserSettings() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to apply settings
   * @param jsonInput {"session":"sessionID","changedSetting1":"newValue1",...}
   * @return Response with the entity {"successful", true/false} and html error code 200
   */
  @POST @Path("/settings")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response updateUserSettings(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject settingsAsJson = jsonReader.readObject();
      User user = Helper.checkSessionID(settingsAsJson.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/settings returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
      if (settingsAsJson.containsKey("firstname")) {
        user.setFirstName(settingsAsJson.getString("firstname"));
      }
      if (settingsAsJson.containsKey("lastname")) {
        user.setName(settingsAsJson.getString("lastname"));
      }
      if (settingsAsJson.containsKey("email")) {
        user.setEmail(settingsAsJson.getString("email"));
      }
      if (settingsAsJson.containsKey("password")) {
        user.setPassword(settingsAsJson.getString("password"));        
      }
      
      JsonObject otherPropertiesAsJson = settingsAsJson.getJsonObject("otherProperties");
      HashMap<String,String> otherProperties = new HashMap<String,String>();
      for(String key : otherPropertiesAsJson.keySet()){
        otherProperties.put(key, otherPropertiesAsJson.getString(key));
      }
      user.setOtherProperties(otherProperties);
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/user/settings returns: " + entity);
      return Response.ok()
          .entity(entity)
          .header(Helper.ACCESSHEADER, "*")
          .build();
    } catch (Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for find
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/find")
  public Response corsFindUser() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to search for users who match the search string
   * @param jsonInput {"session":"sessionID", "search":"searchString"}
   * @return Response with the entity {"successful":false} or json list of found users and html error code 200
   */
  @POST @Path("/find")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response findUser(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject jsonObject = jsonReader.readObject();
      User user = Helper.checkSessionID(jsonObject.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/find returns: " + entity);
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "SessionID invalid")
                .build()))
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
      String entity = User.convertUserListToJson(
          User.findUsers(
              jsonObject.getString("search")));
      log.debug("/user/find returns: " + entity);
      return Response.ok()
          .entity(entity)
          .header(Helper.ACCESSHEADER, "*")
          .build();
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for remove
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/remove")
  public Response corsRemoveUser() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to remove current user
   * @param jsonInput {"session":"sessionID"}
   * @return Response with the entity {"successful":true / false} and html error code 200
   */
  @POST @Path("/remove")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response removeUser(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject jsonSession = jsonReader.readObject();
      User user = Helper.checkSessionID(jsonSession.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/remove returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
      user.removeFromDB();
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/user/remove returns: " + entity);
      return Response.ok()
          .entity(entity)
          .header(Helper.ACCESSHEADER, "*")
          .build();
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for friends
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/friends")
  public Response corsGetFriends() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to get all friends of current user
   * @param jsonInput {"session":"sessionID"}
   * @return Response with the entity {"friends":[{"id":"id","username":"username",...},...],"successful":true} or {"successful":false} and html error code 200
   */
  @POST @Path("/friends")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response getFriends(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject jsonSession = jsonReader.readObject();
      User user = Helper.checkSessionID(jsonSession.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/friends returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
      user.getFriendsFromDB();
      String entity = user.getFriendsAsJson();
      log.debug("/user/friends returns: " + entity);
      return Response.ok()
          .entity(entity)
          .header(Helper.ACCESSHEADER, "*")
          .build();
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for friend/add
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/friend/add")
  public Response corsAddFriend() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to add a friend
   * @param jsonInput {"session":"sessionID","friendID":userID}
   * @return Response with the entity {"successful":true/false} and html error code 200
   */
  @POST @Path("/friend/add")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response addFriend(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject inputAsJson = jsonReader.readObject();
      User user = Helper.checkSessionID(inputAsJson.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/friend/add returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        user.addFriendToDB(inputAsJson.getInt("friendID"));
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/user/friend/add returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for friend/remove
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/friend/remove")
  public Response corsRemoveFriend() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to remove a friend
   * @param jsonInput {"session":"sessionID", "friendID":"friendID"}
   * @return Response with the entity {"successful":true/false} and html error code 200
   */
  @POST @Path("/friend/remove")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response removeFriend(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject inputAsJson = jsonReader.readObject();
      User user = Helper.checkSessionID(inputAsJson.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/friend/remove returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        user.removeFriend(inputAsJson.getInt("friendID"));
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/user/friend/remove returns: " + entity);
        return Response.ok()
            .entity(entity)
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
}
