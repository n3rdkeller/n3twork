package classes;
import java.sql.Connection;
import java.util.HashMap;

public class Main {

  public static void main(String[] args) {
    try {
      Connection dbc = DBConnector.getConnection();
      User dieter = new User("dieter", "horst@dieter.de", "bla", new HashMap<String,String>());
      System.out.println("Writing serialized");
      DBConnector.writeData(dbc, "Users", dieter.getUsername(), dieter);
      System.out.println("Reading serialized");
      User gottenUser = (User)DBConnector.readData(dbc, "Users", "dieter");
      System.out.println(gottenUser.getUsername());
     } catch (Exception e) {
      System.err.println(e.toString());
    }
  }
}
