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
import java.util.LinkedHashSet; 
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.*;


public class CViewer extends JFrame implements ActionListener{
	private int userId;
	static JFrame contentViewer;
	// unsorted instances of ratings, dates, and titles
	private ArrayList<String> ratingList;
	private ArrayList<String> dateList;
	private ArrayList<String> titleList;
	private ArrayList<String> directorList;
	private ArrayList<String> genreList;
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
		assert ratingList.size() == dateList.size() && dateList.size() == titleList.size() && titleList.size() != 0: "Invalid list sizes.";
		sortByDate();
		askForHistoryLength();
		callDatabase();
		directorsChoice(uid);
		createGUI();

	}

	public static String ConvertDate(LocalDate LocalDate){
		return LocalDate.format(DateTimeFormatter.ofPattern("LLLL dd yyyy"));
	}

	static JSlider newSlider;
	static JLabel newLabel = new JLabel();
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
	    // JLabel directorChoiceTitle = new JLabel("Director's Choice: ");

		JTabbedPane tp = new JTabbedPane();
		JPanel watchHistory = new JPanel();
		JPanel recommendation = new JPanel();
		JPanel movieList = new JPanel();

		tenDates.setHorizontalAlignment(JLabel.CENTER);
		hundredDates.setHorizontalAlignment(JLabel.CENTER);
		allDates.setHorizontalAlignment(JLabel.CENTER);
		// directorChoiceTitle.setHorizontalAlignment(JLabel.CENTER);

		
		tenDates.setFont(new Font("Times New Roman", Font.PLAIN, 16));
	    hundredDates.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		allDates.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		// directorChoiceTitle.setFont(new Font("Times New Roman", Font.PLAIN, 16));


		
		
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
		
		//Adding the components to the JFrame
		tp.add("Watch History", watchHistory);
		tp.add("Recommendations", recommendation);
		tp.add("Movie List",movieList);
		tp.setSize(850,950);

		watchHistory.setSize(900,1000);
		recommendation.setSize(900,1000);
		watchHistory.setSize(900,1000);


		watchHistory.setLayout(new GridLayout(4,2));
		watchHistory.add(tenDates);
		watchHistory.add(scrollPaneTen);
		watchHistory.add(hundredDates);
		watchHistory.add(scrollPaneHun);
		watchHistory.add(allDates);
		watchHistory.add(scrollPaneAll);
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridLayout(2,1));
		watchHistory.add(newPanel);
		newPanel.add(newLabel);
		newPanel.add(newSlider);
		watchHistory.add(scrollSlider);

		// recommendation.setLayout(new GridLayout(1,2));
		// recommendation.add(directorChoiceTitle);



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

	private ArrayList<String> directorsChoice(int uid) {
		ArrayList<String> result = new ArrayList<String>();
		// dont forget we added a get director list into fetchwatchhistory when merging
		// also dont forget included linkedlisthashset
		// grab all titles from users and get
		HashMap<String, Integer> directorListOccurence = new HashMap<String, Integer>();
		HashMap<String, Integer> directorListRating = new HashMap<String, Integer>();
		HashMap<String, Double> directorListRatingAverage = new HashMap<String, Double>();
		ArrayList<String> directorListTemp = new ArrayList<String>();
		Connection conn = null;
		try {
			//open a connection
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(Integer i : titleListSorted) {
				// create sql statements to get info we need
				String sqlStatement1 = "SELECT directornameId FROM ContentCreators WHERE titleId = " + i + ";";
				// execute each statement and store info in string
				ResultSet director_result = stmt.executeQuery(sqlStatement1);
				String director_string = "";
				while (director_result.next()) {
					director_string += director_result.getString("directornameId");
				}
				// remove { and } from string then convert string to java.util.ArrayList
				director_string = director_string.replace("{", "").replace("}", "");
				// System.out.println(director_string);
				// convert list to array list and store it as private variable
				directorListTemp = new ArrayList<String>(Arrays.asList(director_string.split(",")));
				directorListOccurence.merge(directorListTemp.get(0),1,Integer::sum);
				Integer count = directorListRating.get(directorListTemp.get(0));
				// System.out.println(titleListSorted.indexOf(i));
				if(count == null) {
					directorListRating.put(directorListTemp.get(0), ratingListSorted.get(titleListSorted.indexOf(i)));
				} else {
					directorListRating.put(directorListTemp.get(0), count + ratingListSorted.get(titleListSorted.indexOf(i)));
				}
				directorListTemp.clear();
			}
			// conn.close();
	    } catch (Exception e) {
			e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
			ArrayList<String> errorArray = new ArrayList<String>();
			errorArray.add("error");
	        return errorArray;
	    }

		// gets average of each director and sorts greatest to least
		for (String key: directorListOccurence.keySet()) {
            directorListRatingAverage.put(key, ((Double.valueOf(directorListRating.get(key))) / (Double.valueOf(directorListOccurence.get(key)))));
        }
		List<Map.Entry<String, Double> > list =
			new LinkedList<Map.Entry<String, Double> >(directorListRatingAverage.entrySet());

		// Sort the list
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
		// directorListRatingAverage.entrySet().forEach(entry -> {System.out.println(entry.getKey() + " " + entry.getValue());});

		// gets all movies from every director
		ArrayList<String> allMoviesTemp;
		ArrayList<String> allMovies = new ArrayList<String>();
		conn = null;
		try {
			//open a connection
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(String i : directorListRatingAverage.keySet()) {
				if(i.equals("-1")) {
					continue;
				}
				// System.out.println(i);
				// create sql statements to get info we need
				String sqlStatement1 = "SELECT titleId FROM contentCreators WHERE ('" + i + "' = ANY(directornameId));";
				// execute each statement and store info in string
				ResultSet allMovies_result = stmt.executeQuery(sqlStatement1);
				String allMovies_string = "";
				while (allMovies_result.next()) {
					allMovies_string += allMovies_result.getString("titleId") + "~";
				}

				// remove { and } from string then convert string to java.util.ArrayList
				allMovies_string = allMovies_string.replace("{", "").replace("}", "");
				// convert list to array list and store it as private variable
				allMoviesTemp = new ArrayList<String>(Arrays.asList(allMovies_string.split("~")));

				ArrayList<String> averageRatingAllMovies = new ArrayList<String>();
				for(String a : allMoviesTemp) {
					String sqlStatement2 = "SELECT averageRating FROM content WHERE titleId = " + a + ";";
					// execute each statement and store info in string
					ResultSet allAverage_result = stmt.executeQuery(sqlStatement2);
					String allAverage_string = "";
					while (allAverage_result.next()) {
						allAverage_string += allAverage_result.getString("averageRating");
					}
					// remove { and } from string then convert string to java.util.ArrayList
					allAverage_string = allAverage_string.replace("{", "").replace("}", "");
					// System.out.println(director_string);
					// convert list to array list and store it as private variable
					averageRatingAllMovies.add(allAverage_string);
				}
				// for(String j : averageRatingAllMovies) {
				// 	System.out.println(j);
				// }
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
			// conn.close();
			
	    } catch (Exception e) {
			e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
			ArrayList<String> errorArray = new ArrayList<String>();
			errorArray.add("error");
	        return errorArray;
	    }

		// System.out.println("Do we get here5?");
		// if all titles watched from current director, recommend next favorite director, repeat until found a director that the user has not seen a movie from yet
		boolean foundMovie = false;
		for(int i = 0; i < allMovies.size(); i++) {
			if(titleList.contains(allMovies.get(i))) {
				
			} else {
				conn = null;
				try {
					//open a connection
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
					"csce315903_14user", "GROUP14CS315");
					Statement stmt = conn.createStatement();
					// System.out.println(i);
					// create sql statements to get info we need
					String sqlStatement1 = "SELECT originalTitle FROM content WHERE titleId=" + allMovies.get(i) + ";";
					// execute each statement and store info in string
					ResultSet after_result = stmt.executeQuery(sqlStatement1);
					while (after_result.next()) {
						result.add(after_result.getString("originalTitle"));
					}
					// System.out.println("Do we get here4?");
					// remove { and } from string then convert string to java.util.ArrayList
					// convert list to array list and store it as private variable
					
					
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					ArrayList<String> errorArray = new ArrayList<String>();
					errorArray.add("error");
					return errorArray;
				}
				System.out.println(result.get(0));
				foundMovie = true;
				// for(int k = 0; k < result.size(); k++) {
				// 	System.out.println(result.get(k));
				// }
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
				// create sql statements to get info we need
				String sqlStatement1 = "SELECT genres FROM ContentCreators WHERE titleId = " + i + ";";
				// execute each statement and store info in string
				ResultSet genre_result = stmt.executeQuery(sqlStatement1);
				String genre_string = "";
				while (genre_result.next()) {
					genre_string += genre_result.getString("genres");
				}
				// remove { and } from string then convert string to java.util.ArrayList
				genre_string = genre_string.replace("{", "").replace("}", "");
				// System.out.println(director_string);
				// convert list to array list and store it as private variable
				genreListTemp = new ArrayList<String>(Arrays.asList(genre_string.split(",")));
				genreListOccurence.merge(genreListTemp.get(0),1,Integer::sum);
				Integer count = genreListRating.get(genreListTemp.get(0));
				// System.out.println(titleListSorted.indexOf(i));
				if(count == null) {
					genreListRating.put(genreListTemp.get(0), ratingListSorted.get(titleListSorted.indexOf(i)));
				} else {
					genreListRating.put(genreListTemp.get(0), count + ratingListSorted.get(titleListSorted.indexOf(i)));
				}
				genreListTemp.clear();
			}
			// conn.close();
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
			//open a connection
	        Class.forName("org.postgresql.Driver");
	        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315903_14db",
	           "csce315903_14user", "GROUP14CS315");
	        Statement stmt = conn.createStatement();
			for(String i : genreListRatingAverage.keySet()) {
				if(i.equals("-1")) {
					continue;
				}
				// System.out.println(i);
				// create sql statements to get info we need
				String sqlStatement1 = "SELECT titleId FROM content WHERE ('" + i + "' = ANY(genres));";
				// execute each statement and store info in string
				ResultSet allMoviesGenre_result = stmt.executeQuery(sqlStatement1);
				String allMoviesGenre_string = "";
				while (allMoviesGenre_result.next()) {
					allMoviesGenre_string += allMoviesGenre_result.getString("titleId") + "~";
				}
				// System.out.println("Do we get here4?");
				// remove { and } from string then convert string to java.util.ArrayList
				allMoviesGenre_string = allMoviesGenre_string.replace("{", "").replace("}", "");
				// convert list to array list and store it as private variable
				allMoviesGenreTemp = new ArrayList<String>(Arrays.asList(allMoviesGenre_string.split("~")));

				ArrayList<String> averageRatingAllMoviesGenre = new ArrayList<String>();
				for(String a : allMoviesGenreTemp) {
					String sqlStatement2 = "SELECT averageRating FROM content WHERE titleId = " + a + ";";
					// execute each statement and store info in string
					ResultSet allAverageGenre_result = stmt.executeQuery(sqlStatement2);
					String allAverageGenre_string = "";
					while (allAverageGenre_result.next()) {
						allAverageGenre_string += allAverageGenre_result.getString("averageRating");
					}
					// remove { and } from string then convert string to java.util.ArrayList
					allAverageGenre_string = allAverageGenre_string.replace("{", "").replace("}", "");
					// System.out.println(director_string);
					// convert list to array list and store it as private variable
					averageRatingAllMoviesGenre.add(allAverageGenre_string);
				}
				// for(String j : averageRatingAllMovies) {
				// 	System.out.println(j);
				// }
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
			// conn.close();
			
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
					// System.out.println(i);
					// create sql statements to get info we need
					String sqlStatement1 = "SELECT originalTitle FROM content WHERE titleId=" + allMovies.get(i) + ";";
					// execute each statement and store info in string
					ResultSet after_result = stmt.executeQuery(sqlStatement1);
					while (after_result.next()) {
						result.add(after_result.getString("originalTitle"));
					}
					// System.out.println("Do we get here4?");
					// remove { and } from string then convert string to java.util.ArrayList
					// convert list to array list and store it as private variable
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
				// for(int k = 0; k < result.size(); k++) {
				// 	System.out.println(result.get(k));
				// }
				return result;
			}
		}

		ArrayList<String> errorArray = new ArrayList<String>();
		errorArray.add("error");
		return errorArray;
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