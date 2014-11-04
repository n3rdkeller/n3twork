package classes;

import java.sql.*;

public class DBConnector {
  private static String driver = "com.mysql.jdbc.Driver";
  private static String url = "jdbc:mysql://141.2.89.26";
  private static String username = "TEAM_2E";
  private static String password = "LcCN8HJR";
  private static String database = "TEAM_2E_DB";
  // this.url = "jdbc:mysql://192.168.178.51";
  // this.password = "Wurstsalat";
  // this.username = "root";

  /**
   * Static function to build connection
   * @return Connection databaseconnection
   * @throws Exception
   */
  public static Connection getConnection() throws Exception {
    Class.forName(driver).newInstance();
    Connection conn = DriverManager.getConnection(url, username, password);
    return conn;
  }

  public static void addUser(Connection conn, User user) throws Exception {
    username = user.getUsername();
    // prepare statement
    PreparedStatement pStmt = conn.prepareStatement("INSERT INTO " + database + ".Users(username,name,email) VALUES(?,?,?)");
    // set input parameter
    pStmt.setString(1,user.getUsername());
    pStmt.setString(2,user.getName());
    pStmt.setString(3,user.getEmail());
    System.out.println(pStmt);

    // do query
    pStmt.executeUpdate();
    System.out.println("executed");

    // close connection
    pStmt.close();

    System.out.println("addUser: done");
  }

  public static void updateUser(Connection conn, String collumn, String value, String username) throws Exception {
    // prepare statement
    PreparedStatement pStmt = conn.prepareStatement("UPDATE " + database + ".Users SET ? = ? WHERE username = ?");

    System.out.println(pStmt);

    // set input parameters
    pStmt.setString(1, collumn);
    pStmt.setString(2, value);
    pStmt.setString(3, username);
    System.out.println(pStmt);

    // do query
    pStmt.executeUpdate();
    System.out.println("executed");

    // close connection
    pStmt.close();
    System.out.println("writeUser: done");
  }

  public static String readUserData(Connection conn, String collumn, String username) throws Exception {
    // prepare statement
    PreparedStatement pStmt = conn.prepareStatement("SELECT ? FROM " + database + ".Users WHERE username = ?");

    // set parameters
    pStmt.setString(1, collumn);
    pStmt.setString(2, username);
    System.out.println(pStmt);

    // do query
    ResultSet rs = pStmt.executeQuery();
    rs.next();
    String userData = rs.getString(1);
    System.out.println(userData);

    rs.close();
    pStmt.close();
    System.out.println("readUserData: done");
    return userData;
  }

}
