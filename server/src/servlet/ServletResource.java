package servlet;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

@Path("/")
public class ServletResource {
  final static Logger log = LogManager.getLogger(ServletResource.class);
  
  private static String ACCESSHEADER = "Access-Control-Allow-Origin";
  private User user;
  private List<String> userList = new ArrayList<String>();
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    return Response.ok("root")
    		.header(ACCESSHEADER, "*")
    		.build();
  }
  
  @OPTIONS @Path("/login")
  public Response corsLogin() {
     return Response.ok()
         .header(ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }

  @POST @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response login(String input){
    log.debug("login input: " + input);
    try {
      this.user = new User(input);
      if (this.user.login()){
        this.user.createSessionID();
    		return Response.ok()
    		    .entity(this.user.getAsJson())
    				.header(ACCESSHEADER, "*")
    				.build();
    	} else {
    		return Response.ok()
    		    .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .build()))
    				.header(ACCESSHEADER, "*")
    				.build();
    	}
    } catch (Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(ACCESSHEADER, "*")
          .build();
    }
  }
//  @POST @Path("/login")
//  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
//  public Response login(@HeaderParam("Session") String sessionID){
//    this.user = new User(sessionID.toCharArray());
//    try {
//      if (this.user.checkSessionID()){
//        this.user.getFromDB();
//        return Response.ok(this.user.getAsJson())
//            .header(ACCESSHEADER, "*")
//            .header("Session", this.user.createSessionID())
//            .build();
//      } else {
//        return Response.status(Status.UNAUTHORIZED)
//            .entity("login not successful")
//            .header(ACCESSHEADER, "*")
//            .build();
//      }
//    } catch (Exception e){
//      return Response.status(Status.INTERNAL_SERVER_ERROR)
//          .entity(e.toString()) //TODO Needs to return error!!!
//          .header(ACCESSHEADER, "*")
//          .build();
//    }
//    
//  }
  
  @OPTIONS @Path("/register")
  public Response corsRegister() {
     return Response.ok()
         .header(ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  @POST @Path("/register")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response register(String jsonInput){
    log.debug("login input: " + jsonInput);
    try {
      this.user = new User(jsonInput);
      if (user.registerInDB()){
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", true)
                .build()))
            .header(ACCESSHEADER, "*")
            .build();
      } else {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("successful", false)
                .build()))
            .header(ACCESSHEADER, "*")
            .build();
      }
    } catch (Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(ACCESSHEADER, "*")
          .build();
    }
  }
  
  @OPTIONS @Path("/register/checkuser")
  public Response corsCheckUser() {
     return Response.ok()
         .header(ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  @POST @Path("/register/checkuser")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response checkUser(String jsonInput){
    log.debug(jsonInput);
    try {
      if (this.userList.size() == 0){
        this.userList = User.getUserList();
      } 
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject userAsJsonObject = jsonReader.readObject();
      String user = userAsJsonObject.getString("username");
      
      if(userList.contains(user)){
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("username", user)
                .add("taken", true)
                .build()))
            .header(ACCESSHEADER, "*")
            .build();
      } else {
        return Response.ok()
            .entity(String.valueOf(Json.createObjectBuilder()
                .add("username", user)
                .add("taken", false)
                .build()))
            .header(ACCESSHEADER, "*")
            .build();
      }
      
    } catch (Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(ACCESSHEADER, "*")
          .build();
    }
  }
  
  @OPTIONS @Path("/usersettings")
  public Response corsUpdateUserSettings() {
     return Response.ok()
         .header(ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  @POST @Path("/usersettings")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response updateUserSettings(String jsonInput){
    try{
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject settingsAsJson = jsonReader.readObject();
      user.setFirstName(settingsAsJson.getString("firstName")); //TODO change all these setters from void to Boolean?
      user.setName(settingsAsJson.getString("name"));
      user.setEmail(settingsAsJson.getString("email"));
      user.setPassword(settingsAsJson.getString("password"));
      JsonObject otherPropertiesAsJson = settingsAsJson.getJsonObject("otherProperties");
      HashMap<String,String> otherProperties = new HashMap<String,String>();
      //for(Entry<String, String> e : otherPropertiesAsJson.entrySet()));
      if (settingsAsJson.containsKey("dateOfBirth")){
        otherProperties.put("dateOfBirth", settingsAsJson.getString("dateOfBirth"));
      }
      if (settingsAsJson.containsKey("education")) {
        otherProperties.put("education", settingsAsJson.getString("education"));
      }
      if (settingsAsJson.containsKey("gender")) {
        otherProperties.put("gender", settingsAsJson.getString("gender"));
      }
      user.setOtherProperties(otherProperties);
      return Response.ok()
          .entity(String.valueOf(Json.createObjectBuilder()
              .add("successful", true)
              .build()))
          .header(ACCESSHEADER, "*")
          .build();
    } catch (Exception e){
      log.error(e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(e.toString())
          .header(ACCESSHEADER, "*")
          .build();
    }
  }
  
  @SuppressWarnings("unused")
  private Boolean checkSessionID(String sessionID){
    log.debug("checkSessionID: " + sessionID);
    this.user = new User(sessionID.toCharArray());
    try {
      return user.getFromDB();
    } catch (Exception e) {
      log.error(e);
      return false;
    }
  }
  
  
}
