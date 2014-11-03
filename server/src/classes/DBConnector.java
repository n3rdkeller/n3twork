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

  // ?1: Table; ?2: Serialized Object
  static final String WRITE_OBJECT_SQL = "INSERT INTO ?(key, value) VALUES (?, ?)";

  // ?1: Table ?2: Key
  static final String READ_OBJECT_SQL = "SELECT value FROM ? WHERE key = ?";

  //private PreparedStatement pStmt = null;

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

  /**
   * Write object to database
   * @param  conn      Connection to database
   * @param  object    Object to save in database
   * @throws Exception
   */
  public static void writeData(Connection conn, String table, String key, Object object) throws Exception {
    // set table name from class name & prepare statement
    PreparedStatement pstmt = conn.prepareStatement(WRITE_OBJECT_SQL);

    System.out.println(pstmt);

    // set input parameters
    pstmt.setString(1, table);
    System.out.println(pstmt);
    pstmt.setString(2, key);
    System.out.println(pstmt);
    pstmt.setObject(3, object);

    System.out.println(pstmt);

    // do query
    pstmt.executeUpdate();
    System.out.println("executeUpdate");

    // close connection
    pstmt.close();
    System.out.println("writeData: done serializing: " + key);
  }

  /**
   * Read object from database
   * @param  conn      Connection to database
   * @param  table     Table the object comes from
   * @param  key       Key
   * @return           Object
   * @throws Exception
   */
  public static Object readData(Connection conn, String table, String key) throws Exception {
    // prepare statement
    PreparedStatement pstmt = conn.prepareStatement(READ_OBJECT_SQL);

    // set parameters
    pstmt.setString(1, table);
    pstmt.setString(2, key);

    // do query
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    Object object = rs.getObject(1);
    String className = object.getClass().getName();

    rs.close();
    pstmt.close();
    System.out.println("readData: done de-serializing: " + className);
    return object;
  }
}
