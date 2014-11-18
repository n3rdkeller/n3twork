package servlet;

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
      user.getFromDB();
      return user;
    } catch (Exception e) {
      log.error(e);
      return null;
    }
  }
}
