package classes;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
public class DBConnector {
  private static String DRIVER = "com.mysql.jdbc.Driver";
  private static String DATABASE = "TEAM_2E_DB";
  private static String URL = "jdbc:mysql://141.2.89.26";
  private static String USERNAME = "TEAM_2E";
  private static String PASSWORD = "n3rdkeller sind die besten"; // old: LcCN8HJR
  // private static String URL = "jdbc:mysql://192.168.178.51";
  // private static String PASSWORD = "Wurstsalat";
  // private static String USERNAME = "root";

  /**
   * Static function to build connection
   * @return Connection database connection
   * @throws Exception
   */
  public static Connection getConnection() throws Exception {
    Class.forName(DRIVER).newInstance();
    Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    return conn;
  }

  public static List<ArrayList<String>> select(Connection conn, String sqlQuery) throws Exception {
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    System.out.println(pStmt);

    ResultSet rs = pStmt.executeQuery();
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnsNumber = rsmd.getColumnCount();
    System.out.println("executed");

    List<ArrayList<String>> output  = new ArrayList<ArrayList<String>>();
    while (rs.next()) {
      ArrayList<String> row = new ArrayList<String>();
        for (int i = 1;i <= columnsNumber; i++) {
        row.add(rs.getString(i));
      }
      output.add(row);
    }

    pStmt.close();
    rs.close();

    System.out.println("selectQuery: done");
    return output;

  }

  public static List<Integer> insertQuery(Connection conn, String sqlQuery) throws Exception {
    Statement stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_UPDATABLE);
    System.out.println(stmt);
    stmt.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);

    ResultSet rs = stmt.getGeneratedKeys();
    System.out.println("executed");

    List<Integer> ids = new ArrayList<Integer>();
    while (rs.next()) {
      ids.add(rs.getInt(1));
    }
    stmt.close();

    System.out.println("insertQuery: done");
    return ids;

  }

  public static void testQuerys(Connection conn) throws Exception {
    DBConnector.printTestSelect(conn);
    List<Integer> query = insertQuery(conn, "insert into " + DATABASE + ".Users(username,name) values('tester','Der Tester')");
    String printIdString = "" + query.get(0);
    System.out.println(printIdString);
    DBConnector.printTestSelect(conn);
    query = DBConnector.insertQuery(conn, "delete from " + DATABASE + ".Users where id=" + printIdString);
  }
  public static void printTestSelect(Connection conn) throws Exception {
    List<ArrayList<String>> query = DBConnector.select(conn, "select id,name,username from " + DATABASE + ".Users");
    String printString = "";
    for (ArrayList<String> row : query) {
      for (String value : row){
        printString = printString + value + " | ";
      }
      printString = printString + "\n";
    }
    System.out.println(printString);
  }

  /*public static void updateData(Connection conn, String table, String collumn, String value, Int id) {
    // prepare statement
    PreparedStatement pStmt = conn.prepareStatement(MYSQL_UPDATE);

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
  public static void addUser(Connection conn, User user) throws Exception {
    // prepare statement
    PreparedStatement pStmt = conn.prepareStatement("INSERT INTO " + DATABASE + ".Users(username,name,email) VALUES(?,?,?)");
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
    PreparedStatement pStmt = conn.prepareStatement("UPDATE " + DATABASE + ".Users SET ? = ? WHERE username = ?");

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
    PreparedStatement pStmt = conn.prepareStatement("SELECT ? FROM " + DATABASE + ".Users WHERE username = ?");

    // set parameters
    pStmt.setString(1, collumn);
    pStmt.setString(2, username);
    System.out.println(pStmt);

    String userData = "";
    // do query
    ResultSet rs = pStmt.executeQuery();

    if (rs.isBeforeFirst()) {
      rs.next();
      userData = rs.getString(1);
    }

    System.out.println("Result: " + userData);

    rs.close();
    pStmt.close();
    System.out.println("readUserData: done");
    return userData;
  }*/

}
