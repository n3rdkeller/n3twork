package servlet;

import java.io.StringReader;

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
import classes.Group;

/**
 * Strictly group related part of the api
 * @author johannes@n3rdkeller.de
 *
 */
@Path("/group")
public class GroupResource {
  final static Logger log = LogManager.getLogger(GroupResource.class);

  /**
   * Options request for join
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/find")
  public Response corsFindGroup() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to find groups
   * @param jsonInput '{ "session" : "sessionID", "groupID":"groupID" }'
   * @return '{ "successful" : true/false }' with html error code 200 or any exception with html error code 500
   */
  @POST @Path("/find")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response findGroup(String jsonInput){
    log.debug("find input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/group/find returns:" + entity);
        return Helper.okResponse(entity);
      } 
      String entity = String.valueOf(Group.convertGroupListToJson(
              Group.findGroup(
                  input.getString("search"))));
      log.debug("/group/find returns:" + entity);
      return Helper.okResponse(entity);
      
    } catch (Exception e){
      // internal server error
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for join
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/join")
  public Response corsJoinGroup() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to join an existing group
   * @param jsonInput '{ "session" : "sessionID", "groupID":"groupID" }'
   * @return '{ "successful" : true/false }' with html error code 200 or any exception with html error code 500
   */
  @POST @Path("/join")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response joinGroup(String jsonInput){
    log.debug("join input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getInt("groupID"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/group/join returns:" + entity);
        return Helper.okResponse(entity);
      } else if (!group.getBasicsFromDB()) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "Group doesn't exist")
            .build());
        log.debug("/group/join returns:" + entity);
        return Helper.okResponse(entity);
      } else if (!group.isMember(user)){
        group.addMember(user);
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/group/join returns:" + entity);
        return Helper.okResponse(entity);
      } else {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "User is already in the group")
            .build());
        log.debug("/group/join returns:" + entity);
        return Helper.okResponse(entity);
      }
    } catch (Exception e){
      // internal server error
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for found
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/found")
  public Response corsFoundGroup() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to found a group
   * @param jsonInput '{ "session" : "sessionID", "groupName":"groupName", "groupDescr":"groupDescr" }'
   * @return '{ "successful" : true/false }' with html error code 200 or any exception with html error code 500
   */
  @POST @Path("/found")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response foundGroup(String jsonInput){
    log.debug("found input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getString("groupName"), input.getString("groupDescr"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/group/found returns:" + entity);
        return Helper.okResponse(entity);
      } else if (group.registerInDB()) {
        group.addMember(user);
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/group/found returns:" + entity);
        return Helper.okResponse(entity);
      } else {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "Group with that name already exists or no name is given")
            .build());
        log.debug("/group/found returns:" + entity);
        return Helper.okResponse(entity);
      }
    } catch (Exception e){
      // internal server error
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for show/all
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/show")
  public Response corsShowGroup() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to get a group if the user is a member
   * @param jsonInput '{ "session" : "sessionID", "groupID":"groupID"}'
   * @return '{ "successful" : true/false }' or group with html error code 200 or any exception with html error code 500
   */
  @POST @Path("/show")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showGroup(String jsonInput){
    log.debug("show input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getInt("groupID"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/group/show returns:" + entity);
        return Helper.okResponse(entity);
      } else if (!group.getBasicsFromDB()) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "Group doesn't exist")
            .build());
        log.debug("/group/show returns:" + entity);
        return Helper.okResponse(entity);
      } else if (group.isMember(user)){
        return Response.ok()
            .entity(group.getAsJson())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "User may not be member of group")
            .build());
        log.debug("/group/show returns:" + entity);
        return Helper.okResponse(entity);
      }
    } catch (Exception e){
      // internal server error
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  /**
   * Options request for show/all
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/show/all")
  public Response corsShowAllGroups() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to get a list of all groups
   * @param jsonInput '{ "session" : "sessionID"}'
   * @return '{ "successful" : true/false }' with html error code 200 or any exception with html error code 500
   */
  @POST @Path("/show/all")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showAllGroups(String jsonInput){
    log.debug("showAll input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/group/show/all returns:" + entity);
        return Helper.okResponse(entity);
      } else {
        String entity = Group.getAllAsJson();
        log.debug("/group/show/all returns:" + entity);
        return Helper.okResponse(entity);
      }
    } catch (Exception e){
      // internal server error
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
  
  
  /**
   * Options request for show/member
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/show/members")
  public Response corsShowGroupMembers() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request to get a list of all members in the group
   * @param jsonInput '{ "session" : "sessionID", "groupID",groupID}'
   * @return '{ "successful" : false }' or member as json with html error code 200 or any exception with html error code 500
   */
  @POST @Path("/show/members")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showGroupMembers(String jsonInput){
    log.debug("showAll input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getInt("groupID"));
      if (user == null) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/group/show/members returns:" + entity);
        return Helper.okResponse(entity);
      } else if (!group.getBasicsFromDB()) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "Group doesn't exist")
            .build());
        log.debug("/group/show/members returns:" + entity);
        return Helper.okResponse(entity);
      } else {
        return Response.ok()
            .entity(group.getMembersAsJson())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
    } catch (Exception e){
      // internal server error
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(Helper.ACCESSHEADER, "*")
          .build();
    }
  }
}
