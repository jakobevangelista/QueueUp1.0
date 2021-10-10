package project2;
import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CViewer extends JFrame implements ActionListener{
	private int userId;
	static JFrame contentViewer;
	// unsorted instances of ratings, dates, and titles
	private ArrayList<String> ratingList;
	private ArrayList<String> dateList;
	private ArrayList<String> titleList;
	// sorted instances of ratings, dates, and titles
	static private ArrayList<LocalDate> dateListSorted;
	static private ArrayList<Integer> titleListSorted;
	static private ArrayList<Integer> ratingListSorted;
	static private ArrayList<String> titleListNameSorted = new ArrayList<String>();
	//instances of different dates for the watch history
	static private ArrayList<LocalDate> pastTenDates =  new ArrayList<LocalDate>();
	static private ArrayList<String> pastTenTitle =  new ArrayList<String>();
	static private ArrayList<LocalDate> pastHundredDates =  new ArrayList<LocalDate>();
	static private ArrayList<String> pastHundredTitles =  new ArrayList<String>();
	static private ArrayList<LocalDate> allTimeDates =  new ArrayList<LocalDate>();
	static private ArrayList<String> allTimeTitles=  new ArrayList<String>();

	
	public CViewer(int uid) {
		userId = uid;
		if(!fetchWatchHistory(uid)) {
			System.exit(-1);
		}
		// Assert that the lists are equal sizes and not 0. You must use the argument -ea during execution in order for this to do anything.
		assert ratingList.size() == dateList.size() && dateList.size() == titleList.size() && titleList.size() != 0: "Invalid list sizes.";
		sortByDate();
		askForHistoryLength();
		callDatabase();
		createGUI();

	}


	public static String ConvertDate(LocalDate LocalDate){
		return LocalDate.format(DateTimeFormatter.ofPattern("LLLL dd yyyy"));
	}

    static public void createGUI(){
		//Setting up JFrame and pertint information
        contentViewer = new JFrame("Content Viewing Experience");

		//Creating some components which will be used within the interface

		JLabel tenDates = new JLabel("Watch History: " + ConvertDate(pastTenDates.get(0)) + " - " + ConvertDate(pastTenDates.get(pastTenDates.size()-1)));
		JLabel hundredDates = new JLabel("Watch History: " + ConvertDate(pastHundredDates.get(0)) + " - " + ConvertDate(pastHundredDates.get(pastHundredDates.size()-1)));
		JLabel allDates = new JLabel("Watch History: " + ConvertDate(allTimeDates.get(0)) + " - " + ConvertDate(allTimeDates.get(allTimeDates.size()-1)));
	    tenDates.setFont(new Font("Times New Roman", Font.PLAIN, 20));
	    hundredDates.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		allDates.setFont(new Font("Times New Roman", Font.PLAIN, 20));


		
		
		//Adding all the elements from ten recent dates to the JFrame
		DefaultListModel dlmTen = new DefaultListModel();
		JList listTen = new JList(dlmTen);
		JScrollPane scrollPaneTen = new JScrollPane(listTen);

		for(String word : pastTenTitle){
			dlmTen.addElement(word);
		}
		listTen.setFont(new Font("Times New Roman", Font.PLAIN, 15));

		//Adding all the elements from 100 recent dates to the JFrame
		DefaultListModel dlmHun = new DefaultListModel();
		JList listHun = new JList(dlmHun);
		JScrollPane scrollPaneHun = new JScrollPane(listHun);

		for(String word : pastHundredTitles){
			dlmHun.addElement(word);
		}
		listHun.setFont(new Font("Times New Roman", Font.PLAIN, 15));


		//Adding all the elements from all time recent titless
		DefaultListModel dlmAll = new DefaultListModel();
		JList listAll = new JList(dlmAll);
		JScrollPane scrollPaneAll = new JScrollPane(listAll);

		for(String word : allTimeTitles){
			dlmAll.addElement(word);
		}
		listAll.setFont(new Font("Times New Roman", Font.PLAIN, 15));




		
		//Adding the components to the JFrame
		contentViewer.add(tenDates);
		contentViewer.add(scrollPaneTen);
		contentViewer.add(hundredDates);
		contentViewer.add(scrollPaneHun);
		contentViewer.add(allDates);
		contentViewer.add(scrollPaneAll);
		

		//Setting up the layout and important information
		contentViewer.setLayout(new GridLayout(3,2, 40, 20));
		contentViewer.setSize(900,1000);
        contentViewer.show();


    }


	//open a connection, grab ratings,dates,and titles, and convert them to java.utils.list
	private boolean fetchWatchHistory(int uid) {
		Connection conn = null;
		try {
			//open a connection
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
	        // create sql statements to get info we need
	        String sqlStatement1 = "SELECT rating FROM users WHERE userId = " + uid + ";";
	        String sqlStatement2 = "SELECT date FROM users WHERE userId = " + uid + ";";
	        String sqlStatement3 = "SELECT titleId FROM users WHERE userId = " + uid + ";";
	        // execute each statement and store info in string
	        ResultSet rating_result = stmt.executeQuery(sqlStatement1);
	        String rating_string = "";
	        while (rating_result.next()) {
	        	rating_string += rating_result.getString("rating");
	        }
	        ResultSet date_result = stmt.executeQuery(sqlStatement2);
	        String date_string = "";
	        while (date_result.next()) {
	        	date_string += date_result.getString("date");
	        }
	        ResultSet title_result = stmt.executeQuery(sqlStatement3);
	        String title_string = "";
	        while (title_result.next()) {
	        	title_string += title_result.getString("titleId");
	        }
	        // remove { and } from string then convert string to java.util.ArrayList
	        rating_string = rating_string.replace("{", "").replace("}", "");
	        date_string = date_string.replace("{", "").replace("}", "");
	        title_string = title_string.replace("{", "").replace("}", "");
	        // convert list to array list and store it as private variable
	        ratingList = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
	        dateList = new ArrayList<String>(Arrays.asList(date_string.split(",")));
	        titleList = new ArrayList<String>(Arrays.asList(title_string.split(",")));
	        conn.close();
	      } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        return false;
	      }
		return true;
	}
	
	// this function sorts our rating list, title list, and date list by date
	// It runs in O(n^2) time because it uses bubble sort
	private void sortByDate() {
		// convert the list of string dates to java.time.LocalDateTimes so we can compare them
		ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (int i = 0; i < dateList.size(); i++) {
			LocalDate d = LocalDate.parse(dateList.get(i), f);
			dates.add(d);
		}
		
		//create a bunch of array lists to be tampered with during sorting process
		ArrayList<LocalDate> dateSorted = new ArrayList<LocalDate>();
		ArrayList<Integer> titleSorted = new ArrayList<Integer>();
		ArrayList<Integer> ratingSorted = new ArrayList<Integer>();
		ArrayList<String> titleTemp = new ArrayList<String>(titleList);
		ArrayList<String> ratingTemp = new ArrayList<String>(ratingList);
		
		boolean sorted = false;
		while(!sorted) {
			if(dates.size() == 0) {
				sorted = true;
				break;
			}
			int minIndex = 0;
			for(int i = 0; i < dates.size(); i++) {
				if(dates.get(i).isBefore(dates.get(minIndex))) {
					minIndex = i;
				}
			}
			dateSorted.add(dates.get(minIndex));
			titleSorted.add(Integer.valueOf(titleTemp.get(minIndex)));
			ratingSorted.add(Integer.valueOf(ratingTemp.get(minIndex)));
			dates.remove(minIndex);
			titleTemp.remove(minIndex);
			ratingTemp.remove(minIndex);
		}
		dateListSorted = dateSorted;
		titleListSorted = titleSorted;
		ratingListSorted = ratingSorted;

	}

	public void callDatabase(){
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
			
		//This portion is for grabbing all the users from the database
		try{
		  Statement stmt = conn.createStatement();
		  JOptionPane.showMessageDialog(null, "Please wait, this could take a while...");
		  for(int i = 0; i < titleListSorted.size()-1; i++){
			if(i == titleListSorted.size()/2){
				JOptionPane.showMessageDialog(null, "About half way done...");
			}
			String sqlStatement = "SELECT originalTitle FROM content WHERE titleId=" + titleListSorted.get(i) +";";
			ResultSet result = stmt.executeQuery(sqlStatement);
			while (result.next()) {
				titleListNameSorted.add(result.getString("originalTitle"));
			}
		  }		 
		} catch (Exception e){
		  JOptionPane.showMessageDialog(contentViewer,e);
		}
	

		//This portion is for closing and reporting the connection
		try {
		  conn.close();
		} catch(Exception e) {
		  JOptionPane.showMessageDialog(contentViewer,"Error in database");
		}


		//This portion is for creating the arrays with the watch history to display
		
		//This is for getting all of the titles (all time watch history)
		for(int i = titleListNameSorted.size()-1; i >= 0; i--){
			allTimeDates.add(dateListSorted.get(i));
			allTimeTitles.add(titleListNameSorted.get(i));
		}

		//This is for getting 100 of the titles (recent watch history)
		for(int i = titleListNameSorted.size()-1; i >= titleListNameSorted.size()-100; i--){	
			pastHundredDates.add(dateListSorted.get(i));
			pastHundredTitles.add(titleListNameSorted.get(i));
		}

		//This is for getting 10 of the titles (recent watch history)
		for(int i = titleListNameSorted.size()-1; i >= titleListNameSorted.size()-10; i--){
			pastTenDates.add(dateListSorted.get(i));
			pastTenTitle.add(titleListNameSorted.get(i));
		}

	}
	
	private void askForHistoryLength() {
		JFrame f = new JFrame("");
		f.getContentPane().removeAll();
		JLabel lengthOfHistoryLabel = new JLabel("label");
		lengthOfHistoryLabel.setBounds(10,10, 250, 20);
		lengthOfHistoryLabel.setText("How many movies do you want to display?");
		f.add(lengthOfHistoryLabel);
		
		JTextField field = new JTextField("field");
		field.setText("");
		field.setBounds(10, 265, 50, 20);
		f.add(field);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

    public static void main(String[] args){
		//Nothing needs to occur here as we are running main from front page
	}

}