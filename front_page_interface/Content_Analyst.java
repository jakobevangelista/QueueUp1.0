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

    //This function is the constructor which will be called in other classes, it will start all database operations and eventually call the JFrame
    public Content_Analyst(){
        Connection conn = null;
        try {
            JOptionPane.showMessageDialog(null, "Please wait this could take a while...");
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
            JOptionPane.showMessageDialog(null, "About half way done...");
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

        callGUI();
    }


    static public void callGUI(){
        //Intial set up of materials
        contentAnalyst = new JFrame("Content Analyst Experience");


        //Creating some cruical components
        JLabel labelList1 = new JLabel("Top 10 Most watched Content Before 2001");
        JLabel labelList2 = new JLabel("Top 10 Most watched Content between 2001 and 2003");
        JLabel labelList3 = new JLabel("Top 10 Most watched Content between 2003 and 2005");
        JLabel labelList4 = new JLabel("Top 10 Most watched Content after 2005");


        labelList1.setHorizontalAlignment(JLabel.CENTER);
        labelList1.setVerticalAlignment(JLabel.CENTER);
        labelList2.setHorizontalAlignment(JLabel.CENTER);
        labelList2.setVerticalAlignment(JLabel.CENTER);
        labelList3.setHorizontalAlignment(JLabel.CENTER);
        labelList3.setVerticalAlignment(JLabel.CENTER);
        labelList4.setHorizontalAlignment(JLabel.CENTER);
        labelList4.setVerticalAlignment(JLabel.CENTER);
        labelList1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelList2.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelList3.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        labelList4.setFont(new Font("Times New Roman", Font.PLAIN, 15));



        JTextArea list1 = new JTextArea(nameList1);
        JTextArea list2 = new JTextArea(nameList2);
        JTextArea list3 = new JTextArea(nameList3);
        JTextArea list4 = new JTextArea(nameList4);
        JScrollPane scrollPane1 = new JScrollPane(list1);
        JScrollPane scrollPane2 = new JScrollPane(list2);
        JScrollPane scrollPane3 = new JScrollPane(list3);
        JScrollPane scrollPane4 = new JScrollPane(list4);
        
        JTabbedPane tp = new JTabbedPane();
        JPanel top10 = new JPanel();
        JPanel genreList = new JPanel();
        JPanel ratingList = new JPanel();

        tp.add("Top 10 Watched", top10);
        tp.add("Top 10 Genres", genreList);
        tp.add("Top 10 Rating", ratingList);

        top10.setSize(900,1000);
        genreList.setSize(900,1000);
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