package classes;
import java.sql.Connection;
import java.util.HashMap;

public class Main {

  public static void main(String[] args) {
    try {
      Connection dbc = DBConnector.getConnection();
      User dieter = new User("dieter", "horst@dieter.de", "bla", new HashMap<String,String>());
      System.out.println("Writing");
      DBConnector.addUser(dbc, dieter);
      System.out.println("Reading");
      String username = DBConnector.readUserData(dbc,"username","dieter");
      System.out.println(username);
     } catch (Exception e) {
      System.err.println(e.toString());
    }
  }
}
