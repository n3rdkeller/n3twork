package classes;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DBConnector {
  final static Logger log = LogManager.getLogger(DBConnector.class);
  private static String DRIVER = "com.mysql.jdbc.Driver";
//  public static String DATABASE = "TEAM_2E_DB";
//  private static String URL = "jdbc:mysql://141.2.89.26";
//  private static String USERNAME = "TEAM_2E";
//  private static String PASSWORD = "n3rdkeller sind die besten"; // old: LcCN8HJR
    public static String DATABASE = "n3twork-dev";
    private static String URL = "jdbc:mysql://10.133.251.251";
    private static String PASSWORD = "kekse sind voll n3rdig";
    private static String USERNAME = "n3twork";

  /**
   * Static function to build connection
   * @return database connection
   * @throws SQLException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public static Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    Class.forName(DRIVER).newInstance();
    Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    return conn;
  }

  /**
   * Function to execute a select query and parse the output into a matrix
   * @param conn - should be initialized with DBConnector.getConnection()
   * @param sqlQuery - any select query
   * @return the output table in matrix form
   * @throws SQLException 
   */
  public static List<ArrayList<String>> selectQuery(Connection conn, String sqlQuery) throws SQLException{
    PreparedStatement pStmt = conn.prepareStatement(sqlQuery);
    log.debug(sqlQuery);

    ResultSet rs = pStmt.executeQuery();
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnsNumber = rsmd.getColumnCount();

    List<ArrayList<String>> output  = new ArrayList<ArrayList<String>>();
    ArrayList<String> row = new ArrayList<String>();

    for (int i = 1;i <= columnsNumber; i++) {
        row.add(rsmd.getColumnName(i));
    }
    output.add(row);

    while (rs.next()) {
      row = new ArrayList<String>();
      for (int i = 1;i <= columnsNumber; i++) {
        row.add(rs.getString(i));
      }
      output.add(row);
    }

    pStmt.close();
    rs.close();
    
    return output;
  }
 
  /**
   * Function to execute insert and update queries
   * @param conn - should be initialized with DBConnector.getConnection()
   * @param sqlQuery - insert or update query
   * @return A List of the generated keys (returns empty list on update query)
   * @throws SQLException 
   */
  public static List<Integer> executeUpdate(Connection conn, String sqlQuery) throws SQLException {
    Statement stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_UPDATABLE);
    log.debug(sqlQuery);
    stmt.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);

    ResultSet rs = stmt.getGeneratedKeys();

    List<Integer> ids = new ArrayList<Integer>();
    while (rs.next()) {
      ids.add(rs.getInt(1));
    }
    stmt.close();

    String idsPrintString = "";
    for (int value : ids){
        idsPrintString = idsPrintString + value + " ";
    }
    
    return ids;

  }

  public static void testQuerys(Connection conn) throws Exception {
    DBConnector.printTestSelect(conn);
    DBConnector.printTestSelect(conn);
    //List<Integer> query = executeUpdate(conn, "insert into " + DATABASE + ".Users(username,name) values('tester','Der Tester')");
    //DBConnector.printTestSelect(conn);
    //query = DBConnector.executeUpdate(conn, "delete from " + DATABASE + ".Users where id=" + printIdString);
  }
  public static void printTestSelect(Connection conn) throws Exception {
    List<ArrayList<String>> query = DBConnector.selectQuery(conn, "select id,name,username from " + DATABASE + ".Users where id=1");
    String printString = "";
    for (ArrayList<String> row : query) {
      printString = printString + "\n";
      for (String value : row){
        printString = printString + value + " | ";
      }
    }
    log.debug(printString);
  }

}
