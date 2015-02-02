/**
 * 
 */
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

import classes.Conversation;
import classes.Suggestion;
import classes.User;

/**
 * @author johannes
 *
 */
@Path("/suggestion")
public class SuggestionResource {
  final static Logger log = LogManager.getLogger(PostResource.class);
  
  /**
   * Options request for / (All options requests are identical, therefore all following are without docstring)
   * @return Response with all needed headers
   */
  @OPTIONS @Path("/network")
  public Response corsGetNetworkSuggestions() {
    return Helper.optionsResponse();
  }
  
  /**
   * 
   * @param jsonInput <pre><code>{
   *  "session":"sessionID"
   *}</code></pre>
   * @return list of users in json format
   */
  @POST @Path("/")
  @Produces(MediaType.APPLICATION_JSON)@Consumes(MediaType.APPLICATION_JSON)
  public Response showConversations(String jsonInput){
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonInput));
      JsonObject input = jsonReader.readObject();
      User user = Helper.checkSessionID(input.getString("session"));
      if (user == null){
        String entity = String.valueOf(Json.createObjectBuilder()
            .add("successful", false)
            .add("reason", "SessionID invalid")
            .build());
        log.debug("/conversation returns:" + entity);
        return Helper.okResponse(entity);
      } 
      String entity = String.valueOf(User.convertUserListToJson(Suggestion.networkSuggestion(user)));
      return Helper.okResponse(entity);
    } catch(Exception e) {
      log.error(e);
      return Helper.errorResponse(e);
    }
  }
  
}
