package servlet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
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
   * Post request to get posts. Only public post if user is no true friend.
   * @param jsonInput <pre><code> {
   *   "groupID":0, //optional if given uses group
   *   "userID":0, //optional if given uses user
   *   "session":"sessionID"
   * }
   * @return <pre><code> {
   *   "postList": [
   *     {
   *       "author":authorID number,
   *       "content":"content text",
   *       "id":postID number,
   *       "owner":ownerID number,
   *       "postDate":timestamp number,
   *       "title":"title text",
   *       "upVotes": [
   *         {
   *           "date":timestamp number,
   *           "voter":voterID number
   *         },
   *       ],
   *       "private":true/false
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
      if (input.containsKey("userID") && input.getInt("userID") != user.getId()) {
        // specific User
        User otherUser = new User (input.getInt("userID"));
        user.getFriendsFromDB();
        Boolean trueFriend = false;
        // check if trueFriend
        for (Entry<User,SimpleEntry<Long,Boolean>> friend : user.getFriends().entrySet()) {
          if (friend.getKey().getId() == otherUser.getId()) {
            trueFriend=friend.getValue().getValue();
          }
        }
        // not trueFriend
        if (!trueFriend) {
          List<Post> postToPrint = new ArrayList<Post>();
          for (Post post: otherUser.getPosts()) {
            if(!post.getPrivatePost()) {
              postToPrint.add(post);
            }
          }
          String entity = String.valueOf(Post.convertPostListToJson(postToPrint));
          log.debug("/post returns:" + entity);
          return Helper.okResponse(entity);
        }
        // trueFriend
        String entity = String.valueOf(Post.convertPostListToJson(otherUser.getPosts()));
        log.debug("/post returns:" + entity);
        return Helper.okResponse(entity);
        
      } else if (input.containsKey("groupID")) {
        // group
        Group group = new Group(input.getInt("groupID"));
        String entity = String.valueOf(Post.convertPostListToJson(group.getPosts()));
        log.debug("/post returns:" + entity);
        return Helper.okResponse(entity);
      }
      // own user
      String entity = String.valueOf(Post.convertPostListToJson(user.getPosts()));
      log.debug("/post returns:" + entity);
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
  
  /**
   * 
   * @param jsonInput <pre><code> {
   *   "session":"sessionID"
   * }</code></pre>
   * @return
   */
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
      String entity = String.valueOf(Post.convertPostListToJson(user.getNewsFeedFromDB()));
      log.debug("/post/newsfeed returns:" + entity);
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
  @OPTIONS @Path("/votes")
  public Response corsShowVotes() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post request to get upVotes of a post
   * @param jsonInput <pre><code> {
   *   "id":postID number,
   *   "session":"sessionID"
   * }
   * @return <pre><code> {
   *   "voteList": [
   *     {
   *       "date":voteDate number,
   *       "voter":{
   *         "firstName":firstName text,
   *         "name":name text,
   *         "username":username text
   *       }
   *     },
   *   ],
   *   "successful":true
   * } </code></pre>
   */
  @POST @Path("/votes")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showVotes(String jsonInput){
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
      Post post = new Post().setId(input.getInt("id"));
      String entity = String.valueOf(post.getUpVotesFromDB().getUpVotesAsJson());
      return Helper.okResponse(entity);
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
   *     "private":true/false
   *   }
   * }</code></pre>
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
        //hacking!!
        if (((user.getId() != 45 && user.getId() != 47) && group.getId() == 1)) {
          entity = String.valueOf(Json.createObjectBuilder()
              .add("successful", false)
              .add("reason", "You are not allowed to post in this group.")
              .build());
          log.debug("/post/add returns:" + entity);
          return Helper.okResponse(entity);
        }
        group.addPost(new Post()
              .setContent(input.getJsonObject("post").getString("content"))
              .setTitle(input.getJsonObject("post").getString("title")), user);
        log.debug("/post/add returns:" + entity);
        return Helper.okResponse(entity);
      }
      user.addPost(new Post()
            .setContent(input.getJsonObject("post").getString("content"))
            .setTitle(input.getJsonObject("post").getString("title"))
            .setPrivatePost(input.getJsonObject("post").getBoolean("private")));
      log.debug("/post/add returns:" + entity);
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
  @OPTIONS @Path("/update")
  public Response corsUpdatePost() {
    return Helper.optionsResponse();
  }
  
  /**
   * Doesn't check if the post is made by the user
   * @param jsonInput <pre><code> {
   *   "session":"sessionID"
   *   "post": {
   *     "id":0,
   *     "title":"", //optional
   *     "content":"", //optional
   *     "private":true/false //optional
   *   }
   * }</code></pre>
   * @return
   */
  @POST @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response updatePost(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post/update returns:" + entity);
        return Helper.okResponse(entity);
      }
      JsonObject jsonPost = input.getJsonObject("post");
      Post post = new Post().setId(jsonPost.getInt("id"));
      if (jsonPost.containsKey("title")) {
        post.setTitle(jsonPost.getString("title"));
      } if (jsonPost.containsKey("content")) {
        post.setContent(jsonPost.getString("content"));
      } if (jsonPost.containsKey("private")) {
        post.setPrivatePost(jsonPost.getBoolean("private"));
      }
      if (post.updateDB()) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/post/update returns:" + entity);
        return Helper.okResponse(entity);
      } else {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "No changes where made. Maybe your input is wrong")
            .build());
        log.debug("/post/update returns:" + entity);
        return Helper.okResponse(entity);
      }
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }  
  }
  
  @OPTIONS @Path("/delete")
  public Response corsDeletePost() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code> {
   *   "session":"sessionID"
   *   "id":0 //id of the doomed post
   * }</code></pre>
   * @return
   */
  @POST @Path("/delete")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response deletePost(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post/delete returns:" + entity);
        return Helper.okResponse(entity);
      }
      if (input.containsKey("id")) {
        Post post = new Post().setId(input.getInt("id"));
        post.deleteFromDB();
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", true)
            .build());
        log.debug("/post/delete returns:" + entity);
        return Helper.okResponse(entity);
      } else {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "The 'id' key is not given")
            .build());
        log.debug("/post/delete returns:" + entity);
        return Helper.okResponse(entity);
      }
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }  
  }
  
  @OPTIONS @Path("/vote/add")
  public Response corsAddVote() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "id":postID,
   *  "session":"sessionID"
   *}</code></pre>
   * @return
   */
  @POST @Path("/vote/add")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response addVote(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post/delete returns:" + entity);
        return Helper.okResponse(entity);
      }
      Post post = new Post()
          .setId(input.getInt("id"))
          .addUpVote(user);
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }  
  }
  
  @OPTIONS @Path("/vote/remove")
  public Response corsRemoveVote() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "id":postID,
   *  "session":"sessionID"
   *}</code></pre>
   * @return
   */
  @POST @Path("/vote/remove")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response removeVote(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/post/delete returns:" + entity);
        return Helper.okResponse(entity);
      }
      Post post = new Post()
          .setId(input.getInt("id"))
          .removeUpVote(user);
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }  
  }
}
