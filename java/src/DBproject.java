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
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add User");
				System.out.println("4. Follow a user");
				System.out.println("5. Search user by username"); //view profile of user 
				System.out.println("6. Search user by tags"); 
				System.out.println("13. List out most popular users of the database"); 
				System.out.println("14. < EXIT");
				
				switch (readChoice()){
					case 1: AddUser(esql); break; //sandy
					case 4: FollowUser(esql); break; // sandy
					case 5: SearchProfileBasedOnUser(esql); break; // sandy
					case 6: SearchProfileBasedOnTags(esql); break; // sandy
					case 13: PopularUsers(esql); break; // sandy
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
			int username_id;
			String username_id_inString;
			String fullname;
			String username;
			String email;
			String user_password;

			System.out.print("Enter email address: ");
			email = in.readLine();
			System.out.print("Enter a username: ");
			username = in.readLine();
			System.out.print("Enter your full name: ");
			fullname = in.readLine();
			System.out.print("Enter new password: ");
			user_password = toHexString(getSHA(in.readLine()));
			
			String sql_stmt = String.format("SELECT MAX(userID) FROM User;");
			username_id_inString = esql.executeQueryAndReturnResult(sql_stmt);
			username_id = Integer.parseInt(username_id_inString) + 1;

			String sql_stmt = String.format("INSERT INTO DBUsers (userID, fullname, username, email, user_password) VALUES ('%d', '%s', '%s', '%s', '%s');", username_id, fullname, username, email, user_password);
			esql.executeUpdate(sql_stmt);

			System.out.println("Successfully added new user!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}


	public static void FollowUser(DBproject esql) {//4 sandy
		try {
			String user_to_follow;
			String user_follower;

			System.out.print("Enter the user you want to follow");
			user_to_follow = in.readLine();
			System.out.print("Enter your username");
			user_follower = in.readLine();

			String sql_stmt = String.format("INSERT INTO UserFollowing (followed, follower) VALUES ('%s', '%s');", user_to_follow, user_follower);
			esql.executeUpdate(sql_stmt);

			System.out.println("Successfully added new follower!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void SearchProfileBasedOnUser(DBproject esql) {//5 sandy
		try {
			String user;

			System.out.print("Enter the username of the profile you want to see");
			user = in.readLine();
			esql.executeQueryAndPrintResult(String.format("SELECT * FROM UserProfile WHERE username = '%s';", user));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void SearchUserBasedOnTags(DBproject esql) {//6 sandy
		try {
			String tag;

			System.out.print("Enter the tag you want to search for");
			tag = in.readLine();

			System.out.println("Here are the usernames that correspond to this tag");
			esql.executeQueryAndPrintResult(String.format("SELECT username FROM Posts WHERE tags = '%s';", tag));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}

	public static void PopularUsers(DBproject esql) {//13 sandy
		try {
			System.out.println("Here are the popular users");
			esql.executeQueryAndPrintResult(String.format("SELECT followed, COUNT(*) AS cnt FROM UserFollowing GROUP BY followed;"));
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}
}