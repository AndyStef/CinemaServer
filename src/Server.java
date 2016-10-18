import java.awt.List;
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

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import Model.Cinema;

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
			getCinemas();
			
			while(true) {
				try {
					waitForConnection();
					setupStreams();
				} catch (EOFException eofexception) {
					showMessage("\n Server ended the connection");
				} finally {
					closeAll();
				}
			}
		} catch(IOException ioException ) {
			ioException.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
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
	      System.out.println("Connecting to database...");
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
		showMessage("Waiting for someone to connect");
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
		
	//closing all streams and sockets as well
	private void closeAll() {
		showMessage(" \n Closing everything...");
		try {
			output.close();
			input.close();
			connectionSocket.close();
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
	private void getCinemas() throws SQLException {
		System.out.println("Creating statement...");
	      stmt = databaseConnection.createStatement();
	      String sql;
	      sql = "SELECT * FROM Cinema";
	      ResultSet resultSet = stmt.executeQuery(sql);

	      //STEP 5: Extract data from result set
	      while(resultSet.next()){
	         //Retrieve by column name
	         int id  = resultSet.getInt("CinemaId");
	         String name = resultSet.getString("Name");
	         String address = resultSet.getString("Address");
	         int hallNumber = resultSet.getInt("HallNumber");
	         
	         //Display values
	         System.out.print("ID: " + id);
	         System.out.print(", Name: " + name);
	         System.out.print(", Address: " + address);
	         System.out.print(", HallNumber: " + hallNumber + "\n");
	         
	      }
	}
}
