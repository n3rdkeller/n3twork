package servlet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.Group;
import classes.Post;
import classes.User;

/**
 * Post related part of the api
 * @author johannes
 *
 */
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
          for (Post post: otherUser.getPosts(user)) {
            if(!post.getPrivatePost()) {
              postToPrint.add(post);
            }
          }
          String entity = String.valueOf(Post.convertPostListToJson(postToPrint, false));
          log.debug("/post returns:" + entity);
          return Helper.okResponse(entity);
        }
        // trueFriend
        String entity = String.valueOf(Post.convertPostListToJson(otherUser.getPosts(user), false));
        log.debug("/post returns:" + entity);
        return Helper.okResponse(entity);
        
      } else if (input.containsKey("groupID")) {
        // group
        Group group = new Group(input.getInt("groupID"));
        String entity = String.valueOf(Post.convertPostListToJson(group.getPosts(user), true));
        log.debug("/post returns:" + entity);
        return Helper.okResponse(entity);
      }
      // own user
      String entity = String.valueOf(Post.convertPostListToJson(user.getPosts(user), false));
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
   * Gets the newsfeed for the current user
   * @param jsonInput <pre><code> {
   *   "session":"sessionID"
   * }</code></pre>
   * @return <pre><code> {
   *   "postList": [
   *     {
   *       "author":authorID number,
   *       "content":"content text",
   *       "id":postID number,
   *       "owner":ownerID number,
   *       "postDate":timestamp number,
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
      String entity = String.valueOf(Post.convertPostListToJson(user.getNewsFeedFromDB(),true));
      log.debug("/post/newsfeed returns:" + entity);
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
   * Save new post
   * @param jsonInput <pre><code> {
   *   "groupID":0, //optional if given uses group
   *   "userID":0, //optional if given uses user
   *   "session":"sessionID"
   *   "post": {
   *     "content":"",
   *     "private":true/false
   *   }
   * }</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
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
              .setContent(input.getJsonObject("post").getString("content")), user);
        log.debug("/post/add returns:" + entity);
        return Helper.okResponse(entity);
      }
      user.addPost(new Post()
            .setContent(input.getJsonObject("post").getString("content"))
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
   * Doesn't check if the post is made by the user, but edits a post.
   * @param jsonInput <pre><code> {
   *   "session":"sessionID"
   *   "id":0,
   *   "content":"", //optional
   *   "private":true/false //optional
   * }</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
   */
  @PUT @Path("/update")
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
      Post post = new Post().setId(input.getInt("id"));
      if (input.containsKey("content")) {
        post.setContent(input.getString("content"));
      } if (input.containsKey("private")) {
        post.setPrivatePost(input.getBoolean("private"));
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
   * Delete a post
   * @param jsonInput <pre><code> {
   *   "session":"sessionID"
   *   "id":0 //id of the doomed post
   * }</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
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
    
  @OPTIONS @Path("/vote/add")
  public Response corsAddVote() {
    return Helper.optionsResponse();
  }
  
  /**
   * Vote a post
   * @param jsonInput <pre><code>{
   *  "id":postID,
   *  "session":"sessionID"
   *}</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
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
        log.debug("/vote/add returns:" + entity);
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
   * "Unvote" a post
   * @param jsonInput <pre><code>{
   *  "id":postID,
   *  "session":"sessionID"
   *}</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
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
        log.debug("/vote/remove returns:" + entity);
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
  
  @OPTIONS @Path("/comments")
  public Response corsShowComments() {
    return Helper.optionsResponse();
  }
  
  /**
   * Post request to get upVotes of a post
   * @param jsonInput <pre><code> {
   *   "id":postID number,
   *   "session":"sessionID"
   * }
   * @return <pre><code> {
   *   "commentList": [
   *     {
   *       "id":commentID,
   *       "date":commentDate number,
   *       "author":{
   *         "firstName":"firstName text",
   *         "lastName":"name text",
   *         "username":"username text"
   *       },
   *       "content":"content text"
   *     },
   *   ],
   *   "successful":true
   * } </code></pre>
   */
  @POST @Path("/comments")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showComments(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/comments returns:" + entity);
        return Helper.okResponse(entity);
      } 
      Post post = new Post().setId(input.getInt("id"));
      String entity = String.valueOf(post.getCommentsFromDB().getCommentsAsJson());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
    
  @OPTIONS @Path("/comment/add")
  public Response corsAddComment() {
    return Helper.optionsResponse();
  }
  
  /**
   * Save a new comment
   * @param jsonInput <pre><code>{
   *  "id":postID,
   *  "session":"sessionID",
   *  "content":"content text"
   *}</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
   */
  @POST @Path("/comment/add")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response addComment(String jsonInput) {
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      if (!input.containsKey("id") || !input.containsKey("session") || !input.containsKey("content")) {
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "Not ever mandatory key is given.")
            .build());
        log.debug("/comment/add returns:" + entity);
        return Helper.okResponse(entity);
      }
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/comment/add returns:" + entity);
        return Helper.okResponse(entity);
      }
      Post post = new Post()
          .setId(input.getInt("id"))
          .addComment(user, input.getString("content"));
      String entity = String.valueOf(Json.createObjectBuilder()
          .add("successful", true)
          .build());
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }  
  }
  
  @OPTIONS @Path("/comment/remove")
  public Response corsRemoveComment() {
    return Helper.optionsResponse();
  }
  
  /**
   * Remove a comment, if the user is the author of the comment or the post
   * @param jsonInput <pre><code>{
   *  "commentID":commentID,
   *  "session":"sessionID"
   *}</code></pre>
   * @return <pre><code> {
   *   "successful":true
   * }</code></pre>
   */
  @POST @Path("/comment/remove")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response removeComment(String jsonInput) {
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
          .removeComment(input.getInt("commentID"),user);
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
