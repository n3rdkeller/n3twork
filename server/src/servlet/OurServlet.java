/**
 * 
 */
package servlet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Class needed to register resources.
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
        classes.add(UserResource.class);
        classes.add(GroupResource.class);
        classes.add(PostResource.class);
        classes.add(ConversationResource.class);
        classes.add(SuggestionResource.class);
        
        return classes;
    }

}
