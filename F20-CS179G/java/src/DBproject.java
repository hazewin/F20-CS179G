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
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");

				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
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
					System.out.println("Done\n\nBye !");
				}//end if
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static void Greeting(){
		 System.out.println(
				"\n\n*******************************************************\n" +
				"              Airplane Client Application      	               \n" +
				"*******************************************************\n");
	}//end Greeting


	/*
	 * Reads the users choice given from the keyboard
	 * @int
	 **/


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

	public static void AddPlane(DBproject esql) {//1
		try{
       String query = "INSERT INTO Plane(id, make, model, age, seats) VALUES (";
       String input = "";

			 System.out.print("\tEnter Plane ID: ");
       input = in.readLine();
       query += "'" + input + "', ";
			 System.out.print("\tEnter Make: ");
       input = in.readLine();
       query += "'" + input + "', ";
       System.out.print("\tEnter Model: ");
       input = in.readLine();
       query += "'" + input + "', ";
       System.out.print("\tEnter age: ");
       input = in.readLine();
       query += input + ", ";
       System.out.print("\tEnter seats: ");
       input = in.readLine();
       query += input + ");";

       esql.executeUpdate(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }

	}

	public static void AddPilot(DBproject esql) {//2
		try{
       String query = "INSERT INTO Pilot(id, fullname, nationality) VALUES (";
       String input = "";

			 System.out.print("\tEnter Pilot ID: ");
       input = in.readLine();
       query += "'" + input + "', ";
			 System.out.print("\tEnter Full Name: ");
       input = in.readLine();
       query += "'" + input + "', ";
       System.out.print("\tEnter Nationality: ");
       input = in.readLine();
       query += "'" + input + "');";

       esql.executeUpdate(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB

		try{
			 String query = "INSERT INTO Flight(fnum, cost, num_sold, num_stops, arrival_aiport, depature_aiport) VALUES (";
       String input = "";

			 //System.out.print("\tEnter fiid: ");
       //input = in.readLine();
       //query += input + ", ";
			 System.out.print("\tEnter Flight Number: ");
       input = in.readLine();
       query += input + ", ";
			 System.out.print("\tEnter Flight Cost: ");
       input = in.readLine();
       query += input + ", ";
			 System.out.print("\tNumber of Seats Sold: ");
       input = in.readLine();
       query += input + ", ";
			 System.out.print("\tNumber of Stops: ");
       input = in.readLine();
       query += input + ", ";
			 System.out.print("\tEnter Destination Airport: ");
       input = in.readLine();
       query += input + ", ";
			 System.out.print("\tEnter Departure Airport: ");
       input = in.readLine();
       query += input + ");";

       esql.executeUpdate(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	public static void AddTechnician(DBproject esql) {//4
		try{
       String query = "INSERT INTO Technician(id, full_name) VALUES (";
       String input = "";

			 System.out.print("\tEnter Tech_ID: ");
       input = in.readLine();
       query += "'" + input + "', ";
			 System.out.print("\tEnter Full Name: ");
       input = in.readLine();
       query += "'" + input + "');";

       esql.executeUpdate(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	/*public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB

		try{
       String query = "INSERT INTO Reservation(rnum,cid, fid) VALUES (";
       String input = "";

			 System.out.print("\tEnter Reservation Number: ");
       input = in.readLine();
       query += input + ", ";
			 System.out.print("\tEnter Customer ID: ");
       input = in.readLine();
       query += input + ", ";
       System.out.print("\tEnter Flight Number: ");
       input = in.readLine();
       query += input + ");";

       esql.executeUpdate(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}*/

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		int ID;

		do {
			System.out.print("Input Customer ID: ");
			try {
				ID = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);

		int number;

		do {
			System.out.print("Input Flight Number: ");
			try {
				number = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);

		try {
			String query = "SELECT status\nFROM Reservation\nWHERE cid = " + ID + " AND fid = " + number + ";";

			String input;

			if(esql.executeQueryAndPrintResult(query) == 0) {
				do {
					System.out.println("Reservation does not exist. Would you like to book a reservation? (y/n)");
					try {
						input = in.readLine();
						if(input.equals("y")) {
							int reserve;

							do {
								System.out.print("Input New Reservation Number: ");
								try {
									reserve = Integer.parseInt(in.readLine());
									break;
								}catch (Exception e) {
									System.out.println("Your input is invalid!");
									continue;
								}
							}while (true);

							String status;

							do {
								System.out.print("Input New Reservation Status: ");
								try {
									status = in.readLine();
									if(!status.equals("W") && !status.equals("R") && !status.equals("C")) {
										throw new RuntimeException("Input only accepts the following inputs: W, R, C");
									}
									break;
								}catch (Exception e) {
									System.out.println(e);
									continue;
								}
							}while (true);
							try {
								query = "INSERT INTO Reservation(rnum, cid, fid, status) VALUES (" + reserve + ", " + ID + ", " + number + ", \'" + status + "\');";

								esql.executeUpdate(query);
							}catch (Exception e) {
								System.err.println (e.getMessage());
							}
						}else if(!input.equals("n")) {
							throw new RuntimeException("Your input is invalid!");
						}
						break;
					}catch (Exception e) {
						System.out.println(e);
						continue;
					}
				}while (true);
			}else {
				do{
					try{
						System.out.println("Would you like to update the reservation? (y/n)");
						input = in.readLine();
						if(input.equals("y")) {
							String status;

							do {
								System.out.print("Input Update Reservation Status: ");
								try {
									status = in.readLine();
									if(!status.equals("W") && !status.equals("R") && !status.equals("C")) {
										throw new RuntimeException("Input only accepts the following inputs: W, R, C");
									}
									break;
								}catch (Exception e) {
									System.out.println(e);
									continue;
								}
							}while (true);
							try {
								query = "UPDATE Reservation SET status = \'" + status + "\' WHERE cid = " + ID + " AND fid = " + number + ";";

								esql.executeUpdate(query);
							}catch (Exception e) {
								System.err.println (e.getMessage());
							}
						}else if(!input.equals("n")) {
							throw new RuntimeException("Your input is invalid!");
						}
						break;
					}catch (Exception e) {
						System.out.println(e);
						continue;
					}
				}while (true);
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )

		try{
       String query = "SELECT (P.seats - F.num_sold) AS available_seats FROM Plane P, Flight F, FlightInfo FI WHERE FI.flight_id=F.fnum AND FI.plane_id=P.id AND F.fnum=";

       System.out.print("\tEnter Flight Number: ");
       String input = in.readLine();
       query += input + " GROUP BY P.seats, F.num_sold";
       esql.executeQueryAndPrintResult(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order

		try{
       String query = "SELECT plane_id, COUNT(*) AS repairs FROM Repairs GROUP BY plane_id ORDER BY repairs DESC;";
       esql.executeQueryAndPrintResult(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
		try{
       String query = "SELECT EXTRACT(YEAR FROM repair_date) as year, COUNT(*) as repairs FROM Repairs GROUP BY EXTRACT(YEAR FROM repair_date) ORDER BY COUNT(*) ASC";
       esql.executeQueryAndPrintResult(query);
    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		int number;

		do {
			System.out.print("\tInput Flight Number: ");
			try {
				number = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("\tYour input is invalid!");
				continue;
			}
		}while (true);

		String status;

		do {
			System.out.print("\tInput Passenger Status: ");
			try {
				status = in.readLine();
				if(!status.equals("W") && !status.equals("R") && !status.equals("C")) {
					throw new RuntimeException("\tInput only accepts the following inputs: W, R, C");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		try {
			String query = "SELECT COUNT(*)\nFROM Reservation\nWHERE fid = " + number + " AND status = \'" + status + "\';";

			esql.executeQueryAndPrintResult(query);
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}
	}

	// Additional Functionalities Here ~

}
