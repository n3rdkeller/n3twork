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
  
  @OPTIONS @Path("/settings")
  public Response corsUpdateUserSettings() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  @POST @Path("/settings")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response updateUserSettings(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject settingsAsJson = jsonReader.readObject();
      User user = Helper.checkSessionIDMin(settingsAsJson.getString("session"));
      if (user == null) {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .build()))
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
      if (settingsAsJson.containsKey("firstName")) {
        user.setFirstName(settingsAsJson.getString("firstName")); //TODO change all these setters from void to Boolean?
      }
      if (settingsAsJson.containsKey("name")) {
        user.setName(settingsAsJson.getString("name"));
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
      return Response.ok()
          .entity(String.valueOf(Json.createObjectBuilder()
              .add("successful", true)
              .build()))
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
  
  @OPTIONS @Path("/friends")
  public Response corsGetFriends() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  @POST @Path("/friends")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response getFriends(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject settingsAsJson = jsonReader.readObject();
      User user = Helper.checkSessionID(settingsAsJson.getString("session"));
      if (user == null) {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .build()))
            .header(Helper.ACCESSHEADER, "*")
            .build();
      }
      return Response.ok()
          .entity(user.getFriendsAsJson())
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

  @OPTIONS @Path("/friend/add")
  public Response corsAddFriend() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  @POST @Path("/friend/add")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response AddFriend(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject inputAsJson = jsonReader.readObject();
      User user = Helper.checkSessionID(inputAsJson.getString("session"));
      if (user == null) {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .build()))
            .header(Helper.ACCESSHEADER, "*")
            .build();
      } else {
        user.addFriendToDB(inputAsJson.getInt("friendID"));
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", true)
                .build()))
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
