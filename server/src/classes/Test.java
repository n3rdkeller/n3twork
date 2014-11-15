package classes;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import servlet.ServletResource;

public class Test {

  final static Logger log = LogManager.getLogger(ServletResource.class);
  public static void loginTest() {
    User user = new User("dieter","","hi");
    try{
      user.login();
    }catch(Exception e) {
      log.error(e);
    }
  }
}
