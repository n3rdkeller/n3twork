package servlet;

import java.io.StringReader;
import java.util.ArrayList;
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
/**
 * Basic part of the api. Contains login, registration and logout
 * @author johannes@n3rdkeller.de
 *
 */
@Path("/")
public class ServletResource {
  final static Logger log = LogManager.getLogger(ServletResource.class);

  private List<String> userList = new ArrayList<String>();
  
  /**
   * Simple get request to check availability of api
   * @return Response with simple String as entity
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    return Response.ok("root")
    		.header(Helper.ACCESSHEADER, "*")
    		.build();
  }
  
  /**
   * Options request for login
   * @return Response with all the needed headers
   */
  @OPTIONS @Path("/login")
  public Response corsLogin() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request for login. 
   * @param input <pre><code>{
   *  "login" : "username/email",
   *  "password" : "pw in plain text"
   *}</pre></code>   
   * @return <pre><code>{
   *  "session":"sessionID",
   *  "id":userID,
   *  "username":"username",
   *  "email":"email",
   *  "lastname":"last name",
   *  "firstname":"first name",
   *  "otherProperties":{
   *    "propertie1":"value",
   *    "propertie2":"value",
   *  },
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response login(String input){
    log.debug("login input: " + input);
    try {
      User user = new User(input);
      if (user.login()){
        // login successful
        user.createSessionID();
        String entity = String.valueOf(user.getAsJson());
        log.debug("/login returns: " + entity);
    		return Response.ok()
    		    .entity(entity)
    				.header(Helper.ACCESSHEADER, "*")
    				.build();
    	} else {
    	  // login not successful
    	  String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .build());
    	  log.debug("/login returns: " + entity);
    		return Response.ok()
    		    .entity(entity)
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
   * Options request for logout
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/logout")
  public Response corsLogout() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request for logout
   * @param jsonInput <pre><code>{
   *  "session" : "sessionID"
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/logout")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response logout(String jsonInput){
    log.debug("logout input: " + jsonInput);
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject sessionID = jsonReader.readObject();
      User user = new User(sessionID.getString("session").toCharArray());
      user.logout();
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      log.debug("/logout returns: " + entity);
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
   * Options request for register
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/register")
  public Response corsRegister() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request for register
   * @param jsonInput <pre><code>{
   *  "email":"email@text",
   *  "password":"pw as plain text",
   *  "username":"usernametext"
   *}</pre></code>
   * @return <pre><code>{
   *  "successful":true/false
   *}</pre></code>
   */
  @POST @Path("/register")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response register(String jsonInput){
    log.debug("register input: " + jsonInput);
    try {
      User user = new User(jsonInput);
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", user.registerInDB())
          .build());
      log.debug("/register returns: " + entity);
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
   * Options request for checkuser
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/register/checkuser")
  public Response corsCheckUser() {
     return Response.ok()
         .header(Helper.ACCESSHEADER, "*")
         .header("Access-Control-Allow-Methods", "POST, OPTIONS")
         .header("Access-Control-Allow-Headers", "Content-Type")
         .build();
  }
  
  /**
   * Post request for checkuser. Gets a List of all usernames and then checks if the user in in that list
   * @param jsonInput <pre><code>{
   *  "username":"usernametext"
   *}</pre></code>
   * @return <pre><code>{
   *  "username":"usernametext",
   *  "taken":true/false,
   *  "successful":true
   *}</pre></code>
   */
  @POST @Path("/register/checkuser")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response checkUser(String jsonInput){
    log.debug(jsonInput);
    try {
      if (this.userList.size() == 0){
        this.userList = User.getUsernameList();
      } 
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject userAsJsonObject = jsonReader.readObject();
      String user = userAsJsonObject.getString("username");
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("username", user)
          .add("taken", this.userList.contains(user))
          .add("successful", true)
          .build());
      log.debug("/register/checkuser returns: " + entity);
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
  
  
  

  
  
}
