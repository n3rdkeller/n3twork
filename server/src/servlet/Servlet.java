package servlet;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Scanner;


@ApplicationPath("/api")
public class Servlet extends Application {
    static final String BASE_URI = "http://localhost:9099/";

  public static void main(String[] args) {
    ResourceConfig rc = new ResourceConfig();
    HttpServer server = JdkHttpServerFactory.createHttpServer(BASE_URI, rc);
    server.start();
    System.in.read();
    server.stop( 0 );
  }
}
