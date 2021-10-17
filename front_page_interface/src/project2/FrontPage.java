package project2;
import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class FrontPage extends JFrame implements ActionListener {
  //Creating the interface which will be shown to the user
  static JFrame FrontGUI;


  //String which is going to hold the entered user Id
  String userId = "";
  static private ArrayList<String> userList =  new ArrayList<>();


  public void StartPage(){
    //Setting up intial interface and calling the class
    FrontPage newInterface = new FrontPage(); 
    FrontGUI = new JFrame("Content Recommendation System");


    //Intializing the components which will be shown on the interface
    JButton trendButton = new JButton("Content Analyst");
    JButton viewButton = new JButton("Content Viewer");
    JButton showUsers = new JButton("List Users");
    JLabel titleLabel = new JLabel("Choose your " + "\n" + "viewing experience");

    //Adding action/event listeners to the buttons
    viewButton.addActionListener(newInterface);
    trendButton.addActionListener(newInterface);
    showUsers.addActionListener(newInterface);

    //Creating a panel which will hold the title header
    JPanel headerPanel = new JPanel(){
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(750, 80);
      };
    };
    
    headerPanel.setLayout(new BorderLayout());
    headerPanel.add(titleLabel, BorderLayout.CENTER);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    titleLabel.setVerticalAlignment(JLabel.CENTER);
    titleLabel.setFont(new Font("Times New Roman", Font.PLAIN, 50));



    //Creating a panel which will sit within the main frame which will holds the buttons
    JPanel mainPanel = new JPanel(){
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(750, 220);
      };
    };
    mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
    
    viewButton.setPreferredSize(new Dimension(200,50));
    trendButton.setPreferredSize(new Dimension(200,50));
    showUsers.setPreferredSize(new Dimension(200,50));
    trendButton.setFont(new Font("Serif", Font.PLAIN, 25));
    viewButton.setFont(new Font("Serif", Font.PLAIN, 25));
    showUsers.setFont(new Font("Serif", Font.PLAIN, 25));
    trendButton.setFocusable(false);
    viewButton.setFocusable(false);
    showUsers.setFocusable(false);

    mainPanel.add(viewButton);
    mainPanel.add(trendButton);
    mainPanel.add(showUsers);

  
    //Setting up layouts and making the frame visible and adding components
    FrontGUI.add(headerPanel);
    FrontGUI.add(mainPanel);
    FrontGUI.setLayout(new FlowLayout(FlowLayout.CENTER, 120, 50));
    FrontGUI.setSize(800,300);
    FrontGUI.show();
  }



  public boolean callDatabase(String userNum, int value){
    //This portion is for connecting to the database
    Connection conn = null;
    boolean toReturn = false;
    try {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
          "csce315903_14user", "GROUP14CS315");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    JOptionPane.showMessageDialog(FrontGUI,"Checking Database...");

    
    //This portion is for grabbing all the users from the database
    try{
      Statement stmt = conn.createStatement();
      String sqlStatement = "";
      if(value == 0){
        sqlStatement = "SELECT userId FROM users WHERE userId=" + userNum +";";
      } else if (value == 1){
        sqlStatement = "SELECT userId FROM users LIMIT 10";
      }
      ResultSet result = stmt.executeQuery(sqlStatement);
      while (result.next()) {
        if(value == 0){
          if(result.getString("userId").equals(userNum)){
            toReturn = true;
          }
        } else if (value == 1){
          userList.add(result.getString("userId"));
        } 
        
      }
    } catch (Exception e){
      JOptionPane.showMessageDialog(FrontGUI,e);
    }

    //This portion is for closing and reporting the connection
    try {
      conn.close();
    } catch(Exception e) {
      JOptionPane.showMessageDialog(FrontGUI,"Error in database");
    }
    return toReturn;
  }

  // if button is pressed
  public void actionPerformed(ActionEvent e){
      String s = e.getActionCommand();
      if (s.equals("Content Viewer")) {
        this.userId = JOptionPane.showInputDialog(FrontGUI, "Please Enter User ID");
        FrontPage newInterface = new FrontPage();
        if(userId.isEmpty()){
          JOptionPane.showMessageDialog(FrontGUI, "User ID required");
        } else {
          boolean toCheck = newInterface.callDatabase(userId, 0);
          if(toCheck){
            JOptionPane.showMessageDialog(FrontGUI,"Success, Opening Account");
            FrontGUI.dispose();
            CViewer newContent = new CViewer(Integer.parseInt(this.userId));
           
          } else {
            JOptionPane.showMessageDialog(FrontGUI,"User ID Invalid, Please Try Again");
          }
        }
       
      } else if(s.equals("Content Analyst")){
        FrontGUI.dispose();
        Content_Analyst newAnalyst =  new Content_Analyst();

      } else if(s.equals("List Users")){
        FrontPage newInterface = new FrontPage();
        newInterface.callDatabase(null, 1);
        JOptionPane.showMessageDialog(FrontGUI, userList);


      }
  }

  //This is the main function which runs all the code
  public static void main(String[] args){
    FrontPage newInterface = new FrontPage();
    newInterface.StartPage();
  
  }   
}