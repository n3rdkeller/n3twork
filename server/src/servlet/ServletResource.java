package servlet;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(@FormParam("username") String username,
		  				          @FormParam("email") String email,
		  				          @FormParam("pw") String pw){
    this.user = new User(username, email, pw);
    try {
      if (this.user.login()){
    		return Response.ok(this.user.getAsJson())
    				.header(ACCESSHEADER, "*")
    				.header("Session", "b3df3e70c2daabd61e7e8175f3f8f9d4")//"<sessionid>") //TODO pack die seschonid
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
  
  
}
