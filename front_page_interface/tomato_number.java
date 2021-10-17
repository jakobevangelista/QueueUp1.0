import java.sql.*;
import java.awt.event.*;
import javax.swing.*;

import org.w3c.dom.NameList;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Enumeration;
import java.io.OutputStream;



public class tomato_number extends JFrame implements ActionListener{
    static JFrame f; 
    
  // creating a hash map to store a title as a key and castmembers as array and values;
  public static HashMap<String, ArrayList<String>> title_castmembers = new HashMap<>();
  public static HashMap<String, ArrayList<String>> pair_title = new HashMap<>();  
  public static HashMap<String, Double> title_rating = new HashMap<>();
  public static HashMap<String, Double> average_pair_rating = new HashMap<>();
  public static String tomato_num = "";
  public static String test_num = "";
  private static HashMap<String, Integer> first_time = new HashMap<>();
  private static HashMap<String, Integer> second_time = new HashMap<>();
  private static HashMap<String, Integer> third_time = new HashMap<>();
  private static HashMap<String, Integer> fourth_time = new HashMap<>();
  private static ArrayList<LocalDate> dates;
  private static ArrayList<String> ratingList;
  private static ArrayList<String> dateList;
  private static ArrayList<String> titleList;
  public static ArrayList<String> titleListtest;
  public static JTextArea label1;
  public static JTextArea input1options = new JTextArea("");
  public static JTextArea input2options = new JTextArea("");
  public static JTextArea userRatings = new JTextArea("");
  public static JTextArea tomoto_path = new JTextArea("");



  // public 
  tomato_number() {
    //Building the connection
    Connection conn = null;
    //TODO STEP 1
    try {
      Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
           "csce315903_14user", "GROUP14CS315");
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);


      // get all the titles from the user's input
      String userInput1 = "Ricochet";
      String userInput2 = "Playing God";
      String sqlstatment1 = "SELECT titleId, titletype,  originalTitle, startYear, genres FROM Content WHERE (originalTitle = '" + userInput1 + "' OR originalTitle = '" + userInput2 + "') ORDER BY originaltitle;";
      ResultSet result = stmt.executeQuery(sqlstatment1);

      // Print all available titles with the title names given
      label1 = new JTextArea("--------------------Query Results--------------------");

      input1options.append("titleId" + " | \t" + "titleType" + " | \t" + "originalTitle" + " | \t \t" + "startYear" + " | \t" + "genres" + "\n");
      while (result.next()) {
        // input1options.append(result.getString("originalTitle") + "\t" + userInput1 + "\n");
        if (result.getString("originalTitle").equals(userInput1)) {
          input1options.append(result.getString("titleId") + " | \t" +  
                          result.getString("titleType") + " | \t" +  
                          result.getString("originalTitle") + " | \t \t" + 
                          result.getString("startYear") + " | \t" + 
                          result.getString("genres") + "\n");                       //this print statement is for a content
        }
      }

      result.beforeFirst(); // go back to before the first row
      input2options.append("titleId" + " | \t" + "titleType" + " | \t" + "originalTitle" + " | \t \t" + "startYear" + " | \t" + "genres" + "\n");
      while (result.next()) {
        // input1options.append(result.getString("originalTitle") + "\t" + userInput2 + "\n");
        if (result.getString("originalTitle").equals(userInput2)) {
          input2options.append(result.getString("titleId") + " | \t" +  
                          result.getString("titleType") + " | \t" +  
                          result.getString("originalTitle") + " | \t \t" + 
                          result.getString("startYear") + " | \t" + 
                          result.getString("genres") + "\n");                       //this print statement is for a content
        }
      }

      // get title ids of all titles with the specified names
      // String title_string = "";
      // while (result.next()) {
      //   title_string += result.getString("titleId");
      // }
      // title_string = title_string.replace("{", "").replace("}", "");
      // titleList = new ArrayList<String>(Arrays.asList(title_string.split(",")));


      // Let users Pick which option of the title they want and save the two titleIds
      int user_titleId1 = 6551102;    // Ricochet
      int user_titleId2 = 8082444;    // Playing God


      // get all users who rated the specific movies
      String sqlstatment2 = "SELECT userId, rating, titleId FROM Users WHERE ('" + user_titleId1 + "' = ANY(titleId) OR '" + user_titleId2 + "' = ANY(titleId));";
      result = stmt.executeQuery(sqlstatment2);
      // userRatings.append("userId" + " | \t" + "rating" + " | \t" + "titleId" + "\n");
      // while (result.next()) {
      //   // input1options.append(result.getString("originalTitle") + "\t" + userInput2 + "\n");
      //   // if (result.getString("originalTitle").equals(userInput2)) {
      //     userRatings.append(result.getString("userId") + " | \t" +  
      //                     result.getString("rating") + " | \t" +  
      //                     result.getString("titleId")+ "\n");                       //this print statement is for a content
      // }
      // ResultSet ratiresult = stmt.executeQuery(sqlStatement1);
      String rating_string = "";
      String title_string = "";
      int title1_index = -1;
      int title2_index = -1;
      for (int i = 0; i < 1; i++) {
        result.next();
      // while (result.next()) {
        title_string += result.getString("titleId");
        
        // Putting titleIds into an array
        title_string = title_string.replace("{", "").replace("}", ",");
        titleList = new ArrayList<String>(Arrays.asList(title_string.split(",")));
        
        // Getting index of User title 1
        if (!(titleList.indexOf(String.valueOf(user_titleId1)) < 1)){
          title1_index = titleList.indexOf(String.valueOf(user_titleId1));
          System.out.println("User " + result.getString("userId") + " has title1 at index: " + title1_index);
        }

        // Getting index of User title 2
        if (!(titleList.indexOf(String.valueOf(user_titleId2)) < 1)){
          title2_index = titleList.indexOf(String.valueOf(user_titleId2));
          System.out.println("User " + result.getString("userId") + " has title2 at index: " + title2_index);
        }

        if (!(!(titleList.indexOf(String.valueOf(user_titleId1)) < 1) && (!(titleList.indexOf(String.valueOf(user_titleId2)) < 1)))){
          // If this prints, the assumption that every user has rated both movies is incorrect
          System.out.println("User " + result.getString("userId") + " did not rate both movies.");
          i--;
        }

        // Clearing the titleList for future user
        titleList.clear();
      }

      result.beforeFirst(); // go back to before the first row
      while (result.next()) {
        rating_string += result.getString("rating");

        // Putting ratings into an array
        rating_string = rating_string.replace("{", "").replace("}", ",");
        ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));

        // Getting rating of title 1 for this user
        try {
          if (Integer.valueOf(ratingList.get(title1_index)) >= 4){
            System.out.println("User " + result.getString("userId") + " rated title1:");
            System.out.println(ratingList.get(title1_index));
          } 
        } catch  (IndexOutOfBoundsException e) {
          ; // If user didn't rate this title, just continue
        }
        
        // Getting rating of title 2 for this user
        try {
          if (Integer.valueOf(ratingList.get(title2_index)) >= 4){
            System.out.println("User " + result.getString("userId") + " rated title2:");
            System.out.println(ratingList.get(title2_index));
          }
        } catch (IndexOutOfBoundsException e) {
          ; // If user didn't rate this title, just continue
        }
        
        // Clearing the ratinglist for future user
        ratingList.clear();
      }



      // getting the shortest tomato number and route
      
    


    } catch (Exception e){
      // JOptionPane.showMessageDialog(null,"Error accessing Database.");
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    /*************** try statement succeded ************************/
    JOptionPane.showMessageDialog(null, "Opened database Successfully");


    /****************** try running the class **********************/
    try{ 
      f = new JFrame("Tomato Number");
      JPanel container = new JPanel();

      JButton btn = new JButton("Close");
      // btn.addActionListener(s);
      
      JTextArea txt1 = new JTextArea(tomato_num);
      container.add(label1);
      container.add(input1options);
      container.add(input2options);
      container.add(userRatings);
      // JScrollPane scroll1 = new JScrollPane (input1options);
      // JScrollPane scroll2 = new JScrollPane (input2options);

      
      // container.add(scroll1);
      // container.add(scroll2);
      container.add(txt1);
      container.add(btn);
      f.add(container);
      f.setSize(800, 800);
      f.setVisible(true);

    } catch (Exception e){
      // JOptionPane.showMessageDialog(null, "Error accessing data");
      // f.dispose();
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    String s = e.getActionCommand();
    if (s.equals("Close")) {
      f.dispose();
    }
    else if (s.equals("<2001")) {
        
    }
  }

  public static void main(String[] args){
    //Main code is run in the class constructor
    tomato_number tn = new tomato_number();
  }

}