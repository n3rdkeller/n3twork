package classes;
import java.sql.*;

public class Main {

  public static void main(String[] args) {
    DBConnector con = new DBConnector();
    con.connect();
    con.simpleQuery();
  }
}
