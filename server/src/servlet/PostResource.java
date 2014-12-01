package servlet;

import java.io.StringReader;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.Group;
import classes.User;

@Path("/post")
public class PostResource {
  final static Logger log = LogManager.getLogger(PostResource.class);
  
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
  
  @OPTIONS @Path("/newsfeed")
  public Response corsGetFeed() {
    return Helper.optionsResponse();
  }
  
  @POST @Path("/newsfeed")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response getFeed(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post/newsfeed returns:" + entity);
        return Helper.okResponse(entity);
      } 
      
      return null;
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
}