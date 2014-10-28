package classes;

import java.sql.*;

public class DBConnector {
  private String url;
  private String username;
  private String password;
  private String database;
  private Connection con = null;
  private Statement stmt = null;
  //private PreparedStatement pStmt = null;

  /**
   * Constructor
   * @return initialized DBConnector object //is that true?
   */
  public DBConnector() {
    this.url = "jdbc:mysql://141.2.89.26";
    this.password = "LcCN8HJR";
    this.username = "TEAM_2E";
    this.database = "TEAM_2E_DB";
  }

  /**
   * Connect to mysql server
   * @return true if connection successful and false if not
   */
  public Boolean connect() {
    try {
      con = DriverManager.getConnection(
        this.url, this.username, this.password
      );
      System.out.print("Established connection to database. Gratz.");
      return true;
    } catch (Exception e) {
      System.err.print("Error establishing your dbcon. b00n.");
      return false;
    } finally {
      try {
        if (con != null) con.close();
        System.err.print("Connection closed.");
      } catch (Exception e) {
        System.err.print("Couldn't close your dbcon. SERVER OVERFLOOOOOOOOOWING!!!!111einself!");
      }
    }
  }

  /**
   * Make query to mysql server
   * @return ResultSet, a set of the queried elements
   */
  public ResultSet query() {
    return null;
  }
}
