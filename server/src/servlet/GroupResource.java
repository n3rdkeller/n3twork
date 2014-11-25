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
  @OPTIONS @Path("/join")
  public Response corsJoin() {
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
  public Response join(String jsonInput){
    log.debug("join input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getInt("groupID"));
      if (user == null){
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "SessionID invalid")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else if (!group.getBasicsFromDB()) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "Group doesn't exist")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else if (!group.isMember(user)){
        group.addMember(user);
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", true)
                .build()))
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "User is already in the group")
                .build()))
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
  
  /**
   * Options request for found
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/found")
  public Response corsFound() {
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
  public Response found(String jsonInput){
    log.debug("found input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getString("groupName"), input.getString("groupDescr"));
      if (user == null) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "SessionID invalid")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else if (group.registerInDB()) {
        group.addMember(user);
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", true)
                .build()))
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "Group with that name already exists or no name is given")
                .build()))
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
  
  /**
   * Options request for show/all
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/show")
  public Response corsShow() {
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
  public Response show(String jsonInput){
    log.debug("show input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getInt("groupID"));
      if (user == null) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "SessionID invalid")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else if (!group.getBasicsFromDB()) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "Group doesn't exist")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else if (group.isMember(user)){
        return Response.ok()
            .entity(group.getAsJson())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "User may not be member of group")
                .build())
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
  
  /**
   * Options request for show/all
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/show/all")
  public Response corsShowAll() {
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
  public Response showAll(String jsonInput){
    log.debug("showAll input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "SessionID invalid")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        return Response.ok()
            .entity(Group.getAllAsJson())
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
  
  
  /**
   * Options request for show/member
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/show/member")
  public Response corsShowMember() {
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
  @POST @Path("/show/member")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showMember(String jsonInput){
    log.debug("showAll input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      Group group = new Group(input.getInt("groupID"));
      if (user == null) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "SessionID invalid")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else if (!group.getBasicsFromDB()) {
        return Response.ok()
            .entity(Json.createObjectBuilder()
                .add("successful", false)
                .add("reason", "Group doesn't exist")
                .build())
            .header(Helper.ACCESSHEADER, "*")
            .build();
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
