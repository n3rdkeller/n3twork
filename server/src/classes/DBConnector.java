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
   * @return initialized DBConnector object // is that true? yes
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
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      con = DriverManager.getConnection(
        this.url, this.username, this.password
      );
      System.out.println("Established connection to database.");
      return true;
    } catch (Exception e) {
      System.err.println(e.toString());
      System.err.println("Error establishing your dbcon. b00n.");
      return false;
    } finally {
      try {
        if (con != null) con.close();
        System.out.println("Connection closed.");
      } catch (Exception e) {
        System.err.println("Couldn't close your dbcon. SERVER OVERFLOOOOOOOOWING!!!!111einself!");
        System.err.println(e.toString());
      }
    }
  }

  public void simpleQuery() {
    System.out.println("Actually doing something1");
    try {
    Statement stmt = null;
    String query = "select * from Users";

        stmt = con.createStatement();
        System.out.println("Actually doing something");
        ResultSet rs = stmt.executeQuery(query);
        System.out.println(rs.getString("ID"));
        // while (rs.next()) {
        //     String coffeeName = rs.getString("COF_NAME");
        //     int supplierID = rs.getInt("SUP_ID");
        //     float price = rs.getFloat("PRICE");
        //     int sales = rs.getInt("SALES");
        //     int total = rs.getInt("TOTAL");
        //     System.out.println(coffeeName + "\t" + supplierID +
        //                        "\t" + price + "\t" + sales +
        //                        "\t" + total);
        // }
    } catch (SQLException e ) {
      System.err.println(e.getSQLState());
    } finally {
        try {
         if (stmt != null) { stmt.close(); }
        } catch (Exception e) {
          System.err.println(e.toString());
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
