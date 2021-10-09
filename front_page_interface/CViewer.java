package project2;
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

public class CViewer extends JFrame implements ActionListener{
	private int userId;
	// unsorted instances of ratings, dates, and titles
	private ArrayList<String> ratingList;
	private ArrayList<String> dateList;
	private ArrayList<String> titleList;
	// sorted instances of ratings, dates, and titles
	private ArrayList<LocalDate> dateListSorted;
	private ArrayList<Integer> titleListSorted;
	private ArrayList<Integer> ratingListSorted;
	
	
	public CViewer(int uid) {
		userId = uid;
		if(!fetchWatchHistory(uid)) {
			System.exit(-1);
		}
		createGUI();
		// Assert that the lists are equal sizes and not 0. You must use the argument -ea during execution in order for this to do anything.
		assert ratingList.size() == dateList.size() && dateList.size() == titleList.size() && titleList.size() != 0: "Invalid list sizes.";
		sortByDate();
		askForHistoryLength();
	}

    static JFrame contentViewer;
    static public void createGUI(){
        contentViewer = new JFrame();
        contentViewer.setSize(800,800);
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

	}

}