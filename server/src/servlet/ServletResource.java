package servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import classes.User;

@Path("/")
public class ServletResource {
  private static String ACCESSHEADER = "Access-Control-Allow-Origin";
  User user;
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    return Response.ok("root")
    		.header(ACCESSHEADER, "*")
    		.build();
  }
  
  @POST @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response login(@HeaderParam("Host") String sessionID,String input){
    this.user = new User(input);
    try {
      if (this.user.login()){
    		return Response.ok(this.user.getAsJson())
    				.header(ACCESSHEADER, "*")
    				.header("Session", this.user.createSessionID())//"<sessionid>") //TODO pack die seschonid
    				.build();
    	} else {
    		return Response.ok("login not successful")
    				.header(ACCESSHEADER, "*")
    				.build();
    	}
    } catch (Exception e){
      return Response.ok(e.toString()) //TODO Needs to return error!!!
          .header(ACCESSHEADER, "*")
          .build();
    }
  }
  @POST @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response login(@HeaderParam("Session") String sessionID){
    this.user = new User(sessionID.toCharArray());
    try {
      if (this.user.checkSessionID()){
        this.user.getFromDB();
        return Response.ok(this.user.getAsJson())
            .header(ACCESSHEADER, "*")
            .header("Session", this.user.createSessionID())//"<sessionid>") //TODO pack die seschonid
            .build();
      } else {
        return Response.ok("login not successful")
            .header(ACCESSHEADER, "*")
            .build();
      }
    } catch (Exception e){
      return Response.ok(e.toString()) //TODO Needs to return error!!!
          .header(ACCESSHEADER, "*")
          .build();
    }
    
  }
  
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
        return Response.ok("registration not successful")
            .header(ACCESSHEADER, "*")
            .build();
      }
    } catch (Exception e){
      return Response.ok(e.toString()) //TODO Needs to return error!!!
          .header(ACCESSHEADER, "*")
          .build();
    }
  }
  
  private Boolean checkSessionID(String sessionID){
    
    return null;
  }
  
  
}
