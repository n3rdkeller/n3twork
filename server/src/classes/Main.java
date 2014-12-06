package classes;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {
  final static Logger log = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    //Test.loginTest();
//    Random randomGenerator = new Random();
//    String seed = "dieter" + randomGenerator.nextInt(1000);
//    String sessionID = "";
//    try {
//      MessageDigest m = MessageDigest.getInstance("MD5");
//      m.update(seed.getBytes("UTF-8"));
//      byte[] digest = m.digest();
//      BigInteger bigInt = new BigInteger(1,digest);
//      String hashtext = bigInt.toString(16);
//      // Now we need to zero pad it if you actually want the full 32 chars.
//      while(hashtext.length() < 32 ){
//        hashtext = "0"+hashtext;
//      }
//      sessionID = hashtext;
//    } catch (UnsupportedEncodingException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch (NoSuchAlgorithmException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    System.out.println(sessionID);
    Test.loginTest();
  }
}
