import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import Model.Cinema;
import Model.ComparisonType;
import Model.Movie;
import Model.ObjectType;
import Model.Query;
import Model.QueryType;
import Model.Responce;
import Model.ResponceType;
import Model.Session;

public class Server extends JFrame {
	
	//variables 
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private JTextArea infoPanel;
	
	//DataBase credentials
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/CinemaNetwork";
	static final String USER = "root";
	static final String PASS = "";
	Connection databaseConnection = null;
	Statement stmt = null;
	
	private Query incomingQuery = null;
	private Responce outcomeResponce;
	
	//constructor
	public Server() {
		super("Server");	
		infoPanel = new JTextArea();
		add(new JScrollPane(infoPanel));
		setSize(300, 150);
		setVisible(true);
	}
	
	//setup server and start running it
	public void startRunning() {
		try {
			serverSocket = new ServerSocket(3111);
			connectToDatabase();
			//getMovies();

			while(true) {
				try {
					waitForConnection();
					setupStreams();					
					waitForQuery();
				} catch (EOFException eofexception) {
					showMessage("\n Server ended the connection");
				} finally {
					closeAll();
				}
			}
		} catch(IOException ioException ) {
			ioException.printStackTrace();
		} 
	}
	
	//DataBase connection
	private void connectToDatabase() {
		// Register JDBC driver
	    try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	      // Open a connection
	      System.out.println("Connecting to database...\n");
	      try {
	    	  databaseConnection = DriverManager.getConnection(DB_URL,USER,PASS);
	    	  showMessage("Connected to Database\n");
		} catch (SQLException e) {
			e.printStackTrace();
			showMessage("Not connected to Database\n");
		}
	}
	
	//Disconnect from DB
	private void disconnectFromDatabase() throws SQLException {
		databaseConnection.close();
		stmt.close();
	}
	
	//wait for connection 
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect\n");
		connectionSocket = serverSocket.accept();
		showMessage("Now connected to " + connectionSocket.getInetAddress().getHostAddress());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
			output = new ObjectOutputStream(connectionSocket.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connectionSocket.getInputStream());
			showMessage("\n Streams are setup \n");
		}
		
	//wait for incoming query and execute it 
	private void waitForQuery() {		
		do {
			try {
				incomingQuery = (Query )input.readObject();
				showMessage(incomingQuery);
				if (incomingQuery != null) {
					executeQuery(incomingQuery);
				}
					
			} catch (ClassNotFoundException | IOException | SQLException e) {
				//e.printStackTrace();
			}  
			
		} while(true);
	}
	
	private void showMessage(Query incomingQuery) {
		QueryType type = incomingQuery.type;
		ObjectType objectType = incomingQuery.objectType;
		
		showMessage(objectType.toString() + " " + type.toString());
	}

	private void executeQuery(Query query) throws SQLException, IOException {
		switch(query.type) {
		case delete: {
			switch(query.objectType) {
			case cinema:
				deleteCinema(query.cinema);
				break;
			case movie:
				deleteMovie(query.movie);
				break;
			case session:
				deleteSession(query.session);
				break;	
			}
			
			break;
		} 
		
		case add: {
			switch(query.objectType) {
			case cinema:
				addCinema(query.cinema);
				break;
			case movie:
				addMovie(query.movie);
				break;
			case session:
				addSession(query.session);
				break;	
			}
			
			break;
		}
		
		case find: {
			switch(query.objectType) {
			case cinema:
				getCinemas();
				break;
			case movie:
				getMovies();
				break;
			case session:
				getSessions();
				break;	
			}
			
			break;
		}
		
		case findWithFilter: {
			switch(query.objectType) {
			case cinema:
				getCinemasWithFilter(query.key, query.comparisonValue, query.comparisonType);
				break;
			case movie:
				getMoviesWithFilter(query.key, query.comparisonValue, query.comparisonType);
				break;
			case session:
				getSessionsWithFilter(query.key, query.comparisonValue, query.comparisonType);
				break;	
			}
			
			break;
			}
		}
	}
	
	//closing all streams and sockets as well
	private void closeAll() {
		showMessage(" \n Closing everything...");
		try {
			output.close();
			input.close();
			connectionSocket.close();
			//serverSocket.close();
			disconnectFromDatabase();
		} catch(IOException ioexception) {
			ioexception.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//show some info 
	private void showMessage(String message) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						infoPanel.append(message);
					}
				}
			);
	}
	
	//SQL Methods(QUERIES)
	private void getCinemas() throws SQLException, IOException {
		  List<Cinema> cinemaList = new ArrayList<>();
		
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Cinema";
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("CinemaId");
	         String name = resultSet.getString("Name");
	         String address = resultSet.getString("Address");
	         int hallNumber = resultSet.getInt("HallNumber");

	         cinemaList.add(new Cinema(id, name, address, hallNumber));
	      }
	      resultSet.close();
	      output.flush();
	      
	      Responce responce = new Responce(ResponceType.sendArray, ObjectType.cinema);
		  responce.statusString = "Cinemas was successful retrieved";
		  responce.cinemaArray = cinemaList;
		  output.writeObject(responce);
	}
	
	private void getMovies() throws SQLException, IOException {
		  List<Movie> movieList = new ArrayList<Movie>();
		
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Movie";
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("MovieId");
	         String name = resultSet.getString("Name");
	         String genre = resultSet.getString("Genre");
	         String producer = resultSet.getString("Producer");
	         int duration = resultSet.getInt("Duration");
	         boolean isCurrentlyShowed = resultSet.getBoolean("IsCurrenlyShowed");
	
	         movieList.add(new Movie(id, name, genre, duration, producer, isCurrentlyShowed));
	      }
	      resultSet.close();
	      output.flush();
	      
	      Responce responce = new Responce(ResponceType.sendArray, ObjectType.movie);
		  responce.statusString = "Movies was successful retrieved";
		  responce.movieArray = movieList;
		  output.writeObject(responce);
	}
	
	private void getSessions() throws SQLException, IOException {

		  List<Session> sessionList = new ArrayList<Session>();
		
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Session";
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("SessionId");
	         int cost = resultSet.getInt("Cost");
	         String format = resultSet.getString("Format");
	         int cinemaId = resultSet.getInt("CinemaId");
	         int movieId = resultSet.getInt("MovieId");
	         Date time = resultSet.getDate("Time");

	         sessionList.add(new Session(id, cost, format, movieId, cinemaId, time));
	      }
	      resultSet.close();
	      output.flush();
	      
	      Responce responce = new Responce(ResponceType.sendArray, ObjectType.session);
		  responce.statusString = "Sessions was successful retrieved";
		  responce.sessionArray = sessionList;
		  output.writeObject(responce);
	}
	
	//Methods for addition my entities
	private void addCinema(Cinema cinema) throws SQLException, IOException {
		stmt = databaseConnection.createStatement();
	    String sql;
	    sql = "INSERT INTO Cinema(CinemaId, `Name`, Address, HallNumber) VALUES (";
	    sql += cinema.objectId + ",'";
	    sql += cinema.name + "','";
	    sql += cinema.address + "',";
	    sql += cinema.hallNumber + ");";
	    
	    stmt.execute(sql);
	    output.flush();
	    
	    Responce responce = new Responce(ResponceType.status, ObjectType.cinema);
	    responce.statusString = "Cinema addition was successful";
	    output.writeObject(responce);
	}
	
	private void addMovie(Movie movie) throws SQLException, IOException {
		stmt = databaseConnection.createStatement();
	    String sql;
	    sql = "INSERT INTO movie (MovieId, `Name`, Genre, Duration, Producer, IsCurrenlyShowed) VALUES (";
	    sql += movie.objectId + ",'";
	    sql += movie.name + "','";
	    sql += movie.genre + "',";
	    sql += movie.duration + ",'";
	    sql += movie.producer + "',";
	    sql += movie.isCurrentlyShown + ");";
	 
	    stmt.execute(sql);	 
	    output.flush();
	    
	    Responce responce = new Responce(ResponceType.status, ObjectType.movie);
	    responce.statusString = "Movie addition was successful";
	    output.writeObject(responce);
	}
	
	private void addSession(Session session) throws SQLException, IOException {
		stmt = databaseConnection.createStatement();
	    String sql;
	    sql = "INSERT INTO cinemanetwork.`session` (Cost, Format, CinemaId, SessionId, `Time`, MovieId) VALUES (";
	    sql += session.cost + ",'";
	    sql += session.format + "',";
	    sql += session.cinemaId + ",";
	    sql += session.objectId + ",'";
	    sql += session.date + "',";
	    sql += session.movieId + ");";

	    stmt.execute(sql);	
	    output.flush();
	    
	    Responce responce = new Responce(ResponceType.status, ObjectType.session);
	    responce.statusString = "Session addition was successful";
	    output.writeObject(responce);
	}
	
	//deleting objects
	private void deleteCinema(Cinema cinema) throws SQLException, IOException {
		stmt = databaseConnection.createStatement();
	    String sql;
	    sql = "DELETE FROM cinemanetwork.cinema WHERE CinemaId = ";
	    sql += cinema.objectId + ";";
	    stmt.execute(sql);	
	    output.flush();
	    
	    Responce responce = new Responce(ResponceType.status, ObjectType.cinema);
	    responce.statusString = "Cinema delition was successful";
	    output.writeObject(responce);
	}
	
	private void deleteMovie(Movie movie) throws SQLException, IOException {
		stmt = databaseConnection.createStatement();
	    String sql;
	    sql = "DELETE FROM cinemanetwork.movie WHERE MovieId = ";
	    sql += movie.objectId + ";";
	    stmt.execute(sql);	
	    output.flush();
	    
	    Responce responce = new Responce(ResponceType.status, ObjectType.movie);
	    responce.statusString = "Movie delition was successful";
	    output.writeObject(responce);
	}
	
	private void deleteSession(Session session) throws SQLException, IOException {
		stmt = databaseConnection.createStatement();
	    String sql;
	    sql = "DELETE FROM cinemanetwork.`session` WHERE SessionId = ";
	    sql += session.objectId + ";";
	    stmt.execute(sql);	
	    output.flush();
	    
	    Responce responce = new Responce(ResponceType.status, ObjectType.session);
	    responce.statusString = "Session delition was successful";
	    output.writeObject(responce);
	}
	
	//Specified selects
	private void getCinemasWithFilter(String key, String comparisonValue, ComparisonType type) throws SQLException, IOException {
		  ArrayList<Cinema> cinemaList = new ArrayList<Cinema>();
		
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Cinema ";
	      sql += Query.whereKey(key, comparisonValue, type);
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("CinemaId");
	         String name = resultSet.getString("Name");
	         String address = resultSet.getString("Address");
	         int hallNumber = resultSet.getInt("HallNumber");
	         
	         cinemaList.add(new Cinema(id, name, address, hallNumber));
	      }
	      resultSet.close();	
	      output.flush();
	      
	      Responce responce = new Responce(ResponceType.sendArray, ObjectType.cinema);
		  responce.statusString = "Cinemas was successful retrieved";
		  responce.cinemaArray = cinemaList;
		  output.writeObject(responce);
	}
	
	private void getMoviesWithFilter(String key, String comparisonValue, ComparisonType type) throws SQLException, IOException {
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Movie";
	      sql += Query.whereKey(key, comparisonValue, type);
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("MovieId");
	         String name = resultSet.getString("Name");
	         String genre = resultSet.getString("Genre");
	         String producer = resultSet.getString("Producer");
	         int duration = resultSet.getInt("Duration");
	         boolean isCurrentlyShowed = resultSet.getBoolean("IsCurrenlyShowed");

	         movieList.add(new Movie(id, name, genre, duration, producer, true));
	      }
	      resultSet.close();
	      output.flush();
	      
	      Responce responce = new Responce(ResponceType.sendArray, ObjectType.movie);
		  responce.statusString = "Movies was successful retrieved";
		  responce.movieArray = movieList;
		  output.writeObject(responce);
	}
	
	private void getSessionsWithFilter(String key, String comparisonValue, ComparisonType type) throws SQLException, IOException {
		 ArrayList<Session> sessionList = new ArrayList<Session>();
			
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Session";
	      sql += Query.whereKey(key, comparisonValue, type);
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("SessionId");
	         int cost = resultSet.getInt("Cost");
	         String format = resultSet.getString("Format");
	         int cinemaId = resultSet.getInt("CinemaId");
	         int movieId = resultSet.getInt("MovieId");
	         Date time = resultSet.getDate("Time");
	         
	         sessionList.add(new Session(id, cost, format, movieId, cinemaId, time));
	      }
	      resultSet.close();
	      output.flush();
	      
	      Responce responce = new Responce(ResponceType.sendArray, ObjectType.session);
		  responce.statusString = "Movies was successful retrieved";
		  responce.sessionArray = sessionList;
		  output.writeObject(responce);	
	}
}
