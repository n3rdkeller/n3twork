package servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class ServletResource {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    return Response.ok("You")
    		.header("Access-Control-Allow-Origin", "*")
    		.build();
  }

  /*@GET @Path("/bye")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayBye() {
    return "Bye!";
  }*/
}
