package servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/post")
public class PostResource {
  
  /**
   * Options request for /
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/")
  public Response corsShowPost() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post request to get post
   * @param jsonInput
   * @return {}
   */
  @POST @Path("/")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showPost(String jsonInput){
    return Helper.okResponse("");
  }
  
}
