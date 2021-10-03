import java.sql.*;
import java.util.Scanner; 

/*
CSCE 315
9-27-2021 Lab
 */
public class jdbcpostgreSQL {

  //Commands to run this script
  //This will compile all java files in this directory
  //javac *.java 
  //This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code
  //Windows: java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL
  //Mac/Linux: java -cp ".:postgresql-42.2.8.jar" jdbcpostgreSQL

  //MAKE SURE YOU ARE ON VPN or TAMU WIFI TO ACCESS DATABASE
  public static void main(String args[]) {
 
    //Building the connection with your credentials
    //TODO: update dbName, userName, and userPassword here
     Connection conn = null;
     String teamNumber = "14";
     String sectionNumber = "903";
     String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
     String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
     String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
     String userPassword = "GROUP14CS315";

    //Connecting to the database 
    try {
        conn = DriverManager.getConnection(dbConnectionString,userName, userPassword);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }

     System.out.println("Opened database successfully");
     
     try{
       //create a statement object
       Statement stmt = conn.createStatement();
      Scanner query_selector = new Scanner(System.in);
      System.out.print("Enter The Query Number from (1 to 20) : "); 

      int number = query_selector.nextInt();

        //Running a query
       //TODO: update the sql command her
      //using switch statement for this
      while (number > 0 && number <= 20){
        switch (number){
          case 1: {
            //1  printing all the data from content table
                String sqlStatement = "SELECT * FROM Content";   //first query
                ResultSet result = stmt.executeQuery(sqlStatement);
                System.out.println("--------------------Query Results--------------------");
                while (result.next()) {
                    System.out.println(result.getString("titleId") + " | \t" +  
                            result.getString("titleType") + " | \t" +  
                            result.getString("originalTitle") + " | \t \t" + 
                            result.getString("startYear") + " | \t" + 
                            result.getString("endYear") + " | \t" + 
                            result.getString("runtimeMinutes") + " | \t" + 
                            result.getString("genres") + " | \t \t" +
                            result.getString("averageRating") + " | \t" + 
                            result.getString("numVotes"));                       //this print statement is for a content
              }
              break;
          }

          case 2: {
            //2 printing all the data from nameLookup Table
              String sqlStatement = "SELECT * FROM nameLookUp";   //2 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                  System.out.println(result.getString("nameId") + " | \t" +  
                                    result.getString("primaryName") + " | \t" +  
                                    result.getString("birthYear") + " | \t \t" + 
                                    result.getString("deathYear") + " | \t" + 
                                    result.getString("primaryProfession"));   
              }
              break;
            }
          

          case 3: {
            //3 printing all the data from Users Table
              String sqlStatement = "SELECT * FROM Users";   //3 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                  System.out.println(result.getString("userId") + " | \t" +  
                                  result.getString("rating") + " | \t" +  
                                  result.getString("date") + " | \t \t" + 
                                  result.getString("titleId"));   
              }
              break;
          }

          case 4: {
            //4 printing all the data form ContentCreators Table
              String sqlStatement = "SELECT * FROM ContentCreators";   //4 query
              ResultSet result = stmt.executeQuery(sqlStatement);
              System.out.println("--------------------Query Results--------------------");
              while (result.next()) {
                System.out.println(result.getString("titleid") + " | \t" +  
                                  result.getString("directornameid") + " | \t" +  
                                  result.getString("writernameid"));   
              }
              break;
          }


          case 5: {
            //5 printing all the data form castMembers Table
              String sqlStatement = "SELECT * FROM CastMembers";   //5 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                  System.out.println(result.getString("titleid") + " | \t" +  
                                  result.getString("nameid") + " | \t" +  
                                  result.getString("category") + " | \t" +  
                                  result.getString("characters"));   
              }
              break;
          }

          case 6: {
            //6 This will print the average rating with the original title from Content table by descending order of average rating
              String sqlStatement = "SELECT averageRating, originalTitle FROM Content ORDER BY averageRating DESC";   //6 query
              ResultSet result = stmt.executeQuery(sqlStatement);
              System.out.println("--------------------Query Results--------------------");
              while (result.next()) {
                System.out.println(result.getString("averageRating") + " | \t" +  
                                  result.getString("originalTitle")  
                                  );   
              }
              break;
          }

          case 7: {
            //7 This query will check if there is any id intersecting between the namelookup and Castmembers 
            String sqlStatement = "SELECT  nameid FROM castmembers INTERSECT SELECT nameId  FROM NameLookup";   //7 query
            System.out.println("--------------------Query Results--------------------");
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
              System.out.println(result.getString("nameid") );   
            }
            break;
            
          }

          case 8: {
              //8 Print the actors ,  actress from castmembers
              String sqlStatement = "SELECT nameid, characters FROM CastMembers WHERE (category = 'actor' OR category = 'actress')";   //8 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                System.out.println(result.getString("nameid") + " | \t" +  
                                  result.getString("characters") );   
              }
              break;
          }

          case 9: {
            //9 This will print the cast members name and if the character is actor then it will show up the character they played
            String sqlStatement = "SELECT CastMembers.nameId, NameLookup.primaryName, CastMembers.category, CastMembers.characters FROM CastMembers INNER JOIN NameLookup ON CastMembers.nameId=NameLookup.nameID WHERE (category = 'actor')";   //9 query
            System.out.println("--------------------Query Results--------------------");
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                System.out.println(result.getString("nameId") + " | \t" +  
                                result.getString("primaryname") + " | \t" +  
                                result.getString("category") + " | \t \t" + 
                                result.getString("characters"));                      
            }
            break;
          }

          case 10: {
            //10 this will query the actor and actress with their characters
              String sqlStatement = "SELECT CastMembers.nameId, NameLookup.primaryName, CastMembers.category, CastMembers.characters FROM CastMembers INNER JOIN NameLookup ON CastMembers.nameId=NameLookup.nameID WHERE (category = 'actor' OR category = 'actress')";   //10 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                System.out.println(result.getString("nameId") + " | \t" +  
                                  result.getString("primaryname") + " | \t" +  
                                  result.getString("category") + " | \t \t" + 
                                  result.getString("characters"));                      
              }
            
          }

          case 11: {
            //11 This will query originaltitle with its director id with average rating 
              String sqlStatement = "SELECT Content.originalTitle, Content.titleType , Content.averageRating, ContentCreators.directorNameID[1] FROM Content INNER JOIN ContentCreators ON Content.titleId=ContentCreators.titleId WHERE Content.averageRating>8.0 ORDER BY Content.averageRating DESC";   //11 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                  System.out.println(result.getString("originalTitle") + " | \t" +  
                                result.getString("titleType") + " | \t" +  
                                result.getString("averageRating") + " | \t" +  
                                result.getString("directornameid"));                      
            }
            break;
          }

          case 12: {
                //12  This query prints the original title of the content from content table and print its director accordingly
                String sqlStatement = "SELECT ContentCreators.titleId, NameLookup.primaryName, Content.originalTitle FROM ContentCreators INNER JOIN NameLookup ON ContentCreators.directorNameId[1]=NameLookup.nameId INNER JOIN Content ON ContentCreators.titleId=Content.titleID;";   //12 query
                System.out.println("--------------------Query Results--------------------");
                ResultSet result = stmt.executeQuery(sqlStatement);
                while (result.next()) {
                    System.out.println(result.getString("titleid") + " | \t" +  
                                  result.getString("primaryName") + " | \t" +  
                                  result.getString("originalTitle"));                      
              }
              break;
          }

          case 13: {
              //13 This will query content with 10 rating  with its originaltitle,titletype, its rating with the  numVotes, genre and startyear on descending order or number of votes
            String sqlStatement = "SELECT averageRating, originalTitle, titletype , numVotes, genres, startYear FROM Content WHERE (averageRating=10) ORDER BY numVotes DESC";   //13 query
            System.out.println("--------------------Query Results--------------------");
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                System.out.println(result.getString("originalTitle") + " | \t" +  
                              result.getString("averagerating") + " | \t" + 
                              result.getString("titletype") + " | \t" +  
                              result.getString("numVotes") + " | \t" +  
                              result.getString("genres") + " | \t" +  
                              result.getString("startYear")
                              );                      
              }
              break;
          }

          case 14: {

        //14 This will query all the movies with their rating, thir original name , number of votes, and it will pring in descending order by number of votes, this query only shows the top 10 from the list
                String sqlStatement = "SELECT averageRating, originalTitle, titleType, numVotes, genres, startYear FROM Content WHERE (averageRating>=8 AND titleType = 'movie') ORDER BY numVotes DESC FETCH FIRST 10 ROWS ONLY";   //14 query
                System.out.println("--------------------Query Results--------------------");
                ResultSet result = stmt.executeQuery(sqlStatement);
                while (result.next()) {
                    System.out.println(result.getString("originalTitle") + " | \t" +  
                                    result.getString("averagerating") + " | \t" + 
                                    result.getString("titletype") + " | \t" +  
                                    result.getString("numVotes") + " | \t" +  
                                    result.getString("genres") + " | \t" +  
                                    result.getString("startYear")
                                    );                      
                }
                break;
          }

          case 15: {
              //15 This will pring the top 10 drama content with averagerating > 8
              String sqlStatement = "SELECT * FROM Content WHERE ('Drama'=ANY(genres) AND averageRating>=8 AND titleType='movie') ORDER BY averageRating DESC FETCH FIRST 10 ROWS ONLY";   //15 query
              ResultSet result = stmt.executeQuery(sqlStatement);
              System.out.println("--------------------Query Results--------------------");
              while (result.next()) {
                  System.out.println(result.getString("titleId") + " | \t" +  
                                  result.getString("titleType") + " | \t" +  
                                  result.getString("originalTitle") + " | \t \t" + 
                                  result.getString("startYear") + " | \t" + 
                                  result.getString("endYear") + " | \t" + 
                                  result.getString("runtimeMinutes") + " | \t" + 
                                  result.getString("genres") + " | \t \t" +
                                  result.getString("averageRating") + " | \t" + 
                                  result.getString("numVotes"));                       //this print statement is for a content
            }
            break;
          }

          case 16: {
              //16 This query will print all content with its director whose rating is 10 in descending order of number of votes
              String sqlStatement = "SELECT Content.originalTitle, Content.averageRating, Content.numVotes, ContentCreators.directorNameID[1], nameLookup.primaryName FROM Content INNER JOIN ContentCreators ON Content.titleId=ContentCreators.titleId INNER JOIN NameLookup ON ContentCreators.directorNameId[1]=NameLookup.nameId WHERE Content.averageRating=10 ORDER BY numVotes DESC";   //16 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                System.out.println(result.getString("originalTitle") + " | \t" +  
                                  result.getString("averagerating") + " | \t" + 
                                  result.getString("numVotes") + " | \t" +  
                                  result.getString("directorNameId") + " | \t" +  
                                  result.getString("primaryname")
                                  );                      
              } 
              break;
            
          }
          case 17: {
              //17 this will print all the content whose rating is 10 and with its cast member in order of number of votes
              String sqlStatement = "SELECT Content.originalTitle, Content.averageRating, Content.numVotes, CastMembers.nameId, nameLookup.primaryName FROM Content INNER JOIN CastMembers ON Content.titleId=CastMembers.titleId INNER JOIN NameLookup ON CastMembers.nameId=NameLookup.nameId WHERE Content.averageRating=10 ORDER BY numVotes;";   //17 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                System.out.println(result.getString("originalTitle") + " | \t" +  
                                  result.getString("averagerating") + " | \t" + 
                                  result.getString("numVotes") + " | \t" +  
                                  result.getString("nameId") + " | \t" +  
                                  result.getString("primaryname")
                                  );                      
              } 
              break;
            
          }
          case 18: {
              //18 It will print all the contetent with cast members and their characters accordingly
              String sqlStatement = "SELECT Content.titleId, Content.originalTitle, CastMembers.nameId, NameLookup.primaryName, CastMembers.characters FROM Content INNER JOIN CastMembers ON Content.titleId=CastMembers.titleID INNER JOIN NameLookup ON CastMembers.nameId=NameLookup.nameId ORDER BY Content.titleId";   //18 query
              System.out.println("--------------------Query Results--------------------");
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                System.out.println(result.getString("titleId") + " | \t" +  
                                  result.getString("originalTitle") + " | \t" + 
                                  result.getString("nameid") + " | \t" +  
                                  result.getString("primaryname") + " | \t" +  
                                  result.getString("characters")
                                  );                      
              } 

          }
          case 19: {
            //19  print number of rating each users gave
            String sqlStatement = "SELECT Users.userId, NameLookup.primaryName, cardinality(rating) FROM Users INNER JOIN NameLookup ON Users.userId=NameLookup.nameId";   //19 query
            System.out.println("--------------------Query Results--------------------");
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                System.out.println(result.getString("userId") + " | \t" +  
                                  result.getString("nameLookup") + " | \t" + 
                                  result.getString("primaryname") + " | \t" +  
                                  result.getString("cardinality")
                                  );                      
            } 
            break;
          }

          case 20: {
              //20  This will print the users most recent content rating
              String sqlStatement = "SELECT Users.userId, Users.date[1], Users.titleId[1], Content.originalTitle, Users.rating[1] FROM Users INNER JOIN Content ON Users.titleId[1]=Content.titleId";   //20 query
              ResultSet result = stmt.executeQuery(sqlStatement);
              while (result.next()) {
                System.out.println(result.getString("userid") + " | \t" +  
                                  result.getString("date") + " | \t" +  
                                  result.getString("titleid") + " | \t" +
                                  result.getString("originalTitle") + " | \t" +
                                  result.getString("rating"));   
              }
              break;
          }

          default: {
            System.out.println(" The input is not in range ");
            break;
          }
        } 
        System.out.print("Enter The Query Number from (1 to 20) : "); 
        number = query_selector.nextInt();
      }   
   } catch (Exception e){
       e.printStackTrace();
       System.err.println(e.getClass().getName()+": "+e.getMessage());
       System.exit(0);
   }
    
    //closing the connection
    try {
      conn.close();
      System.out.println("Connection Closed.");
    } catch(Exception e) {
      System.out.println("Connection NOT Closed.");
    }//end try catch
  }//end main
}//end Class
