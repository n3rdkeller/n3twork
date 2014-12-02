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
import classes.Post;
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
   * @param jsonInput <pre><code> {
   *   "groupID":0, //optional if given uses group
   *   "userID":0, //optional if given uses user
   *   "session":"sessionID"
   * }
   * @return <pre><code> {
   *   "postList": [
   *     {
   *       "content":"content text",
   *       "id":postID number,
   *       "owner":owenerID number,
   *       "postDate":timestamp number,
   *       "title":"title text",
   *       "upVotes": [
   *         {
   *           "date":timestamp number,
   *           "voter":voterID number
   *         },
   *       ],
   *       "visibility":true/false
   *     },
   *   ],
   *   "successful":true
   * } </code></pre>
   */
  @POST @Path("/")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showPost(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post returns:" + entity);
        return Helper.okResponse(entity);
      } 
      if (input.containsKey("userID")) {
        user = new User (input.getInt("userID"));
      } else if (input.containsKey("groupID")) {
        Group group = new Group(input.getInt("groupID"));
        String entity = Post.convertPostListToJson(group.getPosts());
        return Helper.okResponse(entity);
      }
      String entity = Post.convertPostListToJson(user.getPosts());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
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
  
  @OPTIONS @Path("/add")
  public Response corsAddPost() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code> {
   *   "groupID":0, //optional if given uses group
   *   "userID":0, //optional if given uses user
   *   "session":"sessionID"
   *   "post": {
   *     "title":"",
   *     "content":"",
   *     "visibility":true/false
   *   }
   * }
   * @return
   */
  @POST @Path("/add")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response addPost(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post/add returns:" + entity);
        return Helper.okResponse(entity);
      } 
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      if (input.containsKey("userID")) {
        user = new User (input.getInt("userID"));
      } else if (input.containsKey("groupID")) {
        Group group = new Group(input.getInt("groupID"));
        group.addPost(new Post()
              .setContent(input.getJsonObject("post").getString("content"))
              .setTitle(input.getJsonObject("post").getString("title"))
              .setVisibility(input.getJsonObject("post").getBoolean("visibility")));
        return Helper.okResponse(entity);
      }
      user.addPost(new Post()
            .setContent(input.getJsonObject("post").getString("content"))
            .setTitle(input.getJsonObject("post").getString("title"))
            .setVisibility(input.getJsonObject("post").getBoolean("visibility")));
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
}
