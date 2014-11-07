package servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ServletResource {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHello() {
    return "Hello World!";
  }

  @GET @Path("/bye")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayBye() {
    return "Bye!";
  }
}
