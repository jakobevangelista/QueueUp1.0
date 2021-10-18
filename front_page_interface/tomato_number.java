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
  public static String tomato_num = "";
  public static String test_num = "";
  private static ArrayList<LocalDate> dates;
  private static ArrayList<String> ratingList;
  private static ArrayList<String> dateList;
  private static ArrayList<String> titleList;
  private static ArrayList<String> titleList2;
  public static ArrayList<String> titleListtest;
  public static JTextArea label1;
  public static JTextArea input1options = new JTextArea("");
  public static JTextArea input2options = new JTextArea("");
  public static JTextArea userRatings = new JTextArea("");
  // public static JTextArea tomato_path = new JTextArea("");
  public static String tomato_path = "";
  public static String userInput1 = "";
  public static String userInput2 = "";

  public <String> ArrayList<String> intersection(ArrayList<String> list1, ArrayList<String> list2) {
    ArrayList<String> list = new ArrayList<String>();

    for (String t : list1) {
      if(list2.contains(t)) {
        list.add(t);
      }
    }

    return list;
  }

  public tomato_number() {
    //Building the connection
    Connection conn = null;
    //TODO STEP 1
    try {
      Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
           "csce315903_14user", "GROUP14CS315");
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);


      // // get all the titles from the user's input
      String userInput1 = "Ricochet";
      String userInput2 = "Playing God";
      // String sqlstatment1 = "SELECT titleId, titletype,  originalTitle, startYear, genres FROM Content WHERE (originalTitle = '" + userInput1 + "' OR originalTitle = '" + userInput2 + "') ORDER BY originaltitle;";
      // ResultSet result = stmt.executeQuery(sqlstatment1);

      // // Print all available titles with the title names given
      // label1 = new JTextArea("--------------------Query Results--------------------");

      // input1options.append("titleId" + " | \t" + "titleType" + " | \t" + "originalTitle" + " | \t \t" + "startYear" + " | \t" + "genres" + "\n");
      // while (result.next()) {
      //   if (result.getString("originalTitle").equals(userInput1)) {
      //     input1options.append(result.getString("titleId") + " | \t" +  
      //                     result.getString("titleType") + " | \t" +  
      //                     result.getString("originalTitle") + " | \t \t" + 
      //                     result.getString("startYear") + " | \t" + 
      //                     result.getString("genres") + "\n");                       //this print statement is for a content
      //   }
      // }

      // result.beforeFirst(); // go back to before the first row
      // input2options.append("titleId" + " | \t" + "titleType" + " | \t" + "originalTitle" + " | \t \t" + "startYear" + " | \t" + "genres" + "\n");
      // while (result.next()) {
      //   if (result.getString("originalTitle").equals(userInput2)) {
      //     input2options.append(result.getString("titleId") + " | \t" +  
      //                     result.getString("titleType") + " | \t" +  
      //                     result.getString("originalTitle") + " | \t \t" + 
      //                     result.getString("startYear") + " | \t" + 
      //                     result.getString("genres") + "\n");                       //this print statement is for a content
      //   }
      // }


      // Let users Pick which option of the title they want and save the two titleIds
      int user_titleId1 = 23563;
      int user_titleId2 = 304678;  

      
      // get all users who rated the specific movies
      String sqlstatment2 = "SELECT userId, rating, titleId FROM Users WHERE ('" + user_titleId1 + "' = ANY(titleId) OR '" + user_titleId2 + "' = ANY(titleId));";
      ResultSet result = stmt.executeQuery(sqlstatment2);
      String rating_string = "";
      String title_string = "";
      int title1_index = -1;
      int title2_index = -1;
      int title3_index = -1;
      ArrayList<String> userList1 = new ArrayList<String>(); // list of all users who rated title1 above a 3
      ArrayList<String> userList2 = new ArrayList<String>(); // list of all users who rated title2 above a 3
      ArrayList<String> userList3 = new ArrayList<String>(); // list of all users who rated title3 above a 3
      ArrayList<String> possibleTitle3List = new ArrayList<String>(); // list of all users who rated title3 above a 3
      ArrayList<String> intersectList1 = new ArrayList<String>(); // list of all users who have titles 1 and 3 in common
      ArrayList<String> intersectList2 = new ArrayList<String>(); // list of all users who have titles 3 and 2 in common
      
      while (result.next()) {
        // Putting titleIds into an array
        title_string += result.getString("titleId");
        title_string = title_string.replace("{", "").replace("}", ",");
        titleList = new ArrayList<String>(Arrays.asList(title_string.split(",")));
        // possibleTitle3List = titleList;
        
        // Getting index of User title 1
        if (!(titleList.indexOf(String.valueOf(user_titleId1)) < 1)){
          title1_index = titleList.indexOf(String.valueOf(user_titleId1));
          // System.out.println(result.getString("userId") + ": Title1 index = " + title1_index);
        }

        // Getting index of User title 2
        if (!(titleList.indexOf(String.valueOf(user_titleId2)) < 1)){
          title2_index = titleList.indexOf(String.valueOf(user_titleId2));
          // System.out.println(result.getString("userId") + ": Title2 index = " + title2_index);
        }

        // Putting ratings into an array
        rating_string += result.getString("rating");
        rating_string = rating_string.replace("{", "").replace("}", ",");
        ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));

        // Getting rating of title 1 for this user
        try {
          if (Integer.valueOf(ratingList.get(title1_index)) >= 4){
            userList1.add(result.getString("userId"));
            // System.out.println(result.getString("userId") + ": Title1 rating = " + ratingList.get(title1_index));
          } else {
            // System.out.println(result.getString("userId") + " didn't rate title 1 higher than a 3");
          }
        } catch  (IndexOutOfBoundsException e) {
          ; // If user didn't rate this title, just continue
          // System.out.println(result.getString("userId") + " didn't rate title 1");
        }
        
        // Getting rating of title 2 for this user
        try {
          if (Integer.valueOf(ratingList.get(title2_index)) >= 4){
            userList2.add(result.getString("userId"));
            // System.out.println(result.getString("userId") + ": Title2 rating = " + ratingList.get(title2_index));
          } else {
            // System.out.println(result.getString("userId") + " didn't rate title 2 higher than a 3");
          }
        } catch (IndexOutOfBoundsException e) {
          ; // If user didn't rate this title, just continue
          // System.out.println(result.getString("userId") + " didn't rate title 2");
        }


        // Clearing the variables for future user
        title_string = "";
        title1_index = -1;
        title2_index = -1;
        // titleList.clear();
        rating_string = "";
        ratingList.clear();
      }

      // System.out.println(titleList);
      String title3Id;
      for (int i = 0; i < titleList.size()-1; i++) {
        title3Id = titleList.get(i);
        // System.out.println("Id of title at " + i + " = " + titleList.get(i));
        sqlstatment2 = "SELECT userId, rating, titleId FROM Users WHERE ('" + titleList.get(i) + "' = ANY(titleId));";
        result = stmt.executeQuery(sqlstatment2);
        while (result.next()) {
          // Putting titleIds into an array
          title_string += result.getString("titleId");
          title_string = title_string.replace("{", "").replace("}", ",");
          titleList2 = new ArrayList<String>(Arrays.asList(title_string.split(",")));
          // System.out.println("TitleList = " + titleList2);
          
          // Getting index of User title
          if (!(titleList2.indexOf(String.valueOf(titleList.get(i))) < 1)){
            title3_index = titleList2.indexOf(String.valueOf(titleList.get(i)));
            // System.out.println(result.getString("userId") + ": Title3 index = " + title3_index);
          }
  
          // Putting ratings into an array
          rating_string += result.getString("rating");
          rating_string = rating_string.replace("{", "").replace("}", ",");
          ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
  
          // Getting rating of title for this user
          try {
            if (Integer.valueOf(ratingList.get(title3_index)) >= 4){
              userList3.add(result.getString("userId"));
              // System.out.println(result.getString("userId") + ": Title1 rating = " + ratingList.get(title1_index));
            } else {
              // System.out.println(result.getString("userId") + " didn't rate title 1 higher than a 3");
            }
          } catch  (IndexOutOfBoundsException e) {
            ; // If user didn't rate this title, just continue
            // System.out.println(result.getString("userId") + " didn't rate title 1");
          }
  
  
          // Clearing the variables for future user
          title_string = "";
          title3_index = -1; // t
          titleList2.clear();
          rating_string = "";
          ratingList.clear();
        }

        // Now we have userList3
        intersectList1 = intersection(userList1, userList3);
        intersectList2 = intersection(userList3, userList2);
        if (intersectList1.isEmpty() || intersectList2.isEmpty()) {
          System.out.println("Intersect List is Empty, NO INTERSECTION, going on to next title");
          continue;
        } else {
          System.out.println("Found intersection between all of them!");
          // Getting names of titles
          String origName1 = "";
          String origName2 = "";
          String origName3 = "";
          sqlstatment2 = "SELECT titleId, originalTitle FROM Content WHERE (titleId = " + user_titleId1 + " OR titleId = " + title3Id + " OR titleId = " + user_titleId2 + ");";
          result = stmt.executeQuery(sqlstatment2);
          while (result.next()) {
            if (result.getString("titleId").equals(String.valueOf(user_titleId1))){
              origName1 = result.getString("originalTitle");
            }
            if (result.getString("titleId").equals(title3Id)){
              origName2 = result.getString("originalTitle");
            }
            if (result.getString("titleId").equals(String.valueOf(user_titleId2))){
              origName3 = result.getString("originalTitle");
            }
          }
          // getting the shortest tomato number and route


          /*********************************** tomato_path ************************************************** */
          tomato_path = origName1 + "-->" + intersectList1.get(0) + "-->" + origName2 +  "-->" + intersectList2.get(0) + "-->" + origName3;
          /************************************************************************************************** */

          
          System.out.println(tomato_path);
          break;
        }

      }

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
      // container.add(tomato_path);
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