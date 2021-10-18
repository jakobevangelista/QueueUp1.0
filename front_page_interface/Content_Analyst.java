package project2;
import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

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

public class Content_Analyst extends JFrame implements ActionListener {
    static JFrame contentAnalyst;

    //all arrays are important for gather watch trends and to be used within other functions
    private static ArrayList<LocalDate> dates;
    private static ArrayList<String> ratingList;
	  private static ArrayList<String> dateList;
    private static ArrayList<String> titleList;
    private static ArrayList<String> titleList2;
    public static ArrayList<String> titleListtest;
    private static HashMap<String, Integer> first_time = new HashMap<>();
    private static HashMap<String, Integer> second_time = new HashMap<>();
    private static HashMap<String, Integer> third_time = new HashMap<>();
    private static HashMap<String, Integer> fourth_time = new HashMap<>();
    private static ArrayList<String> first_list = new ArrayList<String>(10);
    private static ArrayList<String> second_list = new ArrayList<String>(10);
    private static ArrayList<String> third_list = new ArrayList<String>(10);
    private static ArrayList<String> fourth_list = new ArrayList<String>(10);
    public static Statement stmt;
    public static String nameList1 = "";
    public static String nameList2 = "";
    public static String nameList3 = "";
    public static String nameList4 = "";

    //all arrays will be used for developing hollywood pairs
    public static HashMap<String, ArrayList<String>> title_castmembers = new HashMap<>();
    public static HashMap<String, ArrayList<String>> pair_title = new HashMap<>();  
    public static HashMap<String, Double> title_rating = new HashMap<>();
    public static HashMap<String, Double> average_pair_rating = new HashMap<>();
    public static ArrayList<String> topPairFinal = new ArrayList<String>();
    public static String user_titleId1;
    public static String user_titleId2;

    //This function is the constructor which will be called in other classes, it will start all database operations and eventually call the JFrame
    public Content_Analyst(){
      Connection conn = null;
      JOptionPane.showMessageDialog(null, "Developing Watch Trends...");
      try {
          Class.forName("org.postgresql.Driver");
          conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db", "csce315903_14user", "GROUP14CS315");
          stmt = conn.createStatement();
          String sqlstatment1 = "SELECT * FROM Users";
          String date_string = "";
          String title_string = "";
          String rating_string = "";
          ResultSet result = stmt.executeQuery(sqlstatment1);
          while(result.next()){
              date_string += result.getString("date");
              title_string += result.getString("titleid");
              rating_string += result.getString("rating");
              
          }

          String sqlstatement2 = "SELECT titleid FROM Content";
          ResultSet result1 = stmt.executeQuery(sqlstatement2);
          while(result1.next())
          {
              first_time.put(result1.getString("titleid"), 0);
              second_time.put(result1.getString("titleid"), 0);
              third_time.put(result1.getString("titleid"), 0);
              fourth_time.put(result1.getString("titleid"), 0);
          }
          rating_string = rating_string.replace("{", "").replace("}", ",");
          date_string = date_string.replace("{", "").replace("}", ",");
          title_string = title_string.replace("{", "").replace("}", ",");
          ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
          dateList = new ArrayList<String>(Arrays.asList(date_string.split(",")));
          titleList = new ArrayList<String>(Arrays.asList(title_string.split(",")));
  
          date_creating();
          hashing();
          
          HashMap<String, Integer> first_sorted = sort_hash(first_time);
          HashMap<String, Integer> second_sorted = sort_hash(second_time);
          HashMap<String, Integer> third_sorted = sort_hash(third_time);
          HashMap<String, Integer> fourth_sorted = sort_hash(fourth_time);
   
          int counter = 0;        
          for(HashMap.Entry<String, Integer> set: first_sorted.entrySet()){
              first_list.add(set.getKey());
              counter++;
              if(counter == 10){
                  break;
              }
          } 

          counter = 0;
          for(HashMap.Entry<String, Integer> set: second_sorted.entrySet()){
            second_list.add(set.getKey());
            counter++;
            if(counter == 10){
              break;
            }
          } 

          counter = 0;
          for(HashMap.Entry<String, Integer> set: third_sorted.entrySet()){
            third_list.add(set.getKey());
            counter++;
            if(counter == 10){
              counter = 0;
              break;
            }
          } 

          counter = 0;
          for(HashMap.Entry<String, Integer> set: fourth_sorted.entrySet()){
            fourth_list.add(set.getKey());
            counter++;
            if(counter == 10){
              break;
            }
          } 
          nameList1 = querying(first_list, stmt, nameList1);
          nameList2 = querying(second_list, stmt, nameList2);
          nameList3 = querying(third_list, stmt, nameList3);
          nameList4 = querying(fourth_list, stmt, nameList4);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }

      try{
          conn.close();
      } catch(Exception e) {
          JOptionPane.showMessageDialog(null,e);
      }

      //Running the rest of the functions, the code before was for developing the watch trends
      JOptionPane.showMessageDialog(null, "Developing Hollywood Pairs...");
      hollywood_pair();
      JOptionPane.showMessageDialog(null, "Developing Rotten Tomatoes...");
      user_titleId1 = JOptionPane.showInputDialog(null, "Please Enter titleId 1");
      user_titleId2 = JOptionPane.showInputDialog(null, "Please Enter titleId 2");
      tomato_number();
      JOptionPane.showMessageDialog(null, "Done");
      callGUI();
    }

    public static String tomato_num = "";
    public static String test_num = "";
    public static String tomato_path = "";

    //Gets the lists 
    public static String querying(ArrayList<String> list, Statement state, String result ){
      for(Object var: list){
        try{
        String statement = "SELECT originaltitle FROM Content where (titleid = \'" + var + "\' )";
        ResultSet res = state.executeQuery(statement);
        while(res.next()){
          result += res.getString("originalTitle")  + "\n" ;
        }
      }catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
    }
      return result;
    }



    public void tomato_number() {
      //Building the connection
      Connection conn = null;
      //TODO STEP 1
      try {
        Class.forName("org.postgresql.Driver");
          conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
             "csce315903_14user", "GROUP14CS315");
          Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
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
          
          // Getting index of User title 1
          if (!(titleList.indexOf(String.valueOf(user_titleId1)) < 1)){
            title1_index = titleList.indexOf(String.valueOf(user_titleId1));
          }
  
          // Getting index of User title 2
          if (!(titleList.indexOf(String.valueOf(user_titleId2)) < 1)){
            title2_index = titleList.indexOf(String.valueOf(user_titleId2));

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
    }

    //This function returns the actors/actresses with the highest chemistry rating
    static public void hollywood_pair(){
      Connection conn = null;
      try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
           "csce315903_14user", "GROUP14CS315");
        Statement stmt = conn.createStatement();
        
        //getting all the contents titleid, rating, originaltitle 
        String sqlstatment1 = "SELECT Content.titleid, Content.originalTitle, Content.averageRating, CastMembers.nameId, namelookup.primaryname FROM Content INNER JOIN CastMembers ON Content.titleId=CastMembers.titleId INNER JOIN Namelookup ON Namelookup.nameid = castmembers.nameid WHERE (castmembers.category = 'actor' OR castmembers.category = 'actress');";
        ResultSet result = stmt.executeQuery(sqlstatment1);
        while(result.next()){
            String content = result.getString("titleid");
            ArrayList<String> ary;
            
            if(title_castmembers.get(content) != null){
                ary = title_castmembers.get(content);
                ary.add(result.getString("primaryName"));
                title_castmembers.put(content, ary);
                double rating = Double.parseDouble(result.getString("averageRating"));
                title_rating.put(content, rating);
            } else {
              ary = new ArrayList<String>();
              ary.add(result.getString("primaryName"));
              title_castmembers.put(content, ary);
            }
        }

        //putting all the titleid of the specific pair in array
        for(Map.Entry<String, ArrayList<String>> entry : title_castmembers.entrySet()){
          ArrayList<String> ary = entry.getValue();
          if(ary.size() > 1)
          {
            for(int i = 0; i < ary.size(); i++)
            {
              for(int j = i + 1; j < ary.size(); j++)
              {
                  //making the pair going through all the array 
                  String pairs = ary.get(i) + " , " + ary.get(j);
                  ArrayList<String> ary_title;
                  if(pair_title.containsKey(pairs))
                  {
                    ary_title = pair_title.get(pairs);
                    ary_title.add(entry.getKey());
                    pair_title.put(pairs, ary_title);
                  }
                  else
                  {
                    ary_title = new ArrayList<>();
                    ary_title.add(entry.getKey());
                    pair_title.put(pairs, ary_title);
                  }
                }
              }
            }
          }

        //getting the average rating of all of the pairs 
        for(Map.Entry<String, ArrayList<String>> entry : pair_title.entrySet())
        {
          ArrayList<String> title_ary = entry.getValue();
          double sum = 0;
          int count = title_ary.size();
          if(count > 2)
          {
            for(String str : title_ary)
            {
                sum = sum + title_rating.get(str);
            }
            double average = sum/count;
            average_pair_rating.put(entry.getKey(), average);
          }
          
        }
         
        HashMap<String, Double> shorted_pair = sort_hashDouble(average_pair_rating);
        //setting the counter to just get to 10 pairs 
        int counter = 0;
        for(Map.Entry<String, Double> entry : shorted_pair.entrySet()){
          topPairFinal.add(entry.getKey());
          counter++;
          if(counter == 10)
          {
            counter = 0;
            break;
          }
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
    }

    public static HashMap<String, Double> sort_hashDouble(HashMap<String, Double> unsortedMap)
    {
      // Create a list from elements of HashMap
      List<Map.Entry<String, Double> > list = new LinkedList<Map.Entry<String, Double >>(unsortedMap.entrySet());

      // Sort the list
      Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
        {
            return -(o1.getValue()).compareTo(o2.getValue());
        }
      });

      HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
      for (Map.Entry<String, Double> aa : list) {
          sortedMap.put(aa.getKey(), aa.getValue());
      }

      return sortedMap;
    }

    public static HashMap<String, Integer> sort_hash(HashMap<String, Integer> unsortedMap){
      List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
                return -(o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
            for (Map.Entry<String, Integer> aa : list) {
                sortedMap.put(aa.getKey(), aa.getValue());
            }

      return sortedMap;
    }

    public static void date_creating(){
      dates = new ArrayList<LocalDate>();
      DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      for(int i = 0; i < dateList.size(); i++){
        LocalDate d = LocalDate.parse(dateList.get(i), f);
        dates.add(d);
      }
    }

    public static void hashing(){
        int i = 0; 
        while(i < dates.size()){
          LocalDate date1 = LocalDate.of(2001, 01, 01);
          LocalDate date2 = LocalDate.of(2003, 01, 01);
          LocalDate date3 = LocalDate.of(2005, 01, 01);
          if(dates.get(i).compareTo(date1) < 0){
            int value;
            value = first_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            first_time.put(titleList.get(i), value);
          } else if (dates.get(i).compareTo(date1) >= 0 && dates.get(i).compareTo(date2) < 0){
            int value;
            value = second_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            second_time.put(titleList.get(i), value);
          } else if (dates.get(i).compareTo(date2) >= 0 && dates.get(i).compareTo(date3) < 0){
            int value;
            value = third_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            third_time.put(titleList.get(i), value);
          } else{
            int value;
            value = fourth_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            fourth_time.put(titleList.get(i), value);
          }
          i++;
        }
    }

    public <String> ArrayList<String> intersection(ArrayList<String> list1, ArrayList<String> list2) {
      ArrayList<String> list = new ArrayList<String>();
      for (String t : list1) {
        if(list2.contains(t)) {
          list.add(t);
        }
      }
  
      return list;
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e){
      String s = e.getActionCommand();
      if (s.equals("Close")){
          contentAnalyst.dispose();
      }
      else if (s.equals("<2001")){
         //Nothing for now
      }
    }

    static public void callGUI(){
      //Intial set up of materials
      contentAnalyst = new JFrame("Content Analyst Experience");


      //Creating some cruical components
      JLabel labelList1 = new JLabel("Top 10 Most watched Content Before 2001");
      JLabel labelList2 = new JLabel("Top 10 Most watched Content between 2001 and 2003");
      JLabel labelList3 = new JLabel("Top 10 Most watched Content between 2003 and 2005");
      JLabel labelList4 = new JLabel("Top 10 Most watched Content after 2005");
      JLabel labelHolly = new JLabel("Top 10 Pairs With The Most Chemistry");
      JLabel labelTomato = new JLabel(tomato_path);


      labelList1.setHorizontalAlignment(JLabel.CENTER);
      labelList1.setVerticalAlignment(JLabel.CENTER);
      labelList2.setHorizontalAlignment(JLabel.CENTER);
      labelList2.setVerticalAlignment(JLabel.CENTER);
      labelList3.setHorizontalAlignment(JLabel.CENTER);
      labelList3.setVerticalAlignment(JLabel.CENTER);
      labelList4.setHorizontalAlignment(JLabel.CENTER);
      labelList4.setVerticalAlignment(JLabel.CENTER);
      labelHolly.setHorizontalAlignment(JLabel.CENTER);
      labelHolly.setVerticalAlignment(JLabel.CENTER);
      labelTomato.setHorizontalAlignment(JLabel.CENTER);
      labelTomato.setVerticalAlignment(JLabel.CENTER);
      labelList1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
      labelList2.setFont(new Font("Times New Roman", Font.PLAIN, 15));
      labelList3.setFont(new Font("Times New Roman", Font.PLAIN, 15));
      labelList4.setFont(new Font("Times New Roman", Font.PLAIN, 15));
      labelHolly.setFont(new Font("Times New Roman", Font.PLAIN, 25));
      labelTomato.setFont(new Font("Times New Roman", Font.PLAIN, 22));


      JTextArea list1 = new JTextArea(nameList1);
      JTextArea list2 = new JTextArea(nameList2);
      JTextArea list3 = new JTextArea(nameList3);
      JTextArea list4 = new JTextArea(nameList4);

      JScrollPane scrollPane1 = new JScrollPane(list1);
      JScrollPane scrollPane2 = new JScrollPane(list2);
      JScrollPane scrollPane3 = new JScrollPane(list3);
      JScrollPane scrollPane4 = new JScrollPane(list4);

      //Adding all the elements from ten recent dates to the JFrame
      DefaultListModel dlmTen = new DefaultListModel();
      JList listTen = new JList(dlmTen);
      JScrollPane scrollPaneTen = new JScrollPane(listTen);

      for(String word : topPairFinal){
        dlmTen.addElement(word);
      }
      listTen.setFont(new Font("Times New Roman", Font.PLAIN, 22));
      DefaultListCellRenderer rendererTen =  (DefaultListCellRenderer)listTen.getCellRenderer();  
      rendererTen.setHorizontalAlignment(JLabel.CENTER);  
      
      JTabbedPane tp = new JTabbedPane();
      JPanel top10 = new JPanel();
      JPanel hollyWood = new JPanel();
      JPanel ratingList = new JPanel();

      tp.add("Top 10 Watched", top10);
      tp.add("Hollywood Pairs", hollyWood);
      tp.add("Fresh Rotten Tomatoes", ratingList);

      top10.setSize(900,1000);
      hollyWood.setSize(900,1000);
      ratingList.setSize(900,1000);
     


      //Setting up the view for the Top 10 Watch History
      top10.add(labelList1);
      top10.add(scrollPane1);
      top10.add(labelList2);
      top10.add(scrollPane2);
      top10.add(labelList3);
      top10.add(scrollPane3);
      top10.add(labelList4);
      top10.add(scrollPane4);

      //Setting up the panel for Hollyword pairs
      hollyWood.setLayout(new GridLayout(2,1));
      hollyWood.add(labelHolly);
      hollyWood.add(scrollPaneTen);

      //Setting up the panel for Fresh Tomatoes
      ratingList.setLayout(new GridLayout(1,1));
      ratingList.add(labelTomato);

      

      contentAnalyst.setSize(900,1000);
      contentAnalyst.add(tp);
      top10.setLayout(new GridLayout(4,2));
      contentAnalyst.show();
 
  }
  


    public static void main(String[] args){
     //Main code is run in the class constructor
    }

    

    
    
}