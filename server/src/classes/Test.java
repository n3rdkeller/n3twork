package classes;

public class Test {
  public static void loginTest() {
    User user = new User("dieter","","hi");
    try{
    user.login();
    }catch(Exception e) {
      System.out.println(e.toString());
    }
  }
}
