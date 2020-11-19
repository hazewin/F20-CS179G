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
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
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
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("\nMAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add User");
				System.out.println("2. Add Post"); //in this one, you ask what user you want to add a post for, then add the post
				System.out.println("3. View All Posts"); // view all the posts in the database
				System.out.println("4. Follow a user");
				System.out.println("5. Search user by username"); //view profile of user
				System.out.println("6. Search user by tags");
				System.out.println("7. View photos based on tags");
				System.out.println("8. View photos based on users"); //view pictures of the user
				System.out.println("9. View photos based on date");
				System.out.println("10. View newsfeed of top photos");
				System.out.println("11. View statistics of a photo");
				System.out.println("12. List top photos of the database");
				System.out.println("13. List out most popular users of the database");
				System.out.println("14. EXIT");
				
				switch (readChoice()){
					case 1: AddUser(esql); break;
					case 2: AddPost(esql); break;
					case 3: ViewAllPosts(esql); break;
					case 4: FollowUser(esql); break;
					case 5: SearchProfileBasedOnUser(esql); break;
					case 6: SearchProfileBasedOnTags(esql); break;
					case 7: ViewPhotosByTag(esql); break;
					case 8: ViewPhotosOfUser(esql); break;
					//case 9: FindPassengersCountWithStatus(esql); break;
					case 13: PopularUsers(esql); break;
					case 14: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
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

			System.out.print("Enter email address: ");
			email = in.readLine();
			System.out.print("Enter a username: ");
			username = in.readLine();
			System.out.print("Enter a user ID: ");
			user_id_inString = in.readLine();
			System.out.print("Enter your full name: ");
			fullname = in.readLine();
			System.out.print("Enter new password: ");
			user_password = in.readLine();
			
			user_id = Integer.parseInt(user_id_inString);

			String sql_stmt_2 = String.format("INSERT INTO DBUsers (userID, fullname, username, email, user_password) VALUES ('%d','%s', '%s', '%s', '%s');", user_id, fullname, username, email, user_password);
			esql.executeUpdate(sql_stmt_2);
			String sql_stmt_3 = String.format("INSERT INTO UserProfile (profile_id, username_id, num_posts, followers, followings, follow_status) VALUES ('%d','%s', '%d', '%d', '%d', '%s');", user_id, username, 0, 0, 0, "TRUE");
			esql.executeUpdate(sql_stmt_3);

			System.out.println("Successfully added new user!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void AddPost(DBproject esql) {//2
		// Given a post_id, username, date_posted, tags, and photo_url, add a post in the DB
		int default_num = 0;
		try{
			String query = "INSERT INTO Post(post_id, username_id, likes, date_posted, num_comments, tags, photo_url) VALUES (";
			String input = "";
	 
			System.out.print("\tEnter Post_ID: ");
			input = in.readLine();
			query += "'" + input + "', ";
			System.out.print("\tEnter Username: ");
			input = in.readLine();
			query += "'" + input + "', ";
			/* SET DEFAULT LIKES TO ZERO */
			query += "'" + default_num + "', ";			
			System.out.print("\tEnter Date Posted (Ex: MM/DD/YYYY): ");
			input = in.readLine();
			query += "'" + input + "', ";
			/* SET DEFAULT COMMENTS TO ZERO */
			query += "'" + default_num + "', ";
			System.out.print("\tEnter One Tag: ");
			input = in.readLine();
			query += "'" + input + "', ";
			System.out.print("\tEnter Photo_URL: ");
			input = in.readLine();
			query += "'" + input + "');";

	 
			esql.executeUpdate(query);
		 }catch(Exception e){
			System.err.println (e.getMessage());
		 }
	}

	public static void ViewAllPosts(DBproject esql) {//3
		// View all the posts in the DB

		try{
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

			System.out.print("Enter the user you want to follow: ");
			user_follower = in.readLine();
			System.out.print("Enter your username: ");
			user_being_followed = in.readLine();

			String sql_stmt = String.format("INSERT INTO UserFollowing (username_id, follower) VALUES ('%s', '%s');", user_follower, user_being_followed);
			esql.executeUpdate(sql_stmt);

			System.out.println("Successfully added new follower!\n");
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

			System.out.println("Here are the usernames that correspond to this tag");
			esql.executeQueryAndPrintResult(String.format("SELECT username_id FROM Post WHERE tags = '%s';", tag));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void ViewPhotosByTag(DBproject esql) throws IOException, SQLException {// 7
		// User enters a tag to search and database replies with photos containing tag

		//Print message to get input from user and input fullname
		System.out.print("\nWhich hashtag would you like to see photos for? : ");
		String hashtag = in.readLine();

		//Executes quesry and prints the result
		esql.executeQueryAndPrintResult("select photo_url from post where tags = '#" + hashtag + "'");
		
	}

	public static void ViewPhotosOfUser(DBproject esql) throws IOException, SQLException {// 8
		// Enter username or user full name and get photos of them

		//Print menu so user can search for a User bases on their full name or their username
		System.out.println("\n");
		System.out.println("1: Search by Full Name");
		System.out.println("2: Search by username");
		System.out.println("----------------------");
		 

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
	}
	
	/*public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
	}*/

	public static void PopularUsers(DBproject esql) {//13 
		try {
			System.out.println("Here are the popular users: \n");
			esql.executeQueryAndPrintResult(String.format("SELECT username_id, COUNT(*) AS follower FROM UserFollowing GROUP BY username_id ORDER BY follower DESC;"));
			System.out.print("\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}
}
