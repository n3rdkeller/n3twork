package servlet;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import classes.User;

public class Helper {
  final static Logger log = LogManager.getLogger(Helper.class);
  public static String ACCESSHEADER = "Access-Control-Allow-Origin";

  /**
   * Checks if sessionID is valid and gets some stuff from db
   * @param sessionID to be checked
   * @return null if sessionID is invalid
   */
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
  
  /**
   * Standard response
   * @param entity the only thing thats different for each response
   * @return standard okResponse
   */
  public static Response okResponse(String entity){
    return Response.ok()
        .entity(entity)
        .header(Helper.ACCESSHEADER, "*")
        .build();
  }
  
  /**
   * Modulizing of options requests
   * @param allowedMethods e.g. "POST, OPTIONS"
   * @return Response with all needed headers
   */
  public static Response optionsResponse(String allowedMethods){
    return Response.ok()
        .header(Helper.ACCESSHEADER, "*")
        .header("Access-Control-Allow-Methods", allowedMethods)
        .header("Access-Control-Allow-Headers", "Content-Type")
        .build();
  }
  
  /**
   * Modulizing of options requests. allowedMethods = "POST, OPTIONS"
   * @return Response with all needed headers
   */
  public static Response optionsResponse(){
    return Response.ok()
        .header(Helper.ACCESSHEADER, "*")
        .header("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT")
        .header("Access-Control-Allow-Headers", "Content-Type")
        .build();
  }
  
  public static Response errorResponse(Exception e) {
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(e.toString())
        .header(Helper.ACCESSHEADER, "*")
        .build();
  }
}
