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
import java.util.stream.Collectors;



public class CViewer extends JFrame implements ActionListener{
	static private int userId;
	static JFrame contentViewer;
	// unsorted instances of ratings, dates, and titles
	static private ArrayList<String> ratingList;
	static private ArrayList<String> dateList;
	static private ArrayList<String> titleList;
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
	static private ArrayList<String> sliderArray = new ArrayList<String>();
	static private ArrayList<String> sliderReverseArray = new ArrayList<String>();

	
	public CViewer(int uid) {
		userId = uid;
		if(!fetchWatchHistory(uid)) {
		 	System.exit(-1);
		}
		//Assert that the lists are equal sizes and not 0. You must use the argument -ea during execution in order for this to do anything.
		// assert ratingList.size() == dateList.size() && dateList.size() == titleList.size() && titleList.size() != 0: "Invalid list sizes.";
		sortByDate();
		JOptionPane.showMessageDialog(null, "Developing Viewers Choice...");
		viewersChoice();
		JOptionPane.showMessageDialog(null, "Developing Viewer History...");
		callDatabase();
		JOptionPane.showMessageDialog(null, "Developing Viewer Beware...");
		viewerBeware();
		JOptionPane.showMessageDialog(null, "Done");
		createGUI();

	}


	static private ArrayList<Integer> worstRatings = new ArrayList<Integer>();
	static private ArrayList<String> worstMovies = new ArrayList<String>();
	static private ArrayList<Integer> countWorst = new ArrayList<Integer>();
	static private ArrayList<String> overlappingWorstTitles = new ArrayList<String>();
	static private ArrayList<String> viewerBewareArr;
	static public void viewerBeware(){
		for(int i = 0; i < ratingList.size(); i++){
			if(ratingList.get(i).equals("1") | ratingList.get(i).equals("2")){ //Grabbing the absolute worst ratings for a user
				worstRatings.add(i); //Putting the index within the array 
			}
		}

		//Works at grabbing the worst rated movies for a given user
		for(int j = 0; j < worstRatings.size(); j++){
			worstMovies.add(titleList.get(worstRatings.get(j))); //Getting the movie at the index of the worst movie
		}
		System.out.println(worstMovies);


		//Now we need to grab all users from the database
		Connection conn = null;
		try {
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			String sqlStatement = "SELECT userId FROM users WHERE userId != " + userId + ";";
			ResultSet name_result = stmt.executeQuery(sqlStatement);
	        String name_string = "";
	        while (name_result.next()) {
	        	name_string += name_result.getString("userId");
				name_string += "T";
	        }
			name_string = name_string.replace("{", "").replace("}", "");
			ArrayList<String> allUserNames = new ArrayList<String>(Arrays.asList(name_string.split("T")));

			//At this point I have collected all the worst tiles and all the users, need to loop for each user and get their worst ratings
			for(int i = 0; i < allUserNames.size(); i++){
				String sqlStatement1 = "SELECT rating FROM users WHERE userId = " + allUserNames.get(i) + ";";
				String sqlStatement2 = "SELECT titleId FROM users WHERE userId = " +  allUserNames.get(i) + ";";
				
				//Grabbing ratings for the user in question
				ResultSet rating_result = stmt.executeQuery(sqlStatement1);
				String rating_string = "";
				while (rating_result.next()) {
					rating_string += rating_result.getString("rating");
				}

				//Grabbing movie number for user in question
				ResultSet title_result = stmt.executeQuery(sqlStatement2);
				String title_string = "";
				while (title_result.next()) {
					title_string += title_result.getString("titleId");
				}

				//Converting all the strings to an array which we can parse through
				rating_string = rating_string.replace("{", "").replace("}", "");
				title_string = title_string.replace("{", "").replace("}", "");
				ArrayList<String> ratingListQuestion = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
				ArrayList<String> titleListQuestion = new ArrayList<String>(Arrays.asList(title_string.split(",")));

				//Grabbing the worst ratings for a user in question
				ArrayList<Integer> worstRatingsQuestion = new ArrayList<Integer>();
				for(int k = 0; k < ratingListQuestion.size(); k++){
					if(ratingListQuestion.get(k).equals("1") | ratingListQuestion.get(k).equals("2")){ 
						worstRatingsQuestion.add(k); 
					}
				}

				
				//Putting the worst raings movie id into a new array
				ArrayList<String> worstMoviesQuestion = new ArrayList<String>();
				for(int j = 0; j < worstRatingsQuestion.size(); j++){
					worstMoviesQuestion.add(titleListQuestion.get(worstRatingsQuestion.get(j))); 
				}


				//Counting the number of same worst movies for user logged in to the user in question
				int count = 0;
				for(int k = 0; k < worstMovies.size(); k++){
					if(worstMoviesQuestion.contains(worstMovies.get(k))){
						count += 1;
					}
				}
				countWorst.add(count);
			}
			System.out.println(countWorst);

			//Now we have an array with all the counts for all the users in the database, find the max index
			int max = countWorst.get(0);
			int indexMax = 0;
			for(int num = 1; num < countWorst.size(); num++){
				if(countWorst.get(num) > max){
					max = countWorst.get(num);
					indexMax = num;
				}
			}


			//Found the best index, now get the user
			String userMax = allUserNames.get(indexMax);
			
			//Call the database again 
			Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db", "csce315903_14user", "GROUP14CS315");
	        Statement stmt3 = conn.createStatement();
			String sqlStatement4 = "SELECT titleId FROM users WHERE userId = " + userMax + ";";
			String sqlStatement6 = "SELECT rating FROM users WHERE userId = " + userMax + ";";
			ResultSet titleB_result = stmt.executeQuery(sqlStatement4);
	        String titleB_string = "";
	        while (titleB_result.next()) {
	        	titleB_string += titleB_result.getString("titleId");
	        }
			titleB_string = titleB_string.replace("{", "").replace("}", "");

			ResultSet resultB_result = stmt.executeQuery(sqlStatement6);
	        String resultB_string = "";
	        while (resultB_result.next()) {
	        	resultB_string += resultB_result.getString("rating");
	        }
			resultB_string = resultB_string.replace("{", "").replace("}", "");

			ArrayList<String> overlapUserTitles = new ArrayList<String>(Arrays.asList(titleB_string.split(",")));
			ArrayList<String> overlapUserRating = new ArrayList<String>(Arrays.asList(resultB_string.split(",")));
			ArrayList<String> overlapUserTitlesFull = new ArrayList<String>();

			for(int o = 0; o < overlapUserRating.size(); o++){
				if(overlapUserRating.get(o).equals("1") | overlapUserRating.get(o).equals("2")){
					overlapUserTitlesFull.add(overlapUserTitles.get(o));
				}
			}

			//Now we have all the number 1 ratings for the user with the most overallaping dislikes
			for(int l = 0; l < overlapUserTitlesFull.size(); l++){
				if(!worstMovies.contains(overlapUserTitlesFull.get(l))){
					overlappingWorstTitles.add(overlapUserTitlesFull.get(l));
				}
			}

			//Now we have all the titles ids which our user has not seen, call the database to get the name
			Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db", "csce315903_14user", "GROUP14CS315");
	        Statement stmt2 = conn.createStatement();
			String titleN_string = "";
			for(int value = 0; value < overlappingWorstTitles.size(); value++){
				String sqlStatement5 = "SELECT originalTitle FROM content WHERE titleId = " + overlappingWorstTitles.get(value)+ ";";
				ResultSet titleN_result = stmt.executeQuery(sqlStatement5);
				while (titleN_result.next()) {
					titleN_string += titleN_result.getString("originalTitle");
					titleN_string += "~";
				}
			}
			titleN_string = titleN_string.replace("{", "").replace("}", "");
			viewerBewareArr = new ArrayList<String>(Arrays.asList(titleN_string.split("~")));
	     
	        // convert list to array list and store it as private variable
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	    }
		
		
	
	}

	public static String ConvertDate(LocalDate LocalDate){
		return LocalDate.format(DateTimeFormatter.ofPattern("LLLL dd yyyy"));
	}

	// Strings of recommended movie
	static private ArrayList<Integer> vChoiceTitleIdRecs = new ArrayList<Integer>();
	// creates a list of recommendations based on the users favorite director, writer, and genre
	static public void viewersChoice(){

		int lengthOfHistoryToLookAt = ratingListSorted.size()-1;
		// grab the last 50 movies the user watched and rated a 4 or 5
		ArrayList<Integer> favMovies = new ArrayList<Integer>();
		for (int i = ratingListSorted.size()-1; i > ratingListSorted.size()-lengthOfHistoryToLookAt-1; i--){
			if(ratingListSorted.get(i) == 4 || ratingListSorted.get(i) == 5){
				favMovies.add(i);
			}
		}

		// open a connection for use throughout the function
		Connection conn = null;
		try {
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			
			String favSqlString = "(";
			for(int i = 0; i < favMovies.size(); i++){
				// grab the start years of the users favorite movies
				favSqlString += titleListSorted.get(i) + ", ";
			}
			favSqlString = favSqlString.substring(0, favSqlString.length()-2) + ")";


			String sqlStatement = "SELECT * FROM content WHERE titleId in " + favSqlString + ";";
			ResultSet name_result = stmt.executeQuery(sqlStatement);
			ArrayList<Integer> favYears = new ArrayList<Integer>();
			ArrayList<String> favGenres = new ArrayList<String>();
			while(name_result.next()){
				favYears.add(name_result.getInt("startyear"));
				// grab the genres for every movie the user liked and add them to a list
				String gString = name_result.getString("genres").replace("{", "").replace("}", "");
				for(String s: gString.split(",")){
					favGenres.add(s);
				}
			}

			// calculate the users favorite year
			int sum = 0;
			for(int i = 0; i < favYears.size(); i++){
				sum += favYears.get(i);
			}
			int favYearAvg = sum / favYears.size();

			System.out.println("Avg fav year: " + favYearAvg);

			// find the users favorite genre
			List<String> uniqueElems = favGenres.stream().distinct().collect(Collectors.toList());
			int maxOccurences = 0;
			String mostCommonGenre = "";
			for (String u: uniqueElems){
				int occurences = 0;
				for (String s: favGenres){
					if (s.equals(u)){
						occurences += 1;
					}
				}
				if(occurences > maxOccurences){
					mostCommonGenre = u;
					maxOccurences = occurences;
				}
			}

			System.out.println("Fav genre: " + mostCommonGenre + " occurs: " + maxOccurences + " times.");


			sqlStatement = "SELECT * FROM contentcreators WHERE titleId in " + favSqlString + ";";
			ResultSet directorSqlResultSet = stmt.executeQuery(sqlStatement);
			// grab every director nameid from the movies the user liked
			ArrayList<String> favDirectorWriter = new ArrayList<String>();
			while(directorSqlResultSet.next()){
				String dirRes = directorSqlResultSet.getString("directornameid").replace("{", "").replace("}", "");
				for(String s: dirRes.split(",")){
					if(!s.equals("-1")){
						favDirectorWriter.add(s);
					}
				}

				// grab every writer nameid from the movies the user liked
				String writerRes = directorSqlResultSet.getString("writernameid").replace("{", "").replace("}", "");
				for(String s: writerRes.split(",")){
					if(!s.equals("-1")){
						favDirectorWriter.add(s);
					}
				}
			}

			// find the users favorite director/writer
			List<String> uniqueDirectors = favDirectorWriter.stream().distinct().collect(Collectors.toList());
			maxOccurences = 0;
			String mostCommonDirector = "";
			for (String u: uniqueDirectors){
				int occurences = 0;
				for (String s: favDirectorWriter){
					if (s.equals(u)){
						occurences += 1;
					}
				}
				if(occurences > maxOccurences){
					mostCommonDirector = u;
					maxOccurences = occurences;
				}
			}

			System.out.println("Favorite Director/Writer: " + mostCommonDirector + " occurs: " + maxOccurences + " times.");

			ArrayList<Integer> favActors = new ArrayList<Integer>();
			String actorsSqlStatement = "SELECT * FROM castmembers WHERE titleId in " + favSqlString + ";";
			name_result = stmt.executeQuery(actorsSqlStatement);
			// grab every actor nameid from the movies the user liked
			while(name_result.next()){
				favActors.add(name_result.getInt("nameid"));
			}

			// find the users favorite director/writer
			List<Integer> uniqueActors = favActors.stream().distinct().collect(Collectors.toList());
			maxOccurences = 0;
			int mostCommonActor = -1;
			for (int u: uniqueActors){
				int occurences = 0;
				for (int s: favActors){
					if (s == u && s != -1){
						occurences += 1;
					}
				}
				if(occurences > maxOccurences){
					mostCommonActor = u;
					maxOccurences = occurences;
				}
			}

			System.out.println("Most common actor: " + mostCommonActor + " occurs: " + maxOccurences + " times.");

			// find movies in the db that have the same genre and startyear as the users favorite 
			ArrayList<Integer> eligibleMovies = new ArrayList<Integer>();
			String eligibleMoviesString = "(";
			for(int i = favYearAvg - 10; i < favYearAvg + 10; i++){
				String eligibleMoviesSqlStatement = "SELECT * FROM content WHERE startyear = " + i + " AND NOT titleId in " + favSqlString + ";";
				ResultSet genRes = stmt.executeQuery(eligibleMoviesSqlStatement);
				while(genRes.next()){
					String gString = genRes.getString("genres").replace("{", "").replace("}", "");
					for(String s: gString.split(",")){
						if(s.equals(mostCommonGenre)){
							int title = genRes.getInt("titleId");
							eligibleMovies.add(title);
							eligibleMoviesString += title + ", ";
							break;
						}
					}
				}
			}
			eligibleMoviesString = eligibleMoviesString.substring(0, eligibleMoviesString.length()-2) + ")";

			// System.out.println(eligibleMoviesString);
			System.out.println("Found: " + eligibleMovies.size() + " " + mostCommonGenre + " movies around your favorite year.");

			ArrayList<Integer> trimmedDownMovies = new ArrayList<Integer>();
			String trimmedSqlStatement = "SELECT * FROM contentcreators WHERE titleId in " + eligibleMoviesString + ";";
			ResultSet res = stmt.executeQuery(trimmedSqlStatement);
			while(res.next()){
				String dString = res.getString("directornameid").replace("{", "").replace("}", "");
				dString += "," + res.getString("writernameid").replace("{", "").replace("}", "");
				// System.out.println(dString);

				for(String s: dString.split(",")){
					// System.out.println(s);
					if(Integer.parseInt(s) == Integer.parseInt(mostCommonDirector)){
						trimmedDownMovies.add(res.getInt("titleId"));
						break;
					}
				}
			}
			
			if(trimmedDownMovies.size() == 0){
				System.out.println("Could not find any movies with your favorite director, trying to find a movie with your favorite actor instead");
				trimmedSqlStatement = "SELECT nameId, titleId FROM castmembers WHERE titleId in " + eligibleMoviesString + ";";
				res = stmt.executeQuery(trimmedSqlStatement);
				while(res.next()){
					String aString = res.getString("nameId").replace("{", "").replace("}", "");
					for(String s: aString.split(",")){
						// System.out.println(s);
						if(Integer.parseInt(s) == mostCommonActor){
							trimmedDownMovies.add(res.getInt("titleId"));
							break;
						}
					}
				}
			}

			// System.out.println(trimmedDownMovies);
			System.out.print("List of Recommended Movies based on Viewers Choice: ");
			System.out.println(trimmedDownMovies);
			vChoiceTitleIdRecs = trimmedDownMovies;
			conn.close();
		} catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	    }
	}

	static JSlider newSlider;
	static JLabel newLabel = new JLabel("Slide For Watch History");
	static DefaultListModel dlmSlider = new DefaultListModel();
	static JList newList = new JList(dlmSlider);
	static JScrollPane scrollSlider = new JScrollPane(newList);


	static public void sliderChanged(){
		int value = newSlider.getValue();
		if(value == allTimeTitles.size()){
			value = value-1;
		}
		sliderArray.clear();
		sliderReverseArray.clear();
		//Adding values to the array
		for(int i = value; i >= 0; i--){
			sliderArray.add(allTimeTitles.get(i));
		}

		//Reversing the array
		for(int i = sliderArray.size()-1; i >=0; i--){
			sliderReverseArray.add(sliderArray.get(i));
		}

		dlmSlider.removeAllElements();
		for(String word : sliderReverseArray){
			dlmSlider.addElement(word);
		}
		newList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		DefaultListCellRenderer rendererSlider =  (DefaultListCellRenderer)newList.getCellRenderer();  
		rendererSlider.setHorizontalAlignment(JLabel.CENTER);  

		String firstValue = ConvertDate(allTimeDates.get(0));
		String secondValue = ConvertDate(allTimeDates.get(sliderReverseArray.size()-1));
		newLabel.setText(firstValue + " - " + secondValue);
		newLabel.setFont((new Font("Times New Roman", Font.PLAIN, 16)));
		newLabel.setHorizontalAlignment(JLabel.CENTER);
		newLabel.setVerticalAlignment(JLabel.CENTER);

	}


    static public void createGUI(){
		//Setting up JFrame and pertint information
        contentViewer = new JFrame("Content Viewing Experience");

		//Creating the slider
		newSlider = new JSlider(0, allTimeDates.size());
		newSlider.setMajorTickSpacing(10);
		newSlider.setMinorTickSpacing(1);
		newSlider.setPaintTicks(true);
		newSlider.addChangeListener(e -> sliderChanged());

		

		


		//Creating some components which will be used within the interface
		JLabel tenDates = new JLabel("Watch History: " + ConvertDate(pastTenDates.get(0)) + " - " + ConvertDate(pastTenDates.get(pastTenDates.size()-1)));
		JLabel hundredDates = new JLabel("Watch History: " + ConvertDate(pastHundredDates.get(0)) + " - " + ConvertDate(pastHundredDates.get(pastHundredDates.size()-1)));
		JLabel allDates = new JLabel("Watch History: " + ConvertDate(allTimeDates.get(0)) + " - " + ConvertDate(allTimeDates.get(allTimeDates.size()-1)));
	    
		JTabbedPane tp = new JTabbedPane();
		JPanel watchHistory = new JPanel();
		JPanel viewerBewarePanel = new JPanel();
		JPanel movieList = new JPanel();

		tenDates.setHorizontalAlignment(JLabel.CENTER);
		hundredDates.setHorizontalAlignment(JLabel.CENTER);
		allDates.setHorizontalAlignment(JLabel.CENTER);

		
		tenDates.setFont(new Font("Times New Roman", Font.PLAIN, 16));
	    hundredDates.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		allDates.setFont(new Font("Times New Roman", Font.PLAIN, 16));


		
		
		//Adding all the elements from ten recent dates to the JFrame
		DefaultListModel dlmTen = new DefaultListModel();
		JList listTen = new JList(dlmTen);
		JScrollPane scrollPaneTen = new JScrollPane(listTen);

		for(String word : pastTenTitle){
			dlmTen.addElement(word);
		}
		listTen.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		DefaultListCellRenderer rendererTen =  (DefaultListCellRenderer)listTen.getCellRenderer();  
		rendererTen.setHorizontalAlignment(JLabel.CENTER);  



		//Adding all the elements from 100 recent dates to the JFrame
		DefaultListModel dlmHun = new DefaultListModel();
		JList listHun = new JList(dlmHun);
		JScrollPane scrollPaneHun = new JScrollPane(listHun);

		for(String word : pastHundredTitles){
			dlmHun.addElement(word);
		}
		listHun.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		DefaultListCellRenderer rendererHun =  (DefaultListCellRenderer)listHun.getCellRenderer();  
		rendererHun.setHorizontalAlignment(JLabel.CENTER);  

		//Adding all the elements from all time recent titless
		DefaultListModel dlmAll = new DefaultListModel();
		JList listAll = new JList(dlmAll);
		JScrollPane scrollPaneAll = new JScrollPane(listAll);

		for(String word : allTimeTitles){
			dlmAll.addElement(word);
		}
		listAll.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		DefaultListCellRenderer rendererAll =  (DefaultListCellRenderer)listAll.getCellRenderer();  
		rendererAll.setHorizontalAlignment(JLabel.CENTER);  


		//Adding all the elements for the viewer beware
		DefaultListModel dlmBeware = new DefaultListModel();
		JList listBeware = new JList(dlmBeware);
		JScrollPane scrollPaneBeware = new JScrollPane(listBeware);

		for(String word : viewerBewareArr){
			dlmBeware.addElement(word);
		}
		listBeware.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		DefaultListCellRenderer rendererBeware =  (DefaultListCellRenderer)listBeware.getCellRenderer();  
		rendererBeware.setHorizontalAlignment(JLabel.CENTER);  

		
		//Adding the components to the JFrame
		tp.add("Watch History", watchHistory);
		tp.add("Viewer Beware", viewerBewarePanel);
		tp.add("Movie List",movieList);
		tp.setSize(850,950);

		watchHistory.setSize(900,1000);
		viewerBewarePanel.setSize(900,1000);
		watchHistory.setSize(900,1000);


		//Setting up watch history
		watchHistory.setLayout(new GridLayout(4,2));
		watchHistory.add(tenDates);
		watchHistory.add(scrollPaneTen);
		watchHistory.add(hundredDates);
		watchHistory.add(scrollPaneHun);
		watchHistory.add(allDates);
		watchHistory.add(scrollPaneAll);


		//Setting up viewerBware
		viewerBewarePanel.setLayout(new GridLayout(2,1));
		JLabel viewerBeLabel = new JLabel("We think you won't like these films");
		viewerBeLabel.setFont((new Font("Times New Roman", Font.PLAIN, 30)));
		viewerBeLabel.setHorizontalAlignment(JLabel.CENTER);
		viewerBeLabel.setVerticalAlignment(JLabel.CENTER);
		viewerBewarePanel.add(viewerBeLabel);
		viewerBewarePanel.add(scrollPaneBeware);
		
		//Add all the information for the slider which we missed in Phase 3
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridLayout(2,1));
		watchHistory.add(newPanel);
		newPanel.add(newLabel);
		newPanel.add(newSlider);
		watchHistory.add(scrollSlider);

		contentViewer.add(tp);
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
		  for(int i = 0; i < titleListSorted.size()-1; i++){
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}