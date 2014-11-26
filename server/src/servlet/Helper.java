package servlet;

import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.User;

public class Helper {
  final static Logger log = LogManager.getLogger(Helper.class);
  public static String ACCESSHEADER = "Access-Control-Allow-Origin";

  public static User checkSessionID(String sessionID){
    log.debug("checkSessionID: " + sessionID);
    User user = new User(sessionID.toCharArray());
    try {
      if (user.getBasicsFromDB()) return user;
      else return null;
    } catch (Exception e) {
      log.error(e);
      return null;
    }
  }
  
  public static Response okResponse(String entity){
    return Response.ok()
        .entity(entity)
        .header(Helper.ACCESSHEADER, "*")
        .build();
  }
  
  public static Response okResponse(String entity){
    return Response.ok()
        .entity(entity)
        .header(Helper.ACCESSHEADER, "*")
        .build();
  }
}
