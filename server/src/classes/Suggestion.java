/**
 * 
 */
package classes;

import gnu.trove.TDoubleArrayList;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntDoubleHashMap;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author johannes
 *
 */
public class Suggestion {

  final static Logger log = LogManager.getLogger(Suggestion.class);
  
  /**
   * Converts any map of users with their rating to a json string
   * @param users - users in the list don't necessarily need to have it's attributes set 
   * @return <pre><code>{
   *  "userList":[
   *    {
   *      "id":userID,
   *      "username":"username",
   *      "email":"em@il",
   *      "emailhash":"hash of email",
   *      "lastName":"name",
   *      "firstName":"firstName",
   *      "rating":1,
   *      "otherProperties:{
   *        "property1":"value",
   *        "property2":"value",
   *      }
   *    },
   *  ],
   *  successful":true
   *}</code></pre>
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public static String convertUserMapToJson(Map<User, Double> users) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JsonArrayBuilder userList = Json.createArrayBuilder();
    for (Entry<User, Double> user : users.entrySet()) {
      JsonObjectBuilder otherProperties = Json.createObjectBuilder();
      for (Entry<String, String> e : user.getKey().getOtherProperties().entrySet()) {
        if (e.getValue() == null) e.setValue("");
        if (e.getValue() != "") otherProperties.add(e.getKey(), e.getValue());
      }
      userList.add(Json.createObjectBuilder()
        .add("id", user.getKey().getId())
        .add("username", user.getKey().getUsername())
        .add("email", user.getKey().getEmail())
        .add("emailhash", User.md5(user.getKey().getEmail().toLowerCase()))
        .add("lastName", user.getKey().getName())
        .add("firstName", user.getKey().getFirstName())
        .add("rating", user.getValue())
        .add("otherProperties", otherProperties));
    }
    return String.valueOf(Json.createObjectBuilder()
        .add("userList", userList)
        .add("successful", true)
        .build());
  }
  
  /**
   * 
   * @param user - user, who is requesting suggestions
   * @return suggestionList
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static Map<User, Double> networkSuggestion(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
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
    Map<Integer, Integer> suggestionMap = new HashMap<Integer, Integer>();
    Map<Integer, User> idUserMap = new HashMap<Integer, User>();
    Set<HashSet<Integer>> idPairSet = new HashSet<HashSet<Integer>>();
    while (suggestionTable.next()) {
      // wandle gerrichtete kanten in ungerrichtete kanten um
      HashSet<Integer> newIdPair = new HashSet<Integer>();
      newIdPair.add(suggestionTable.getInt("id"));
      newIdPair.add(suggestionTable.getInt("partner"));
      if(idPairSet.contains(newIdPair)) continue;
      idPairSet.add(newIdPair);
      
      // Add user to suggestionMap, if he is not already in the Map
      int userID = suggestionTable.getInt("id");
      User toBeAdded = new User(
          userID,
          suggestionTable.getString("username"),
          suggestionTable.getString("email"),
          suggestionTable.getString("name"),
          suggestionTable.getString("firstName"));
      if(suggestionMap.containsKey(userID)){
        suggestionMap.put(userID, suggestionMap.get(userID) + 1);
      } else {
        suggestionMap.put(userID, 1);
      }
      idUserMap.put(userID, toBeAdded);
    }
    log.debug(idPairSet);
    log.debug(suggestionMap);
    //select all friends of the user
    sql = "SELECT userID as id FROM " + DBConnector.DATABASE + ".Friends WHERE friendID=? "
        + "UNION "
        + "SELECT friendID as id FROM " + DBConnector.DATABASE + ".Friends WHERE userID=?";
    pStmt = conn.prepareStatement(sql);
    pStmt.setInt(1, user.getId());
    pStmt.setInt(2, user.getId());
    ResultSet friendsTable = pStmt.executeQuery();
    while(friendsTable.next()) {
      suggestionMap.remove(friendsTable.getInt("id"));
    }
    conn.close();
    Map<User, Double> suggestionList = new HashMap<User, Double>();
    for(Entry<Integer, Integer> entry: suggestionMap.entrySet()) {
      suggestionList.put(idUserMap.get(entry.getKey()), (double) (entry.getValue()));
    }
    return suggestionList;
  }
  
  /**
   * 
   * @param user
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public static Map<User, Double> postBasedSuggestion(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
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
    log.debug("postTable -> wordTable");
    // put postTable in Hashmap<Integer, ArrayList<String>>, where every word in the posts is an entry in the ArrayList<String>
    User author = new User();
    Map<Integer,ArrayList<String>> wordTable = new HashMap<Integer,ArrayList<String>>();
    Map<Integer, User> userMap = new HashMap<Integer, User>();
    ArrayList<String> wordRow = new ArrayList<String>();
    while(postTable.next()) {
      //if the author is new, then add the old one to the HashMap
      if(author.getId() != postTable.getInt("id")) {
        if(wordRow.size() != 0) {
          Collections.sort(wordRow);
          wordTable.put(author.getId(), wordRow);
          userMap.put(author.getId(), author);
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
      String[] wordArray = postTable.getString("content").split("[0-9_\\W]");
      for(String word: wordArray) { // 0-9 +-=.,!?@#$%^&*();\\\n/|<>'\"\t
        if(!word.isEmpty()){
          wordRow.add(word.toLowerCase());
        }
      }
      //log.debug(debug + "]");
      //log.debug(wordRow);
    }
    //add last author to the wordTable
    Collections.sort(wordRow);
    wordTable.put(author.getId(), wordRow);
    userMap.put(author.getId(), author);
    log.debug(wordTable);
    //fill the matrix
    int userRow = 0;
    List<String> wordList = new ArrayList<String>();
    List<TDoubleArrayList> wordUserMatrix = new ArrayList<TDoubleArrayList>();
    TDoubleArrayList matrixRow = new TDoubleArrayList();
    for(Entry<Integer, ArrayList<String>> row: wordTable.entrySet()) {
      matrixRow = new TDoubleArrayList();
      matrixRow.add(row.getKey());
      if (row.getKey() == user.getId()) {
        userRow = wordUserMatrix.size(); // needed later
      }
      int wordCounter = 0;
      //count duplicates of every word in the row, which is already in the wordList
      //and save that in the matrixRow
      for(String headerWord: wordList) {
        int i = row.getValue().indexOf(headerWord);
        wordCounter = 0;
        if(i != -1) {
          while(row.getValue().get(i).equals(headerWord)) {
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
        if(word.equals(lastWord)) {
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
          wordCounter = 1;
          addWord = true;
        } else {
          addWord = false;
        }
        lastWord = word;
      }
      if(wordCounter != 0) matrixRow.add(wordCounter);
      wordUserMatrix.add(matrixRow);
    }
    log.debug("wordUserMatrix " + wordUserMatrix);
    log.debug("fill nList");
    TIntArrayList nList = new TIntArrayList(wordList.size());
    for(int i = 1; i <= wordList.size(); i++) {
      int n = 0;
      for(TDoubleArrayList row: wordUserMatrix){
        if(i < row.size() && n < row.get(i)) { 
          n = (int) row.get(i);
        }
      }
      nList.add(n);
    }
    log.debug(nList);
    log.debug("fill maxList");
    TDoubleArrayList maxList = new TDoubleArrayList();
    for(int i = 0; i < wordUserMatrix.size(); i++) {
      TDoubleArrayList row = wordUserMatrix.get(i);
      Double max = 0.0;
      for(int j = 1; j < row.size(); j++) {
        if(max < row.get(j)) max = row.get(j);
      }
      maxList.add(max);
    }
    log.debug(maxList);
    log.debug("calculation of wij");
    int j = 0;
    for(TDoubleArrayList row: wordUserMatrix) {
      for(int i = 1; i <= wordList.size(); i++) {
        if(i < row.size() && row.get(i) != 0) {
          // calculation of wij = (hij/maxj)*log(N/ni)
          Double newEntry = (row.get(i)/maxList.get(j))*Math.log(wordUserMatrix.size()/nList.get(i-1));
          row.set(i, newEntry);
        }
      }
      j++;
    }
    log.debug("Calculate userSum: sqrt(sum(wi^2))");
    Double userSum = 0.0;
    for (int i = 1; i < wordUserMatrix.get(userRow).size(); i++) {
      userSum = userSum + Math.pow(wordUserMatrix.get(userRow).get(i), 2);
    }
    userSum = Math.sqrt(userSum);
    log.debug("Calc userRatingMap");
    Map<User, Double> userRatingMap = new HashMap<User, Double>();
    for(TDoubleArrayList row: wordUserMatrix) {
      if(row.get(0) == user.getId()) continue;
      Double rowSum = 0.0;
      for (int i = 1; i < row.size(); i++) {
        rowSum = rowSum + Math.pow(row.get(i), 2);
      }
      rowSum = Math.sqrt(rowSum);
      log.debug("rowSum: " + rowSum);
      Double productSum = 0.0;
      for (int i = 1; i < Math.min(row.size(), wordUserMatrix.get(userRow).size()); i++) {
        productSum = productSum + row.get(i) * wordUserMatrix.get(userRow).get(i);
      }
      log.debug("productSum: " + productSum);
      Double sim = productSum/(userSum*productSum);
        userRatingMap.put(userMap.get((int) (row.get(0))), productSum/(userSum*rowSum));
    }
    return userRatingMap;
  }
}
