package servlet;

import java.io.StringReader;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.Group;
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
     return Helper.optionsResponse();
  }
  
  /**
   * Post Request to apply settings
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "changedSetting1":"newValue1",
   *  "changedSetting2":"newValue2",
   *  "otherProperties":{
   *    "changedPropertie1":"newValue3",
   *    "changedPropertie2":"newValue4"
   *  }
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
   */
  @PUT @Path("/settings")
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
        return Helper.okResponse(entity);
      }
      if (settingsAsJson.containsKey("firstName")) {
        user.setFirstName(settingsAsJson.getString("firstName"));
      }
      if (settingsAsJson.containsKey("lastName")) {
        user.setName(settingsAsJson.getString("lastName"));
      }
      if (settingsAsJson.containsKey("email")) {
        user.setEmail(settingsAsJson.getString("email"));
      }
      if (settingsAsJson.containsKey("password")) {
        user.setPassword(settingsAsJson.getString("password"));        
      }
      if (settingsAsJson.containsKey("otherProperties")) {
        JsonObject otherPropertiesAsJson = settingsAsJson.getJsonObject("otherProperties");
        HashMap<String,String> otherProperties = new HashMap<String,String>();
        for(String key : otherPropertiesAsJson.keySet()){
          otherProperties.put(key, otherPropertiesAsJson.getString(key));
        }
        user.setOtherProperties(otherProperties);
      }
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/user/settings returns: " + entity);
      return Helper.okResponse(entity);
    } catch (Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }

  @OPTIONS @Path("/find")
  public Response corsFindUser() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post Request to get a json all users
   * @param jsonInput <pre><code>{
   *  "session":"sessionID"
   *}
   * @return <pre><code>{
   *  "userList":[
   *    {
   *      "id":userID,
   *      "username":"username",
   *      "email":"email",
   *      "lastName":"last name",
   *      "firstName":"first name",
   *      "otherProperties":{
   *        "propertie1":"value",
   *        "propertie2":"value",
   *      }
   *    },
   *  ],
   *  "successful":true
   *}</pre></code>
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
        return Helper.okResponse(entity);
      }
      String entity = User.convertUserListToJson(
          User.getAllUsers());
      log.debug("/user/find returns: " + entity);
      return Helper.okResponse(entity);
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }

  @OPTIONS @Path("/count")
  public Response corsCountUser() {
    return Helper.optionsResponse();
  }
  
  /**
   * GET Request to count user in db
   * @param jsonInput <pre><code>{
   *  "session":"sessionID"
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true,
   *  "users":number of users in the system
   *  "usersOnline": number of users online
   *}</code></pre>
   */
  @POST @Path("/count")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response countUser(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user returns: " + entity);
        return Helper.okResponse(entity);
      }
      String entity = User.getSimpleUserStats();
      log.debug("/user returns: " + entity);
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
   * Options request for show
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/")
  public Response corsShowUser() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to show user by id
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "username":"username" // optional
   *}<pre><code>
   * @return <pre><code>{
   *  "id":userID,
   *  "username":"username",
   *  "email":"email",
   *  "lastName":"last name",
   *  "firstName":"first name",
   *  "otherProperties":{
   *    "propertie1":"value",
   *    "propertie2":"value",
   *  },
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showUser(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user returns: " + entity);
        return Helper.okResponse(entity);
      }
      if (input.containsKey("username")) {
        user = new User(jsonInput);
        if (!user.getBasicsFromDB()) {
          String entity = String.valueOf(Json.createObjectBuilder()
              .add("successful", false)
              .add("reason", "Requested user doesn't exist")
              .build());
          log.debug("/user returns: " + entity);
          return Helper.okResponse(entity);
        }      
      } else {
        user.setSessionID(null);
      }
      String entity = String.valueOf(user.getAsJson());
      log.debug("/user returns: " + entity);
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

  @OPTIONS @Path("/remove")
  public Response corsRemoveUser() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post Request to remove current user
   * @param jsonInput <pre><code>{
   *  "session":"sessionID"
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
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
        return Helper.okResponse(entity);
      }
      user.removeFromDB();
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/user/remove returns: " + entity);
      return Helper.okResponse(entity);
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
   * Post Request to get all friends of a user
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "id":userID //optional
   *}</pre></code>
   * @return <pre><code>{
   *  "friendList":[
   *    {
   *      "id":"id",
   *      "username":"username",
   *      "lastName":"last name",
   *      "firstName":"first name",
   *      "email":"email",
   *      "trueFriend":true/false,
   *      "date":timestamp of adding      
   *    },
   *  ],
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/friends")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response getFriends(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/friends returns: " + entity);
        return Helper.okResponse(entity);
      }
      if (input.containsKey("id")) {
        user = new User(input.getInt("id"));
      }
      user.getFriendsFromDB();
      String entity = user.getFriendsAsJson();
      log.debug("/user/friends returns: " + entity);
      return Helper.okResponse(entity);
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for friend requests
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/friendrequests")
  public Response corsGetFriendRequests() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post Request to get all friend requests of a user
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "id":userID //optional
   *}</pre></code>
   * @return <pre><code>{
   *  "friendRequests":[
   *    {
   *      "id":"id",
   *      "username":"username",
   *      "lastName":"last name",
   *      "firstName":"first name",
   *      "email":"email",
   *      "date":timestamp of adding 
   *    },
   *  ],
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/friendrequests")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response getFriendRequests(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/friendrequests returns: " + entity);
        return Helper.okResponse(entity);
      }
      if (input.containsKey("id")) {
        user = new User(input.getInt("id"));
      }
      user.getFriendRequestsFromDB();
      String entity = user.getFriendRequestsAsJson();
      log.debug("/user/friendrequests returns: " + entity);
      return Helper.okResponse(entity);
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
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "friend":userID
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
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
        return Helper.okResponse(entity);
      } else {
        user.addFriendToDB(inputAsJson.getInt("friend"));
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/user/friend/add returns: " + entity);
        return Helper.okResponse(entity);
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
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "friend":friendID
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
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
        return Helper.okResponse(entity);
      } else {
        user.removeFriend(inputAsJson.getInt("friend"));
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/user/friend/remove returns: " + entity);
        return Helper.okResponse(entity);
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
   * Options request for groups
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/groups")
  public Response corsGetGroups() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to get all groups of a user
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "username":"username" //optional
   *}</pre></code>
   * @return <pre><code>{
   *  "groupList":[
   *    {
   *      "groupID":groupID,
   *      "groupName":"group name",
   *      "groupDescr":"group descr"
   *    },
   *  ],
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/groups")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response getGroups(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/groups returns: " + entity);
        return Helper.okResponse(entity);
      }
      if (input.containsKey("username")) {
        user = new User().setUsername(input.getString("username"));
      }
      String entity = user.getGroupsFromDB().getGroupsAsJson();
      log.debug("/user/groups returns: " + entity);
      return Helper.okResponse(entity);
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
    
  }
  
  /**
   * Options request for group/join
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/group/join")
  public Response corsJoinGroup() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to allow the current user to join a group
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "group":groupID
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
   */
  @POST @Path("/group/join")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response joinGroup(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/group/join returns: " + entity);
        return Helper.okResponse(entity);
      }
      Group group = new Group(input.getInt("group"));
      if (!group.isMember(user)) {
        user.addGroup(group);
      }
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/user/group/join returns: " + entity);
      return Helper.okResponse(entity);
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for group/leave
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/group/leave")
  public Response corsLeaveGroup() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to allow the current user to leave a group
   * @param jsonInput <pre><code>{
   *  "session":"sessionID",
   *  "group":groupID
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
   */
  @POST @Path("/group/leave")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response leaveGroup(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/user/group/leave returns: " + entity);
        return Helper.okResponse(entity);
      }
      user.removeGroup(new Group(input.getInt("group")));
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/user/group/leave returns: " + entity);
      return Helper.okResponse(entity);
    } catch(Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
}
