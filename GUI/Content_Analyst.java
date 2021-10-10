import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
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


/*
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/

public class Content_Analyst extends JFrame implements ActionListener {
    static JFrame f;
    private static ArrayList<LocalDate> dates;
    private static ArrayList<String> ratingList;
	  private static ArrayList<String> dateList;
    private static ArrayList<String> titleList;
    private static HashMap<String, Integer> first_time = new HashMap<>();
    private static HashMap<String, Integer> second_time = new HashMap<>();
    private static HashMap<String, Integer> third_time = new HashMap<>();
    private static HashMap<String, Integer> fourth_time = new HashMap<>();

    public static HashMap<String, Integer> sort_hash(HashMap<String, Integer> unsortedMap)
    {
      // Create a list from elements of HashMap
      List<Map.Entry<String, Integer> > list =
      new LinkedList<Map.Entry<String, Integer> >(unsortedMap.entrySet());

      // Sort the list
      Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
        public int compare(Map.Entry<String, Integer> o1,
                            Map.Entry<String, Integer> o2)
        {
            return (o1.getValue()).compareTo(o2.getValue());
        }
      });

      // put data from sorted list to hashmap
      HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
      for (Map.Entry<String, Integer> aa : list) {
          sortedMap.put(aa.getKey(), aa.getValue());
      }

      return sortedMap;
    }

    public static void date_creating()
    {
      dates = new ArrayList<LocalDate>();
      DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      for(int i = 0; i < dateList.size(); i++)
      {
        LocalDate d = LocalDate.parse(dateList.get(i), f);
        dates.add(d);
      }
    }

    public static void hashing()
    {
        int i = 0; 
        while(i < dates.size())
        {
          LocalDate date1 = LocalDate.of(2004, 01, 01);
          LocalDate date2 = LocalDate.of(2006, 01, 01);
          LocalDate date3 = LocalDate.of(2008, 01, 01);
          if(dates.get(i).compareTo(date1) < 0)
          {
            int value;
            value = first_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            first_time.put(titleList.get(i), value);
          }
          else if (dates.get(i).compareTo(date1) >= 0 
          && dates.get(i).compareTo(date2) < 0)
          {
            int value;
            value = second_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            second_time.put(titleList.get(i), value);
          } 
          else if (dates.get(i).compareTo(date2) >= 0 
          && dates.get(i).compareTo(date3) < 0)
          {
            int value;
            value = third_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            third_time.put(titleList.get(i), value);
          }
          else
          {
            int value;
            value = fourth_time.get(titleList.get(i));
            // System.out.println("title : "+ titleList.get(i) + " + " + value);
            value++;
            fourth_time.put(titleList.get(i), value);
          }
          i++;
        }
        System.out.println(first_time.get("8528522"));
        System.out.println(second_time.get("8528522"));
        System.out.println(third_time.get("8528522"));
        System.out.println(fourth_time.get("8528522"));
    }


    public static void main(String[] args)
    {
      //Building the connection
      Connection conn = null;
      //TODO STEP 1
      try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
           "csce315903_14user", "GROUP14CS315");
        Statement stmt = conn.createStatement();
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
	        // convert list to array list and store it as private variable
	      ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
	      dateList = new ArrayList<String>(Arrays.asList(date_string.split(",")));
        titleList = new ArrayList<String>(Arrays.asList(title_string.split(",")));
        date_creating();
        hashing();
        
        HashMap<String, Integer> first_sorted = sort_hash(first_time);
        System.out.println(first_sorted);
      } 
      catch (Exception e) 
      {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
      JOptionPane.showMessageDialog(null,"Opened database successfully");
      try{
        
        

      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      //closing the connection
      try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

    

    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            f.dispose();
        }
    }
}