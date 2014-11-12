package classes;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {

  public static void main(String[] args) {
    //Test.loginTest();
    Random randomGenerator = new Random();
    String seed = "dieter" + randomGenerator.nextInt(1000);
    String sessionID = "";
    try {
      MessageDigest m = MessageDigest.getInstance("MD5");
      m.update(seed.getBytes("UTF-8"));
      byte[] digest = m.digest();
      BigInteger bigInt = new BigInteger(1,digest);
      String hashtext = bigInt.toString(16);
      // Now we need to zero pad it if you actually want the full 32 chars.
      while(hashtext.length() < 32 ){
        hashtext = "0"+hashtext;
      }
      sessionID = hashtext;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(sessionID);
//    try {
//      // User Tests
//      User user = new User();
//      System.out.println(user.getId());
//    /*if(user.login() != null)
//      //if(user.getFromDB())
//        System.out.println(user.getAsJson());
//*/
//      // DBConnector Tests
//      // Connection dbc = DBConnector.getConnection();
//      // DBConnector.testQuerys(dbc);
//     } catch (Exception e) {
//      System.err.println(e.toString());
//    }
  }
}
