package classes;

public class Main {

  public static void main(String[] args) {
    try {
      // User Tests
      User user = new User();
      System.out.println(user.getId());
    /*if(user.login() != null)
      //if(user.getFromDB())
        System.out.println(user.getAsJson());
*/
      // DBConnector Tests
      // Connection dbc = DBConnector.getConnection();
      // DBConnector.testQuerys(dbc);
     } catch (Exception e) {
      System.err.println(e.toString());
    }
  }
}
