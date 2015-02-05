/**
 * 
 */
package classes;

import gnu.trove.TDoubleArrayList;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntDoubleHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author johannes
 *
 */
public class Suggestion {

  final static Logger log = LogManager.getLogger(Suggestion.class);
  

  /**
   * Copied Method from http://javarevisited.blogspot.de/2012/12/how-to-sort-hashmap-java-by-key-and-value.html
   * Java method to sort Map in Java by value e.g. HashMap or Hashtable
   * throw NullPointerException if Map contains null values
   * It also sort values even if they are duplicates
   */
  public static <K extends Object,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
      List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
   
      Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

          @Override
          public int compare(Entry<K, V> o1, Entry<K, V> o2) {
              return o1.getValue().compareTo(o2.getValue());
          }
      });
   
      //LinkedHashMap will keep the keys in the order they are inserted
      //which is currently sorted on natural ordering
      Map<K,V> sortedMap = new LinkedHashMap<K,V>();
   
      for(Map.Entry<K,V> entry: entries){
          sortedMap.put(entry.getKey(), entry.getValue());
      }
   
      return sortedMap;
  }
  
  public static List<User> networkSuggestion(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    String sql = "SELECT Users.id, Users.username, Users.email, Users.name, Users.firstName, Friends.userID as partner "
        + "FROM " + DBConnector.DATABASE + ".Users "
        + "JOIN " + DBConnector.DATABASE + ".Friends ON Users.id = friendID "
        + "WHERE Friends.userID in ("
          + "SELECT userID from " + DBConnector.DATABASE + ".Friends WHERE friendID = ? "
          + "UNION "
          + "SELECT friendID from " + DBConnector.DATABASE + ".Friends WHERE userID = ? "
        + ") AND NOT Users.id = ? "
        + "UNION ALL "
        + "SELECT Users.id, Users.username, Users.email, Users.name, Users.firstName, Friends.friendId as partner "
        + "FROM " + DBConnector.DATABASE + ".Users "
        + "JOIN " + DBConnector.DATABASE + ".Friends ON Users.id = userID "
        + "WHERE Friends.friendID in ( "
          + "SELECT userID from " + DBConnector.DATABASE + ".Friends WHERE friendID = ? "
          + "UNION "
          + "SELECT friendID from " + DBConnector.DATABASE + ".Friends WHERE userID = ? "
        + ") AND NOT Users.id = ?";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, user.getId());
    pStmt.setInt(2, user.getId());
    pStmt.setInt(3, user.getId());
    pStmt.setInt(4, user.getId());
    pStmt.setInt(5, user.getId());
    pStmt.setInt(6, user.getId());
    
    ResultSet suggestionTable = pStmt.executeQuery();
    Map<User, Integer> suggestionMap = new HashMap<User, Integer>();
    Set<HashSet<Integer>> idPairSet = new HashSet<HashSet<Integer>>();
    whileLoop:
    while (suggestionTable.next()) {
      HashSet<Integer> newIdPair = new HashSet<Integer>();
      newIdPair.add(suggestionTable.getInt("id"));
      newIdPair.add(suggestionTable.getInt("partner"));
      for (HashSet<Integer> idPair: idPairSet) {
        if(idPair.equals(newIdPair)) {
          continue whileLoop;
        }
      }
      idPairSet.add(newIdPair);
      User toBeAdded = new User(
          suggestionTable.getInt("id"),
          suggestionTable.getString("username"),
          suggestionTable.getString("email"),
          suggestionTable.getString("name"),
          suggestionTable.getString("firstName"));
      if(suggestionMap.containsKey(toBeAdded)){
        suggestionMap.put(toBeAdded, suggestionMap.get(toBeAdded) + 1);
      } else {
        suggestionMap.put(toBeAdded, 1);
      }
    }
    conn.close();
    TIntArrayList a;
    Suggestion.sortByValues(suggestionMap);
    List<User> suggestionList = new ArrayList<User>();
    for(Entry<User, Integer> entry: suggestionMap.entrySet()) {
      suggestionList.add(entry.getKey());
    }
    return suggestionList;
  }
  
  public static List<User> postBasedSuggestion(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Connection conn = DBConnector.getConnection();
    // select the user in question and all users, that are not a friend of them
    String sql = "SELECT Posts.content, Users.id, Users.username, Users.email, Users.name, Users.firstName "
        + "FROM " + DBConnector.DATABASE + ".Posts "
        + "JOIN " + DBConnector.DATABASE + ".Users ON Posts.authorID = Users.id "
        + "WHERE Posts.authorID NOT IN ( "
          + "SELECT userID from " + DBConnector.DATABASE + ".Friends WHERE friendID = ? "
          + "UNION "
          + "SELECT friendID from " + DBConnector.DATABASE + ".Friends WHERE userID = ? "
        + ")";
    PreparedStatement pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, user.getId());
    pStmt.setInt(2, user.getId());
    ResultSet postTable = pStmt.executeQuery();
    // put postTable in Hashmap<User, ArrayList<String>>, where every word in the posts is an entry in the ArrayList<String>
    User author = new User();
    Map<User,ArrayList<String>> wordTable = new HashMap<User,ArrayList<String>>();
    ArrayList<String> wordRow = new ArrayList<String>();
    while(postTable.next()) {
      //if the author is new, then add the old one to the HashMap
      if(author.getId() != postTable.getInt("id")) {
        if(wordRow.size() != 0) {
          Collections.sort(wordRow);
          wordTable.put(author, wordRow);
        }
        wordRow = new ArrayList<String>();
        author = new User(
            postTable.getInt("id"),
            postTable.getString("username"),
            postTable.getString("email"),
            postTable.getString("name"),
            postTable.getString("firstName"));
      }
      //split the post into words and add them to wordRow
      for(String word: postTable.getString("content").split("[0123456789 +-=.,!?@#$%^&*();\\/|<>'\"\t]")) { //[\W]
        wordRow.add(word.toLowerCase());
      }
    }
    //add last author to the wordTable
    Collections.sort(wordRow);
    wordTable.put(author, wordRow);
    
    //fill the matrix
    List<String> wordList = new ArrayList<String>();
    List<TDoubleArrayList> wordUserMatrix = new ArrayList<TDoubleArrayList>();
    TDoubleArrayList matrixRow = new TDoubleArrayList();
    for(Entry<User, ArrayList<String>> row: wordTable.entrySet()) {
      matrixRow.add(row.getKey().getId());
      int wordCounter = 0;
      //count duplicates of every word in the row, which is already in the wordList
      //and save that in the matrixRow
      for(String headerWord: wordList) {
        int i = row.getValue().indexOf(headerWord);
        wordCounter = 0;
        if(i != -1) {
          while(row.getValue().get(i) == headerWord) {
            wordCounter++;
            i++;
          }
        }
        matrixRow.add(wordCounter);
      }
      
      //go through the entire row and check for every new word if it is already in the wordList.
      //if it is not, then add it to the wordList and count, how often it occurs in the row.
      Boolean addWord = false;
      String lastWord = "";
      for(String word: row.getValue()) {
        if(word == lastWord) {
          if(addWord) {
            wordCounter++;
          }
          continue;
        }
        if(!wordList.contains(word)) {
          if (addWord) {
            matrixRow.add(wordCounter);
            wordCounter = 1;
          }
          wordList.add(word);
          addWord = true;
        } else {
          addWord = false;
        }
        lastWord = word;
      }
      wordUserMatrix.add(matrixRow);
    }
    
    TIntArrayList nList = new TIntArrayList(wordList.size());
    for(int i = 1; i <= wordList.size(); i++) {
      int n = 0;
      for(TDoubleArrayList row: wordUserMatrix){
        if(row.size() >= i && row.get(i) != 0) { 
          n++;
        }
      }
      nList.add(n);
    }
    TDoubleArrayList maxList = new TDoubleArrayList(wordUserMatrix.size());
    for(TDoubleArrayList row: wordUserMatrix) {
      maxList.add(row.max());
    }
    for(int i = 1; i <= wordList.size(); i++) {
      int j = 0;
      for(TDoubleArrayList row: wordUserMatrix) {
        if(row.size() >= i && row.get(i) != 0) {
          // calculation of wij = (hij/maxk hkj)*log(N/ni)
          Double newEntry = (row.get(i)/maxList.get(j))*Math.log(wordUserMatrix.size()/nList.get(i));
          row.set(i, newEntry);
        }
        j++;
      }
    }
    return null;
  }
}
