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



public class hollywood_pair extends JFrame implements ActionListener{
    static JFrame f; 
    
    //creating a hash map to store a title as a key and castmembers as array and values;
     public static HashMap<String, ArrayList<String>> title_castmembers = new HashMap<>();
     public static HashMap<String, ArrayList<String>> pair_title = new HashMap<>();  
     public static HashMap<String, Double> title_rating = new HashMap<>();
     public static HashMap<String, Double> average_pair_rating = new HashMap<>();
     public static String top_pair = "";

     public static HashMap<String, Double> sort_hash(HashMap<String, Double> unsortedMap)
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


    public hollywood_pair()
    {
      //Building the connection
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

        // for(Map.Entry<String, Double> entry : title_rating.entrySet())
        // {
        //   System.out.println(entry.getKey() + " " + entry.getValue());
        // }

        
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
         
        
         HashMap<String, Double> shorted_pair = sort_hash(average_pair_rating);
         //setting the counter to just get to 10 pairs 
         int counter = 0;
         for(Map.Entry<String, Double> entry : shorted_pair.entrySet())
         {
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
      JOptionPane.showMessageDialog(null, "Opened database Successful");
      try{
        f = new JFrame("Top Hollywood Pair");
        JPanel container = new JPanel();

        JButton btn = new JButton("Close");
        // btn.addActionListener(s);
        
        JTextArea txt1 = new JTextArea(top_pair);
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
        else if (s.equals("<2001"))
        {
           
        }
    }

    public static void main(String[] args){
      //Main code is run in the class constructor
     }

}
