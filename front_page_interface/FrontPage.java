package project2;
import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;


public class FrontPage extends JFrame implements ActionListener {
  //Creating the interface which will be shown to the user
  static JFrame FrontGUI;


  //String which is going to hold the entered user Id
  String userId = "";

  public void StartPage(){
    //Setting up intial interface and calling the class
    FrontPage newInterface = new FrontPage(); 
    FrontGUI = new JFrame("Content Recommendation System");


    //Intializing the components which will be shown on the interface
    JButton trendButton = new JButton("Content Analyst");
    JButton viewButton = new JButton("Content Viewer");
    JLabel titleLabel = new JLabel("Choose your " + "\n" + "viewing experience");

    //Adding action/event listeners to the buttons
    viewButton.addActionListener(newInterface);
    trendButton.addActionListener(newInterface);

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
    
    viewButton.setPreferredSize(new Dimension(250,50));
    trendButton.setPreferredSize(new Dimension(250,50));
    trendButton.setFont(new Font("Serif", Font.PLAIN, 30));
    viewButton.setFont(new Font("Serif", Font.PLAIN, 30));
    trendButton.setFocusable(false);
    viewButton.setFocusable(false);

    
    mainPanel.add(viewButton);
    mainPanel.add(trendButton);

  
    //Setting up layouts and making the frame visible and adding components
    FrontGUI.add(headerPanel);
    FrontGUI.add(mainPanel);
    FrontGUI.setLayout(new FlowLayout(FlowLayout.CENTER, 120, 50));
    FrontGUI.setSize(800,300);
    FrontGUI.show();
  }



  public boolean callDatabase(String userNum){
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
      String sqlStatement = "SELECT userId FROM users WHERE userId=" + userNum;
      ResultSet result = stmt.executeQuery(sqlStatement);
      while (result.next()) {
        if(result.getString("userId").equals(userNum)){
          toReturn = true;
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
          boolean toCheck = newInterface.callDatabase(userId);
          if(toCheck){
            JOptionPane.showMessageDialog(FrontGUI,"Success, Opening Account");
            CViewer newContent = new CViewer(Integer.parseInt(this.userId));
            FrontGUI.dispose();
          } else {
            JOptionPane.showMessageDialog(FrontGUI,"User ID Invalid, Please Try Again");
          }
        }
       
      } else if(s.equals("Content Analyst")){
        FrontGUI.dispose();

      } 
  }

  //This is the main function which runs all the code
  public static void main(String[] args){
    FrontPage newInterface = new FrontPage();
    newInterface.StartPage();
  
  }   
}