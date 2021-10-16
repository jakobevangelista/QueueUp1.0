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


public class Content_Analyst extends JFrame implements ActionListener {
    static JFrame contentAnalyst;
    private static ArrayList<LocalDate> dates;
    private static ArrayList<String> ratingList;
	  private static ArrayList<String> dateList;
    private static ArrayList<String> titleList;
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

    public static HashMap<String, ArrayList<String>> title_castmembers = new HashMap<>();
    public static HashMap<String, ArrayList<String>> pair_title = new HashMap<>();  
    public static HashMap<String, Double> title_rating = new HashMap<>();
    public static HashMap<String, Double> average_pair_rating = new HashMap<>();
    public static String top_pair = "";

    //This function is the constructor which will be called in other classes, it will start all database operations and eventually call the JFrame
    
    public Content_Analyst(){
      Connection conn = null;
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
  
          //counter 
          int counter = 0;
          
          for(HashMap.Entry<String, Integer> set: first_sorted.entrySet())
          {
          
              first_list.add(set.getKey());
              counter++;
              if(counter == 10){
                  break;
              }
          } 

        counter = 0;
        for(HashMap.Entry<String, Integer> set: second_sorted.entrySet())
        {
          // System.out.println(set.getKey() + " = " + set.getValue());
          second_list.add(set.getKey());
          counter++;
          if(counter == 10){
            break;
          }
        } 

        counter = 0;

        for(HashMap.Entry<String, Integer> set: third_sorted.entrySet())
        {
          // System.out.println(set.getKey() + " = " + set.getValue());
          third_list.add(set.getKey());
          counter++;
          
          if(counter == 10){
            counter = 0;
            break;
          }
        } 

        counter = 0;
        for(HashMap.Entry<String, Integer> set: fourth_sorted.entrySet())
        {
          fourth_list.add(set.getKey());
          counter++;
          if(counter == 10){
            break;
          }
        } 
        
        //creating a new panel
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
      
      hollywood_pair();
      callGUI();
    }

    static public void hollywood_pair(){
      Connection conn = null;
      //TODO STEP 1
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
            
            if(title_castmembers.get(content) != null)
            {
                ary = title_castmembers.get(content);
                ary.add(result.getString("primaryName"));
                title_castmembers.put(content, ary);
                double rating = Double.parseDouble(result.getString("averageRating"));
                title_rating.put(content, rating);
            }
            else {
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
          top_pair +=  entry.getKey() + "\n";
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


    static public void callGUI(){
        //Intial set up of materials
        contentAnalyst = new JFrame("Content Analyst Experience");


        //Creating some cruical components
        JLabel labelList1 = new JLabel("Top 10 Most watched Content Before 2001");
        JLabel labelList2 = new JLabel("Top 10 Most watched Content between 2001 and 2003");
        JLabel labelList3 = new JLabel("Top 10 Most watched Content between 2003 and 2005");
        JLabel labelList4 = new JLabel("Top 10 Most watched Content after 2005");
        JLabel labelHolly = new JLabel("Top 10 Pairs With The Most Chemistry");



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
        labelList1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelList2.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelList3.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelList4.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelHolly.setFont(new Font("Times New Roman", Font.PLAIN, 25));


        JTextArea list1 = new JTextArea(nameList1);
        JTextArea list2 = new JTextArea(nameList2);
        JTextArea list3 = new JTextArea(nameList3);
        JTextArea list4 = new JTextArea(nameList4);
        JTextArea hollyWoodText = new JTextArea(top_pair);
        JScrollPane scrollPane1 = new JScrollPane(list1);
        JScrollPane scrollPane2 = new JScrollPane(list2);
        JScrollPane scrollPane3 = new JScrollPane(list3);
        JScrollPane scrollPane4 = new JScrollPane(list4);
        JScrollPane scrollPaneHolly = new JScrollPane(hollyWoodText);

        
        JTabbedPane tp = new JTabbedPane();
        JPanel top10 = new JPanel();
        JPanel hollyWood = new JPanel();
        JPanel ratingList = new JPanel();

        tp.add("Top 10 Watched", top10);
        tp.add("Hollywood Pairs", hollyWood);
        tp.add("Top 10 Rating", ratingList);

        top10.setSize(900,1000);
        hollyWood.setSize(900,1000);
        ratingList.setSize(900,1000);
       


        //Setting up the view for the JFrame
        top10.add(labelList1);
        top10.add(scrollPane1);

        top10.add(labelList2);
        top10.add(scrollPane2);

        top10.add(labelList3);
        top10.add(scrollPane3);

        top10.add(labelList4);
        top10.add(scrollPane4);


        hollyWood.setLayout(new GridLayout(2,1));
        hollyWood.add(labelHolly);
        hollyWood.add(scrollPaneHolly);

        

        contentAnalyst.setSize(900,1000);
        contentAnalyst.add(tp);
        top10.setLayout(new GridLayout(4,2));
        contentAnalyst.show();
   
    }

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

    public static HashMap<String, Double> sort_hashDouble(HashMap<String, Double> unsortedMap)
    {
      // Create a list from elements of HashMap
      List<Map.Entry<String, Double> > list = new LinkedList<Map.Entry<String, Double >>(unsortedMap.entrySet());

      // Sort the list
      Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
        public int compare(Map.Entry<String, Double> o1,
                            Map.Entry<String, Double> o2)
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


    public static void main(String[] args){
     //Main code is run in the class constructor
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
}