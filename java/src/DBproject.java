/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		// if (args.length != 3) {
		// 	System.err.println (
		// 		"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		//             " <dbname> <port> <user>");
		// 	return;
		// }//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			//String dbname = args[0];
			//String dbport = args[1];
			//String user = args[2];
			
			String dbname = "titillaty_DB";
			String dbport = "9999";
			String user = "titillaty";
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			Greeting();
			while(keepon){
				System.out.println("\nMAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add User");
				System.out.println("2. Add Post"); //in this one, you ask what user you want to add a post for, then add the post
				System.out.println("3. View All Posts"); // view all the posts in the database
				System.out.println("4. Follow a user");
				System.out.println("5. Search user by username"); //view profile of user
				System.out.println("6. Search user by tags");
				System.out.println("7. View photos based on tags"); //view pictues based on tags
				System.out.println("8. View photos based on users"); //view pictures of the user
				System.out.println("9. View photos based on date"); //view posts based on date
				System.out.println("10. View newsfeed of top photos"); //view news feed of who username is following
				System.out.println("11. List top photos of the database"); //view top photos of the entire database (likes)
				System.out.println("12. List out most popular users of the database"); // view top users based on followers
				System.out.println("13. Tag a user in a post"); // tag another user on the post
				System.out.println("14. Upload a photo to file system!"); // upload a file to HDFS
				System.out.println("15. Download a photo (locally)!"); // download a file from HDFS
				System.out.println("16. EXIT\n");
				
				switch (readChoice()){
					case 1: AddUser(esql); break;
					case 2: AddPost(esql); break;
					case 3: ViewAllPosts(esql); break;
					case 4: FollowUser(esql); break;
					case 5: SearchProfileBasedOnUser(esql); break;
					case 6: SearchProfileBasedOnTags(esql); break;
					case 7: ViewPhotosByTag(esql); break;
					case 8: ViewPhotosOfUser(esql); break;
          			case 9: ViewPhotosOnDate(esql); break;
					case 10: ViewNewsFeedOfFollowing(esql); break;
					case 11: PopularPhotos(esql); break;
					case 12: PopularUsers(esql); break;
					case 13: TagAUser(esql); break;
					case 14: UploadPhoto(); break;
					case 15: DownloadPhoto(); break;
					case 16: keepon = false; break;
					default : System.out.println("Unrecognized choice! Try again."); break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					Done();
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static void Greeting(){
		System.out.println(
			   "\n\n*******************************************************\n" +
			   "               Welcome to Instagram 2.0      	               \n" +
			   "*******************************************************\n");
   }//end Greeting

   public static void Done(){
	System.out.println(
		   "\n\n*******************************************************\n" +
		   "            Thank You For Using Our Application!    	               \n" +
		   "*******************************************************\n");
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddUser(DBproject esql) {//1 sandy
		try {
			int user_id;
			String user_id_inString;
			String fullname;
			String username;
			String email;
			String user_password;

			System.out.print("\tEnter email address: ");
			email = in.readLine();
			System.out.print("\tEnter a username: ");
			username = in.readLine();
			System.out.print("\tEnter a user ID: ");
			user_id_inString = in.readLine();
			System.out.print("\tEnter your full name: ");
			fullname = in.readLine();
			System.out.print("\tEnter new password: ");
			user_password = in.readLine();
			
			user_id = Integer.parseInt(user_id_inString);

			String sql_stmt_2 = String.format("INSERT INTO DBUsers (userID, fullname, username, email, user_password) VALUES ('%d','%s', '%s', '%s', '%s');", user_id, fullname, username, email, user_password);
			esql.executeUpdate(sql_stmt_2);
			String sql_stmt_3 = String.format("INSERT INTO UserProfile (profile_id, username_id, num_posts, followers, followings, follow_status) VALUES ('%d','%s', '%d', '%d', '%d', '%s');", user_id, username, 0, 0, 0, "TRUE");
			esql.executeUpdate(sql_stmt_3);

			System.out.println("\nAdding user to database...\n");
			// MAKE DIRECTORY IN HDFS FOR NEW USER
			String cmd="hadoop fs -mkdir /instagram/" + username;
			//System.out.println(cmd);

			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);
			pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line=buf.readLine())!=null) {
				System.out.println(line); }

			System.out.println("\n\tSuccessfully added new user!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void AddPost(DBproject esql) {//2
		// Given a post_id, username, date_posted, tags, and photo_url, add a post in the DB
		try{
			//String query = "INSERT INTO Post(post_id, username_id, likes, date_posted, num_comments, tags, photo_url) VALUES (";
			//String input = "";
			
			int post_id;
			String post_id_inString;
			String username;
			String date;
			String tags;
			String fs;
	 
			System.out.print("\tEnter Post_ID: ");
			post_id_inString = in.readLine();
			System.out.print("\tEnter Username: ");
			username = in.readLine();		
			System.out.print("\tEnter Date Posted (Ex: MM/DD/YYYY): ");
			date = in.readLine();
			System.out.print("\tEnter One Tag: ");
			tags = in.readLine();
			System.out.print("\tEnter Photo File Location: ");
			fs = in.readLine();

			post_id = Integer.parseInt(post_id_inString);

			String query = String.format("INSERT INTO Post(post_id, username_id, likes, date_posted, num_comments, tags, photo_url) VALUES ('%d','%s', '%d', '%s', '%d', '%s', '%s');", post_id, username, 0, date, 0, tags, fs);
			esql.executeUpdate(query);

			System.out.println("\nAdding photo to database...\n");
			// ADD PHOTO TO HDFS
			String cmd="hadoop fs -put " + fs + " /instagram/" + username + "/" + username + "-" + post_id + ".jpg";
			//System.out.println(cmd);

			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);
			pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line=buf.readLine())!=null) {
				System.out.println(line); }

			System.out.println("\n\tSuccessfully added a new post!\n");
		 }catch(Exception e){
			System.err.println (e.getMessage());
		 }
	}

	public static void ViewAllPosts(DBproject esql) {//3
		// View all the posts in the DB

		try{
			System.out.println("Here are all the posts! \n");
			String query = "SELECT * FROM Post;"; 
			esql.executeQueryAndPrintResult(query);
		 }catch(Exception e){
			System.err.println (e.getMessage());
		 }
	}

	public static void FollowUser(DBproject esql) {//4 need to update follower number on user profile
		try {
			String user_being_followed;
			String user_follower;

			System.out.print("Enter your username: ");
			user_follower = in.readLine();
			System.out.print("Enter user you want to follow: ");
			user_being_followed = in.readLine();

			String sql_stmt = String.format("INSERT INTO UserFollowing (username_id, follower) VALUES ('%s', '%s');", user_follower, user_being_followed);
			esql.executeUpdate(sql_stmt);

			System.out.println("You are now following: " + user_being_followed + "\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void SearchProfileBasedOnUser(DBproject esql) { //5
		try {
			String user;

			System.out.print("Enter the username of the profile you want to see: ");
			user = in.readLine();
			esql.executeQueryAndPrintResult(String.format("SELECT * FROM UserProfile WHERE username_id = '%s';", user));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void SearchProfileBasedOnTags(DBproject esql) { //6
		try {
			String tag;
			System.out.print("Enter the tag you want to search for: ");
			tag = in.readLine();

			System.out.println("Here are the usernames that correspond to this tag\n");
			esql.executeQueryAndPrintResult(String.format("SELECT username_id FROM Post WHERE tags = '%s';", tag));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void ViewPhotosByTag(DBproject esql)  {// 7
		// User enters a tag to search and database replies with photos containing tag

		//Print message to get input from user and input fullname
		try{
			System.out.print("\nWhich hashtag would you like to see photos for? : ");
			String hashtag = in.readLine();

			//Executes quesry and prints the result
			//esql.executeQueryAndPrintResult("select photo_url from post where tags = '#" + hashtag + "'");
			esql.executeQueryAndPrintResult(String.format("SELECT photo_url FROM Post WHERE tags = '%s';", hashtag));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void ViewPhotosOfUser(DBproject esql) {// 8
		// Enter username or user full name and get photos of them

		//Print menu so user can search for a User bases on their full name or their username
		try{
			System.out.println("\n");
			System.out.println("\tEnter 1 or 2:\n");
			System.out.println("\t1: Search by Full Name");
			System.out.println("\t2: Search by username");
			System.out.println("---------------------------------");
		 

			switch(readChoice()){
				// asks user to enter full name to search for and reads in input
				case 1: System.out.print("Enter users full name: ");
						String fullName = in.readLine();
						System.out.println("");
						
						//executes sql query
						esql.executeQueryAndPrintResult("select photo_url from post where username_id = (select username from DBusers where fullname = '" + fullName + "');");
				break;
				// asks user to enter username to search for and reads in input
				case 2: System.out.print("Enter users username: ");
						String username = in.readLine();
						System.out.println("");

						// executes sql statement and prints result
						esql.executeQueryAndPrintResult("select photo_url from post where username_id = '" + username + "';");
						break;
			}
			// prints extra line for formatting	
			System.out.println("");	

		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
			
	}
	
	public static void ViewPhotosOnDate(DBproject esql) {//9
		// view photos based on date user enters
		try{
			//Prompts user to enter a date and reads in their input
			System.out.print("Enter date to view posted photos(YYYY-MM-DD): ");
			String date = in.readLine();
			System.out.println("");

			//Executes sequal statement to get posts from date
			esql.executeQueryAndPrintResult("select * from post where date_posted = '" + date + "';");
		} catch (Exception e) {
			//Catches exception and prints error message
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void ViewNewsFeedOfFollowing(DBproject esql) { // 10
		// Enter a username and view a news feed of who they are following 
		try{
			//Asks for username to view newsfeed of
			System.out.print("Enter a users FULL NAME to view their newsfeed: ");
			String fullName = in.readLine();

			//executes sql statement that returns a list of people user is following
			List<List<String>> result = esql.executeQueryAndReturnResult("select follower from UserFollowing where username_id = (select username from DBUsers where fullname = '" + fullName + "');");
			
			//checks to see if list of people following is not empty
			if(result != null && !result.isEmpty() ){
				//loops through list of people following and outputs their posts
				for(int i = 0; i < result.size(); i++){
					esql.executeQueryAndPrintResult("select * from post where username_id = '" + result.get(i).get(0) + "';");
					System.out.println("");
				}
			}
		//catches exception
		}catch (Exception e){
			System.out.println(e.getMessage() + "\n");
		}
	
	}


	
	public static void PopularPhotos(DBproject esql) {//11 
		try {
			System.out.println("Here are our top 10 most popular photos! \n");
			esql.executeQueryAndPrintResult(String.format("SELECT username_id,likes,photo_url FROM Post ORDER BY likes DESC LIMIT 10;"));
			System.out.print("\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}


	public static void PopularUsers(DBproject esql) {//12 
		try {
			System.out.println("Here are our top 10 most popular users: \n");
			esql.executeQueryAndPrintResult(String.format("SELECT username_id, COUNT(*) AS follower FROM UserFollowing GROUP BY username_id ORDER BY follower DESC LIMIT 10;"));
			System.out.print("\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void CommentPost(DBproject esql) { 
		try {
			String user;
			String comment;

			System.out.print("Enter the user you want to comment on: ");
			user = in.readLine();
			System.out.print("Enter your comment: ");
			comment = in.readLine();

			String query = String.format("INSERT INTO PostComment (username_id, comment) VALUES ('%s', '%s');", user, comment);
			esql.executeUpdate(query);

			System.out.println("Successfully added comment!\n"); 
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}	

	public static void TagAUser(DBproject esql) {//13
		try {
			String post_id_string;
			int post_id;
			String taggeduser;

			System.out.print("Enter the id of the post to tag: ");
			post_id_string = in.readLine();
			System.out.print("Enter the user you want to tag: ");
			taggeduser = in.readLine();
			post_id = Integer.parseInt(post_id_string);

			String sql_stmt = String.format("INSERT INTO UserTagged (pid, tagged) VALUES ('%d', '%s');", post_id, taggeduser);
			esql.executeUpdate(sql_stmt);
			
			//System.out.println("Here is the list of users tagged in this post\n");
			//String sql_stmt1 = String.format("SELECT tagged FROM UserTagged WHERE pid = '%d';", post_id);
			//esql.executeUpdate(sql_stmt1);
			
			System.out.println("Successfully tagged " + taggeduser + " in post #" + post_id + "\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void UploadPhoto() { //14
		try {

			String user;
			String num;
			String fs;

			System.out.print("Enter username: ");
			user = in.readLine();
			System.out.print("Enter post #: ");
			num = in.readLine();
			System.out.print("Input File Location: ");
			fs = in.readLine();

			String cmd="hadoop fs -put " + fs + " /instagram/" + user + "/" + user + "-" + num + ".jpg";
			//System.out.println(cmd);

			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);
			pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line=buf.readLine())!=null) {
				System.out.println(line); }
			System.out.println("Successfully uploaded photo to HDFS!");
		} catch (Exception e) {
				e.printStackTrace();
			}	
	}


	public static void DownloadPhoto() { //15
		try {
			String user;
			String num;

			System.out.print("Enter username: ");
			user = in.readLine();
			System.out.print("Enter post #: ");
			num = in.readLine();

			//String cmd="hadoop fs -mkdir /test/javatest";
			String cmd="hadoop fs -get /instagram/" + user + "/" + user + "-" + num + ".jpg" + " /Users/titillaty/F20-CS179G/downloadedPhotos/";
			//System.out.println(cmd);
			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);
			pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line=buf.readLine())!=null) {
				System.out.println(line); }
			System.out.println("Photo successfully downloaded!");
		} catch (Exception e) {
				e.printStackTrace();
			}	
	}
}


