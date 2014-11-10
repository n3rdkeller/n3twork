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
  private static String HEADER = "Access-Control-Allow-Origin";
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    return Response.ok("root")
    		.header(HEADER, "*")
    		.build();
  }
  
  @POST @Path("/login")
  @Produces(MediaType.TEXT_PLAIN)
  public Response login(@FormParam("username") String username,
		  				          @FormParam("email") String email,
		  				          @FormParam("pw") String pw){
    User user = new User(username, email, pw);
    try {
      if (user.login()){
    		return Response.ok("login successful")
    				.header(HEADER, "*")
    				.build();
    	} else {
    		return Response.ok("login not successful")
    				.header(HEADER, "*")
    				.build();
    	}
    } catch (Exception e){
      return Response.ok(e.toString()) //Needs to return error!!!
          .header(HEADER, "*")
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
            .header(HEADER, "*")
            .build();
      } else {
        return Response.ok("registration not successful")
            .header(HEADER, "*")
            .build();
      }
    } catch (Exception e){
      return Response.ok(e.toString()) //Needs to return error!!!
          .header(HEADER, "*")
          .build();
    }
  }
  
  
}
