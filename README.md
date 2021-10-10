# Project2
In order to run the GUI and all its components, there is a specific way to do so since it is within a package named "project2" 

First run, javac -d . FrontPage.java Content_Analyst.java CViewer.java -Xlint 

Then run, java -cp “.;postgresql-42.2.8.jar” project2.FrontPage 

Finally, this will open the GUI from which you can get some recommendations and content 
