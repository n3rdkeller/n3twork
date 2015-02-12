package classes;

import java.util.Comparator;

/**
 * Needed to make Post comparable by date.
 * @author johannes
 *
 */
public class PostComparator implements Comparator<Post> {
  @Override
  public int compare(Post o1, Post o2) {
    return o1.getPostDate().compareTo(o2.getPostDate());
  }
}
