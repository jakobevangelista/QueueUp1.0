import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;


public class FrontPage extends JFrame implements ActionListener {
  //Creating the interfaces which will be shown to the user
  static JFrame FrontGUI;
  static JFrame ViewerDetermine;

  public void StartPage(){
    //Setting up intial interface and calling the class
    FrontPage newInterface = new FrontPage(); 
    FrontGUI = new JFrame("Content Recommendation System");


    //Intializing the components which will be shown on the interface
    JButton viewButton = new JButton("Content Viewer");
    JButton trendButton = new JButton("Content Analyst");
    JButton closeButton = new JButton("Close");
    JLabel titleLabel = new JLabel("Please choose your viewing experience");

    //Adding action/event listeners to the buttons
    viewButton.addActionListener(newInterface);
    trendButton.addActionListener(newInterface);
    closeButton.addActionListener(newInterface);

    //Creating a panel which will hold the title header
    JPanel headerPanel = new JPanel(){
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(700, 200);
      };
    };
    headerPanel.setLayout(new BorderLayout());
    headerPanel.add(titleLabel, BorderLayout.CENTER);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    titleLabel.setVerticalAlignment(JLabel.CENTER);
    titleLabel.setFont(new Font("Serif", Font.PLAIN, 40));



    //Creating a panel which will sit within the main frame which will holds the buttons
    JPanel mainPanel = new JPanel(){
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(700, 350);
      };
    };
    mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 80));
    viewButton.setPreferredSize(new Dimension(350,45));
    trendButton.setPreferredSize(new Dimension(350,45));
    trendButton.setFont(new Font("Serif", Font.PLAIN, 32));
    viewButton.setFont(new Font("Serif", Font.PLAIN, 32));
    mainPanel.add(viewButton);
    mainPanel.add(trendButton);

  
    //Setting up layouts and making the frame visible and adding components
    FrontGUI.add(headerPanel);
    FrontGUI.add(mainPanel);
    FrontGUI.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));
    FrontGUI.setSize(800,800);
    FrontGUI.show();
  }


  public void StartViewers(){
    ViewerDetermine = new JFrame();
    ViewerDetermine.setSize(800,800);
    ViewerDetermine.show();
  }

  public void createConnection(){
    Connection conn = null;
    try {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
          "csce315903_14user", "GROUP14CS315");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    JOptionPane.showMessageDialog(null,"Opened database successfully");

    
    
    String name = "";
    try{
      Statement stmt = conn.createStatement();
      String sqlStatement = "SELECT originalTitle FROM content WHERE titleType='movie' LIMIT 10;";
      ResultSet result = stmt.executeQuery(sqlStatement);
      while (result.next()) {
        name += result.getString("originalTitle")+"\n";
      }
    } catch (Exception e){
      JOptionPane.showMessageDialog(null,"Error accessing Database.");
    }

    try {
      conn.close();
      JOptionPane.showMessageDialog(null,"Connection Closed.");
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
    }

  }

  // if button is pressed
  public void actionPerformed(ActionEvent e){
      String s = e.getActionCommand();
      if (s.equals("Content Viewer")) {
        FrontGUI.dispose();
        FrontPage newInterface = new FrontPage();
        newInterface.StartViewers();

      } else if(s.equals("Content Analyst")){
        FrontGUI.dispose();

      } else if(s.equals("Close")){
        FrontGUI.dispose();
      }
  }

  public static void main(String[] args){
    FrontPage newInterface = new FrontPage();
    newInterface.StartPage();
    
    
  }   
}