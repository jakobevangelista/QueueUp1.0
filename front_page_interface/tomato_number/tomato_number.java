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



public class tomato_number extends JFrame implements ActionListener{
    static JFrame f; 
    
  // creating a hash map to store a title as a key and castmembers as array and values;
  public static HashMap<String, ArrayList<String>> title_castmembers = new HashMap<>();
  public static HashMap<String, ArrayList<String>> pair_title = new HashMap<>();  
  public static HashMap<String, Double> title_rating = new HashMap<>();
  public static HashMap<String, Double> average_pair_rating = new HashMap<>();
  public static String tomato_num = "Fucker";
  private static HashMap<String, Integer> first_time = new HashMap<>();
  private static HashMap<String, Integer> second_time = new HashMap<>();
  private static HashMap<String, Integer> third_time = new HashMap<>();
  private static HashMap<String, Integer> fourth_time = new HashMap<>();
  private static ArrayList<LocalDate> dates;
  private static ArrayList<String> ratingList;
  private static ArrayList<String> dateList;
  private static ArrayList<String> titleList;



  // public 
  tomato_number() {
    //Building the connection
    Connection conn = null;
    //TODO STEP 1
    try {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
          "csce315903_14user", "GROUP14CS315");

      Statement stmt = conn.createStatement();


      // get all the titles from the user's input
      // String userInput1 = "Alice in Wonderland";
      // String userInput2 = "Atlantis";
      // String sqlstatment1 = "SELECT titletype, originaltitle, startyear, genres FROM Content WHERE (originalTitle='" + userInput1 + "');";
      // String sqlstatment2 = "SELECT * FROM Content WHERE (originalTitle='" + userInput2 + "');";
      // ResultSet result1 = stmt1.executeQuery(sqlstatment1);
      // ResultSet result2 = stmt2.executeQuery(sqlstatment2);


      // Let users Pick which option of the title they want and save the two titleIds
      int user_titleId1 = 420;    // Alice in Wonderland
      int user_titleId2 = 2646;   // Atlantis

      

      // Get all the ratings from the users for each titleId
      String sqlstatment1 = "SELECT userId, rating, titleId FROM Users";
      String userId_string = "";
      String titleId_string = "";
      String rating_string = "";
      ResultSet result = stmt.executeQuery(sqlstatment1);
      while(result.next()) {
          userId_string += result.getString("userId");
          titleId_string += result.getString("titleId");
          rating_string += result.getString("rating");
      }

      String sqlstatement2 = "SELECT titleId FROM Content";
      ResultSet result1 = stmt.executeQuery(sqlstatement2);
      while(result1.next()) {
        first_time.put(result1.getString("titleId"), 0);
        second_time.put(result1.getString("titleId"), 0);
        third_time.put(result1.getString("titleId"), 0);
        fourth_time.put(result1.getString("titleId"), 0);
      }
      rating_string = rating_string.replace("{", "").replace("}", ",");
      userId_string = userId_string.replace("{", "").replace("}", ",");
      titleId_string = titleId_string.replace("{", "").replace("}", ",");
      ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
      dateList = new ArrayList<String>(Arrays.asList(userId_string.split(",")));
      titleList = new ArrayList<String>(Arrays.asList(titleId_string.split(",")));

      // for(Map.Entry<String, Double> entry : title_rating.entrySet())
      // {
      //   System.out.println(entry.getKey() + " " + entry.getValue());
      // }

      
      //putting all the titleId of the specific pair in array
      for(Map.Entry<String, ArrayList<String>> entry : title_castmembers.entrySet()){
        ArrayList<String> ary = entry.getValue();
        if (ary.size() > 1) {
          for (int i = 0; i < ary.size(); i++) {
            for (int j = i + 1; j < ary.size(); j++) {
              //making the pair going through all the array 
              String pairs = ary.get(i) + " , " + ary.get(j);
              ArrayList<String> ary_title;
              if (pair_title.containsKey(pairs)) {
                ary_title = pair_title.get(pairs);
                ary_title.add(entry.getKey());
                pair_title.put(pairs, ary_title);
              } else {
                ary_title = new ArrayList<>();
                ary_title.add(entry.getKey());
                pair_title.put(pairs, ary_title);
              }
            }
          }
        }
      }

      //getting the average rating of all of the pairs 
      for (Map.Entry<String, ArrayList<String>> entry : pair_title.entrySet()) {
        ArrayList<String> title_ary = entry.getValue();
        double sum = 0;
        int count = title_ary.size();
        if(count > 2) {
          for(String str : title_ary) {
              sum = sum + title_rating.get(str);
          }
          double average = sum/count;
          average_pair_rating.put(entry.getKey(), average);
        }
      }

      // getting the shorted tomato number and route
        
    


    } catch (Exception e){
      JOptionPane.showMessageDialog(null,"Error accessing Database.");
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
      container.add(txt1);
      container.add(btn);
      f.add(container);
      f.setSize(800, 800);
      f.setVisible(true);

    } catch (Exception e){
      JOptionPane.showMessageDialog(null, "Error accessing data");
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