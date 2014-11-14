package servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.User;

@Path("/")
public class ServletResource {
  final static Logger log = LogManager.getLogger(ServletResource.class);
  
  private static String ACCESSHEADER = "Access-Control-Allow-Origin";
  private User user;
  
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
    this.user = new User(input);
    try {
      if (this.user.login()){
        this.user.createSessionID();
    		return Response.ok()
    		    .entity(this.user.getAsJson())
    				.header(ACCESSHEADER, "*")
    				.build();
    	} else {
    		return Response.status(Status.UNAUTHORIZED)
    		    .entity("login not successful")
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
  
  @POST @Path("/register")
  @Produces(MediaType.TEXT_PLAIN)
  public Response register(@FormParam("username") String username,
                           @FormParam("email") String email,
                           @FormParam("pw") String pw){
    User user = new User(username, email, pw);
    try {
      if (user.registerInDB()){
        return Response.ok("registration successful")
            .header(ACCESSHEADER, "*")
            .build();
      } else {
        return Response.status(Status.INTERNAL_SERVER_ERROR)
            .entity("registration not successful")
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
  
  private Boolean checkSessionID(String sessionID){
    return null;
  }
  
  
}
