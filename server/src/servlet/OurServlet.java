/**
 * 
 */
package servlet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rtemme
 *
 */
@ApplicationPath("/")
public class OurServlet extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        
        // register resources
        classes.add(ServletResource.class);
        
        return classes;
    }
}
