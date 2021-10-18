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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.*;



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
	
	// instances of arrays with specific data for the viewer beware function
	static private ArrayList<Integer> worstRatings = new ArrayList<Integer>();
	static private ArrayList<String> worstMovies = new ArrayList<String>();
	static private ArrayList<Integer> countWorst = new ArrayList<Integer>();
	static private ArrayList<String> overlappingWorstTitles = new ArrayList<String>();
	static private ArrayList<String> viewerBewareArr;
	
	// instanes of array specifc to directors choice
	static private ArrayList<String> directorsChoiceArr;
	static private ArrayList<String> vChoiceTitleIdRecs = new ArrayList<String>();

	// important variables which are used for the adjustable slider
	static JSlider newSlider;
	static JLabel newLabel = new JLabel("Slide For Watch History");
	static DefaultListModel dlmSlider = new DefaultListModel();
	static JList newList = new JList(dlmSlider);
	static JScrollPane scrollSlider = new JScrollPane(newList);
	

	//This is the constructor for the CViewer class which will be called from FrontPage.java
	public CViewer(int uid) {
		userId = uid;
		if(!fetchWatchHistory(uid)) {
		 	System.exit(-1);
		}
		assert ratingList.size() == dateList.size() && dateList.size() == titleList.size() && titleList.size() != 0: "Invalid list sizes.";
		sortByDate();
		JOptionPane.showMessageDialog(null, "Developing Viewers Choice...");
		viewersChoice();
		JOptionPane.showMessageDialog(null, "Developing Viewer History...");
		callDatabase();
		JOptionPane.showMessageDialog(null, "Developing Viewer Beware...");
		viewerBeware();
		JOptionPane.showMessageDialog(null, "Developing Directors Choice...");
		directorsChoiceArr = directorsChoice();
		JOptionPane.showMessageDialog(null, "Done");
		createGUI();
	}


	//This function grabs all the worst titles for a user based on the most overlapping dislikes from other users
	static public void viewerBeware(){
		
		//Grabbing all the dislikes for a user
		for(int i = 0; i < ratingList.size(); i++){
			if(ratingList.get(i).equals("1") | ratingList.get(i).equals("2")){ 
				worstRatings.add(i);
			}
		}
		for(int j = 0; j < worstRatings.size(); j++){
			worstMovies.add(titleList.get(worstRatings.get(j))); 
		}

		//Comparing the user in questions dislikes to other users in the database
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

			for(int i = 0; i < allUserNames.size(); i++){
				String sqlStatement1 = "SELECT rating FROM users WHERE userId = " + allUserNames.get(i) + ";";
				String sqlStatement2 = "SELECT titleId FROM users WHERE userId = " +  allUserNames.get(i) + ";";
				ResultSet rating_result = stmt.executeQuery(sqlStatement1);
				String rating_string = "";
				while (rating_result.next()) {
					rating_string += rating_result.getString("rating");
				}

				ResultSet title_result = stmt.executeQuery(sqlStatement2);
				String title_string = "";
				while (title_result.next()) {
					title_string += title_result.getString("titleId");
				}

				rating_string = rating_string.replace("{", "").replace("}", "");
				title_string = title_string.replace("{", "").replace("}", "");
				ArrayList<String> ratingListQuestion = new ArrayList<String>(Arrays.asList(rating_string.split(",")));
				ArrayList<String> titleListQuestion = new ArrayList<String>(Arrays.asList(title_string.split(",")));

				ArrayList<Integer> worstRatingsQuestion = new ArrayList<Integer>();
				for(int k = 0; k < ratingListQuestion.size(); k++){
					if(ratingListQuestion.get(k).equals("1") | ratingListQuestion.get(k).equals("2")){ 
						worstRatingsQuestion.add(k); 
					}
				}

				ArrayList<String> worstMoviesQuestion = new ArrayList<String>();
				for(int j = 0; j < worstRatingsQuestion.size(); j++){
					worstMoviesQuestion.add(titleListQuestion.get(worstRatingsQuestion.get(j))); 
				}

				int count = 0;
				for(int k = 0; k < worstMovies.size(); k++){
					if(worstMoviesQuestion.contains(worstMovies.get(k))){
						count += 1;
					}
				}
				countWorst.add(count);
			}

			//Finding the user with the most overlapping dislikes with the given user
			int max = countWorst.get(0);
			int indexMax = 0;
			for(int num = 1; num < countWorst.size(); num++){

				if(countWorst.get(num) > max){
					max = countWorst.get(num);
					indexMax = num;
				}
			}
			String userMax = allUserNames.get(indexMax);

			//Call the database again with the user with the most overlapping dislikes
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
			for(int l = 0; l < overlapUserTitlesFull.size(); l++){
				if(!worstMovies.contains(overlapUserTitlesFull.get(l))){
					overlappingWorstTitles.add(overlapUserTitlesFull.get(l));
				}
			}

			//After we have found all the titles that the given user has not watched and will not like, we get the actual titles name
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
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	    }
	}


	//This function opens a connection, grab ratings,dates,and titles, and convert them to java.utils.list
	private boolean fetchWatchHistory(int uid) {
		Connection conn = null;
		try {
			//open a connection
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
	        String sqlStatement1 = "SELECT rating FROM users WHERE userId = " + uid + ";";
	        String sqlStatement2 = "SELECT date FROM users WHERE userId = " + uid + ";";
	        String sqlStatement3 = "SELECT titleId FROM users WHERE userId = " + uid + ";";
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
	        rating_string = rating_string.replace("{", "").replace("}", "");
	        date_string = date_string.replace("{", "").replace("}", "");
	        title_string = title_string.replace("{", "").replace("}", "");
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
	private void sortByDate() {
		ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (int i = 0; i < dateList.size(); i++) {
			LocalDate d = LocalDate.parse(dateList.get(i), f);
			dates.add(d);
		}
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

	//This function is for getting all the required information for the viewers watch history 
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

	// This function creates a list of recommendations based on the users favorite director, writer, genre, year, and actor
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
				favSqlString += titleListSorted.get(i) + ", ";
			}
			favSqlString = favSqlString.substring(0, favSqlString.length()-2) + ")";


			// grab the genres for every movie the user liked and add them to a list
			String sqlStatement = "SELECT * FROM content WHERE titleId in " + favSqlString + ";";
			ResultSet name_result = stmt.executeQuery(sqlStatement);
			ArrayList<Integer> favYears = new ArrayList<Integer>();
			ArrayList<String> favGenres = new ArrayList<String>();
			while(name_result.next()){
				favYears.add(name_result.getInt("startyear"));
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

			// grab every director nameid from the movies the user liked
			sqlStatement = "SELECT * FROM contentcreators WHERE titleId in " + favSqlString + ";";
			ResultSet directorSqlResultSet = stmt.executeQuery(sqlStatement);
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

			// grab every actor nameid from the movies the user liked
			ArrayList<Integer> favActors = new ArrayList<Integer>();
			String actorsSqlStatement = "SELECT * FROM castmembers WHERE titleId in " + favSqlString + ";";
			name_result = stmt.executeQuery(actorsSqlStatement);
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
			ArrayList<String> trimmedDownMovies = new ArrayList<String>();
			String trimmedSqlStatement = "SELECT * FROM contentcreators WHERE titleId in " + eligibleMoviesString + ";";
			ResultSet res = stmt.executeQuery(trimmedSqlStatement);
			while(res.next()){
				String dString = res.getString("directornameid").replace("{", "").replace("}", "");
				dString += "," + res.getString("writernameid").replace("{", "").replace("}", "");

				for(String s: dString.split(",")){
					if(Integer.parseInt(s) == Integer.parseInt(mostCommonDirector)){
						trimmedDownMovies.add(res.getString("titleId"));
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
						if(Integer.parseInt(s) == mostCommonActor){
							trimmedDownMovies.add(res.getString("titleId"));
							break;
						}
					}
				}
			}

			for(int i = 0; i < trimmedDownMovies.size(); i++){
				String nameSqlStatement = "SELECT originalTitle FROM content WHERE titleId=" + trimmedDownMovies.get(i) + ";";
				res = stmt.executeQuery(nameSqlStatement);
				while(res.next()){
					vChoiceTitleIdRecs.add(res.getString("originalTitle").replace("{", "").replace("}", ""));
				}
			}

			System.out.println(vChoiceTitleIdRecs);
			conn.close();
		} catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	    }
	}

	//This function returns 1-2 movies based on favorite director and favorite genere for a movie they have not seen with the highest ratings
	public ArrayList<String> directorsChoice() {
		ArrayList<String> result = new ArrayList<String>();
		HashMap<String, Integer> directorListOccurence = new HashMap<String, Integer>();
		HashMap<String, Integer> directorListRating = new HashMap<String, Integer>();
		HashMap<String, Double> directorListRatingAverage = new HashMap<String, Double>();
		ArrayList<String> directorListTemp = new ArrayList<String>();
		Connection conn = null;
		try {
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(Integer i : titleListSorted) {
				String sqlStatement1 = "SELECT directornameId FROM ContentCreators WHERE titleId = " + i + ";";
				ResultSet director_result = stmt.executeQuery(sqlStatement1);
				String director_string = "";
				while (director_result.next()) {
					director_string += director_result.getString("directornameId");
				}
				director_string = director_string.replace("{", "").replace("}", "");
				directorListTemp = new ArrayList<String>(Arrays.asList(director_string.split(",")));
				directorListOccurence.merge(directorListTemp.get(0),1,Integer::sum);
				Integer count = directorListRating.get(directorListTemp.get(0));
				if(count == null) {
					directorListRating.put(directorListTemp.get(0), ratingListSorted.get(titleListSorted.indexOf(i)));
				} else {
					directorListRating.put(directorListTemp.get(0), count + ratingListSorted.get(titleListSorted.indexOf(i)));
				}
				directorListTemp.clear();
			}
	    } catch (Exception e) {
			e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
			ArrayList<String> errorArray = new ArrayList<String>();
			errorArray.add("error");
	        return errorArray;
	    }

		for (String key: directorListOccurence.keySet()) {
            directorListRatingAverage.put(key, ((Double.valueOf(directorListRating.get(key))) / (Double.valueOf(directorListOccurence.get(key)))));
        }
		List<Map.Entry<String, Double> > list =
			new LinkedList<Map.Entry<String, Double> >(directorListRatingAverage.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
			public int compare(Map.Entry<String, Double> o2,
							Map.Entry<String, Double> o1)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		
		// put data from sorted list to hashmap
		HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		directorListRatingAverage = temp;

		// gets all movies from every director
		ArrayList<String> allMoviesTemp;
		ArrayList<String> allMovies = new ArrayList<String>();
		conn = null;
		try {
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(String i : directorListRatingAverage.keySet()) {
				if(i.equals("-1")) {
					continue;
				}
				String sqlStatement1 = "SELECT titleId FROM contentCreators WHERE ('" + i + "' = ANY(directornameId));";
				ResultSet allMovies_result = stmt.executeQuery(sqlStatement1);
				String allMovies_string = "";
				while (allMovies_result.next()) {
					allMovies_string += allMovies_result.getString("titleId") + "~";
				}
				allMovies_string = allMovies_string.replace("{", "").replace("}", "");

				// convert list to array list and store it as private variable
				allMoviesTemp = new ArrayList<String>(Arrays.asList(allMovies_string.split("~")));

				ArrayList<String> averageRatingAllMovies = new ArrayList<String>();
				for(String a : allMoviesTemp) {
					String sqlStatement2 = "SELECT averageRating FROM content WHERE titleId = " + a + ";";
					ResultSet allAverage_result = stmt.executeQuery(sqlStatement2);
					String allAverage_string = "";
					while (allAverage_result.next()) {
						allAverage_string += allAverage_result.getString("averageRating");
					}
					allAverage_string = allAverage_string.replace("{", "").replace("}", "");

					// convert list to array list and store it as private variable
					averageRatingAllMovies.add(allAverage_string);
				}
				ArrayList<String> averageRatingAllMoviesTemp = new ArrayList<String>();
				while(allMoviesTemp.size() != 0) {
					int highest = averageRatingAllMovies.indexOf(Collections.max(averageRatingAllMovies));
					averageRatingAllMoviesTemp.add(allMoviesTemp.get(highest));
					averageRatingAllMovies.remove(highest);
					allMoviesTemp.remove(highest);
				}
				allMoviesTemp = averageRatingAllMoviesTemp;
				
	
				allMovies.addAll(allMoviesTemp);
				allMoviesTemp.clear();
			}			
	    } catch (Exception e) {
			e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
			ArrayList<String> errorArray = new ArrayList<String>();
			errorArray.add("error");
	        return errorArray;
	    }

		// if all titles watched from current director, recommend next favorite director, repeat until found a director that the user has not seen a movie from yet
		boolean foundMovie = false;
		for(int i = 0; i < allMovies.size(); i++) {
			if(titleList.contains(allMovies.get(i))) {
				
			} else {
				conn = null;
				try {
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
					"csce315903_14user", "GROUP14CS315");
					Statement stmt = conn.createStatement();
					String sqlStatement1 = "SELECT originalTitle FROM content WHERE titleId=" + allMovies.get(i) + ";";
					ResultSet after_result = stmt.executeQuery(sqlStatement1);
					while (after_result.next()) {
						result.add(after_result.getString("originalTitle"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					ArrayList<String> errorArray = new ArrayList<String>();
					errorArray.add("error");
					return errorArray;
				}
				System.out.println(result.get(0));
				foundMovie = true;
				return result;
			}
		}
		
		// if all movies from every director is seen, recommend most popular movie in favorite genre they havent seen
		HashMap<String, Integer> genreListOccurence = new HashMap<String, Integer>();
		HashMap<String, Integer> genreListRating = new HashMap<String, Integer>();
		HashMap<String, Double> genreListRatingAverage = new HashMap<String, Double>();
		ArrayList<String> genreListTemp = new ArrayList<String>();
		conn = null;
		try {
			//open a connection
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(Integer i : titleListSorted) {
				String sqlStatement1 = "SELECT genres FROM ContentCreators WHERE titleId = " + i + ";";
				ResultSet genre_result = stmt.executeQuery(sqlStatement1);
				String genre_string = "";
				while (genre_result.next()) {
					genre_string += genre_result.getString("genres");
				}
				genre_string = genre_string.replace("{", "").replace("}", "");
				genreListTemp = new ArrayList<String>(Arrays.asList(genre_string.split(",")));
				genreListOccurence.merge(genreListTemp.get(0),1,Integer::sum);
				Integer count = genreListRating.get(genreListTemp.get(0));
				if(count == null) {
					genreListRating.put(genreListTemp.get(0), ratingListSorted.get(titleListSorted.indexOf(i)));
				} else {
					genreListRating.put(genreListTemp.get(0), count + ratingListSorted.get(titleListSorted.indexOf(i)));
				}
				genreListTemp.clear();
			}
	    } catch (Exception e) {
			e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
			ArrayList<String> errorArray = new ArrayList<String>();
			errorArray.add("error");
	        return errorArray;
	    }

		// gets average of each genre and sorts greatest to least
		for (String key: genreListOccurence.keySet()) {
            genreListRatingAverage.put(key, ((Double.valueOf(genreListRating.get(key))) / (Double.valueOf(genreListOccurence.get(key)))));
        }
		list = new LinkedList<Map.Entry<String, Double> >(genreListRatingAverage.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
			public int compare(Map.Entry<String, Double> o2,
							Map.Entry<String, Double> o1)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Double> genreTemp = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> aa : list) {
			genreTemp.put(aa.getKey(), aa.getValue());
		}

		genreListRatingAverage = genreTemp;
		
		// gets all movies from every genre
		ArrayList<String> allMoviesGenreTemp;
		ArrayList<String> allMoviesGenre = new ArrayList<String>();
		conn = null;
		try {
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(String i : genreListRatingAverage.keySet()) {
				if(i.equals("-1")) {
					continue;
				}
				String sqlStatement1 = "SELECT titleId FROM content WHERE ('" + i + "' = ANY(genres));";
				ResultSet allMoviesGenre_result = stmt.executeQuery(sqlStatement1);
				String allMoviesGenre_string = "";
				while (allMoviesGenre_result.next()) {
					allMoviesGenre_string += allMoviesGenre_result.getString("titleId") + "~";
				}
				allMoviesGenre_string = allMoviesGenre_string.replace("{", "").replace("}", "");

				// convert list to array list and store it as private variable
				allMoviesGenreTemp = new ArrayList<String>(Arrays.asList(allMoviesGenre_string.split("~")));

				ArrayList<String> averageRatingAllMoviesGenre = new ArrayList<String>();
				for(String a : allMoviesGenreTemp) {
					String sqlStatement2 = "SELECT averageRating FROM content WHERE titleId = " + a + ";";
					ResultSet allAverageGenre_result = stmt.executeQuery(sqlStatement2);
					String allAverageGenre_string = "";
					while (allAverageGenre_result.next()) {
						allAverageGenre_string += allAverageGenre_result.getString("averageRating");
					}
					allAverageGenre_string = allAverageGenre_string.replace("{", "").replace("}", "");
					
					// convert list to array list and store it as private variable
					averageRatingAllMoviesGenre.add(allAverageGenre_string);
				}
				ArrayList<String> averageRatingAllMoviesGenreTemp = new ArrayList<String>();
				while(allMoviesGenreTemp.size() != 0) {
					int highest = averageRatingAllMoviesGenre.indexOf(Collections.max(averageRatingAllMoviesGenre));
					averageRatingAllMoviesGenreTemp.add(allMoviesGenreTemp.get(highest));
					averageRatingAllMoviesGenre.remove(highest);
					allMoviesGenreTemp.remove(highest);
				}
				allMoviesGenreTemp = averageRatingAllMoviesGenreTemp;
				
				allMoviesGenre.addAll(allMoviesGenreTemp);
				allMoviesGenreTemp.clear();
			}			
	    } catch (Exception e) {
			e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
			ArrayList<String> errorArray = new ArrayList<String>();
			errorArray.add("error");
	        return errorArray;
	    }

		// finds movie based on genre and recommends it
		for(int i = 0; i < allMoviesGenre.size(); i++) {
			result.add("You have watched every movie from every director you have seen, here is a top movie from your favorite genre: ");
			if(titleList.contains(allMoviesGenre.get(i))) {
				
			} else {
				conn = null;
				try {
					//open a connection
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
					"csce315903_14user", "GROUP14CS315");
					Statement stmt = conn.createStatement();
					String sqlStatement1 = "SELECT originalTitle FROM content WHERE titleId=" + allMovies.get(i) + ";";
					ResultSet after_result = stmt.executeQuery(sqlStatement1);
					while (after_result.next()) {
						result.add(after_result.getString("originalTitle"));
					}
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					ArrayList<String> errorArray = new ArrayList<String>();
					errorArray.add("error");
					return errorArray;
				}
				System.out.println(result.get(0));
				System.out.println(result.get(1));
				foundMovie = true;
				return result;
			}
		}

		ArrayList<String> errorArray = new ArrayList<String>();
		errorArray.add("error");
		return errorArray;
	}	

	//This function converts database dates into dates readable by the users
	public static String ConvertDate(LocalDate LocalDate){
		return LocalDate.format(DateTimeFormatter.ofPattern("LLLL dd yyyy"));
	}

	//This functions handles the slider on watch history to be adjustable
	static public void sliderChanged(){
		int value = newSlider.getValue();
		if(value == allTimeTitles.size()){
			value = value-1;
		}
		sliderArray.clear();
		sliderReverseArray.clear();
		for(int i = value; i >= 0; i--){
			sliderArray.add(allTimeTitles.get(i));
		}
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

	//This function is what creates the GUI once all the information is ready
    static public void createGUI(){
		//Setting up JFrame and pertint information
        contentViewer = new JFrame("Content Viewing Experience");

		//Creating the slider
		newSlider = new JSlider(0, allTimeDates.size());
		newSlider.setMajorTickSpacing(10);
		newSlider.setMinorTickSpacing(1);
		newSlider.setPaintTicks(true);
		newSlider.addChangeListener(e -> sliderChanged());

		newLabel.setFont((new Font("Times New Roman", Font.PLAIN, 16)));
		newLabel.setHorizontalAlignment(JLabel.CENTER);
		newLabel.setVerticalAlignment(JLabel.CENTER);
			

		//Creating some components which will be used within the interface
		JLabel tenDates = new JLabel("Watch History: " + ConvertDate(pastTenDates.get(0)) + " - " + ConvertDate(pastTenDates.get(pastTenDates.size()-1)));
		JLabel hundredDates = new JLabel("Watch History: " + ConvertDate(pastHundredDates.get(0)) + " - " + ConvertDate(pastHundredDates.get(pastHundredDates.size()-1)));
		JLabel allDates = new JLabel("Watch History: " + ConvertDate(allTimeDates.get(0)) + " - " + ConvertDate(allTimeDates.get(allTimeDates.size()-1)));
	    
		JTabbedPane tp = new JTabbedPane();
		JPanel watchHistory = new JPanel();
		JPanel viewerBewarePanel = new JPanel();
		JPanel movieList = new JPanel();
		JPanel dirChoi = new JPanel();

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
		int count = 0;
		DefaultListModel dlmBeware = new DefaultListModel();
		JList listBeware = new JList(dlmBeware);
		JScrollPane scrollPaneBeware = new JScrollPane(listBeware);

		for(String word : viewerBewareArr){
			if(count == 100){
				break;
			} else {
				count += 1;
				dlmBeware.addElement(word);				
			}
		}
		listBeware.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		DefaultListCellRenderer rendererBeware =  (DefaultListCellRenderer)listBeware.getCellRenderer();  
		rendererBeware.setHorizontalAlignment(JLabel.CENTER);  


		//Adding all the elements for the viewer's choice
		DefaultListModel dlmChoice = new DefaultListModel();
		JList listChoice = new JList(dlmChoice);
		JScrollPane scrollPaneChoice = new JScrollPane(listChoice);

		for(String word : vChoiceTitleIdRecs){
			dlmChoice.addElement(word);
		}
		listChoice.setFont(new Font("Times New Roman", Font.PLAIN, 22));
		DefaultListCellRenderer rendererChoice =  (DefaultListCellRenderer)listChoice.getCellRenderer();  
		rendererChoice.setHorizontalAlignment(JLabel.CENTER);  

		//Adding all the elements for the viewer's choice
		DefaultListModel dlmDir = new DefaultListModel();
		JList listDir = new JList(dlmDir);
		JScrollPane scrollPaneDir = new JScrollPane(listDir);
		for(String word : directorsChoiceArr){
			dlmDir.addElement(word);
		}
		listDir.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		DefaultListCellRenderer rendererDir=  (DefaultListCellRenderer)listDir.getCellRenderer();  
		rendererDir.setHorizontalAlignment(JLabel.CENTER);  

		
		//Adding the components to the JFrame
		tp.add("Watch History", watchHistory);
		tp.add("Viewer Beware", viewerBewarePanel);
		tp.add("Viewer's Choice",movieList);
		tp.add("Director's Choice", dirChoi);
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

		//Setting up viewer's Choice
		movieList.setLayout(new GridLayout(2,1));
		JLabel viewerChoiceLabel = new JLabel("Based on your favorite actors and genres, we think you should watch these videos");
		viewerChoiceLabel.setFont((new Font("Times New Roman", Font.PLAIN, 23)));
		viewerChoiceLabel.setHorizontalAlignment(JLabel.CENTER);
		viewerChoiceLabel.setVerticalAlignment(JLabel.CENTER);
		movieList.add(viewerChoiceLabel);
		movieList.add(scrollPaneChoice);

		//Setting up director's choice
		dirChoi.setLayout(new GridLayout(2,1));
		JLabel dirLabel = new JLabel("Based on your favorite directors and genres, we think you should watch these videos");
		dirLabel.setFont((new Font("Times New Roman", Font.PLAIN, 23)));
		dirLabel.setHorizontalAlignment(JLabel.CENTER);
		dirLabel.setVerticalAlignment(JLabel.CENTER);
		dirChoi.add(dirLabel);
		dirChoi.add(scrollPaneDir);


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


    public static void main(String[] args){
		//Nothing needs to occur here as we are running main from front page
	}

}