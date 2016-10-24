package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import Model.Cinema;
import Model.Movie;
import Model.ObjectType;
import Model.Query;
import Model.QueryType;
import Model.Responce;
import Model.Session;

public class Client extends JFrame {
	//Variables
	private JTextArea infoPanel;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String serverIP;
	private Socket connectionSocket;
	
	private Query outcomeQuery;
	private boolean isActive;
	private Responce incomingResponce;
	
	//DataSource
	private List<Cinema> cinemas;
	private List<Movie> movies;
	private List<Session> sessions;
	
	//UI elements 
	JFrame frame;
	
	//constructor
	public Client(String host) {
		super("Client Andy");
		serverIP = host;
		infoPanel = new JTextArea();
		infoPanel.setBounds(300,300,300,300);
		add(new JScrollPane(infoPanel), BorderLayout.PAGE_END);
		setSize(300, 300);
		setVisible(true);
	}
	
	//connect to server
		public void startRunning() {
			try {
				connectToServer();
				setupStreams();
				whileActive();
			}catch(EOFException eofexception) {
				showMessage("\n Client terminated the connection");
			} catch(IOException ioexception) {
				ioexception.printStackTrace();
			}finally {
				closeAll();
			}
		}
	
		private void connectToServer() throws IOException {
			showMessage("Attempt to connect.. .. \n");
			connectionSocket = new Socket(InetAddress.getByName(serverIP), 3111);
			showMessage("Connected to " + connectionSocket.getInetAddress().getHostAddress());
		}
		
		//get stream to send and receive data
		private void setupStreams() throws IOException{
			output = new ObjectOutputStream(connectionSocket.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connectionSocket.getInputStream());
			showMessage("\n You're ready to go \n");
			isActive = true;
		}
		
		private void whileActive() throws IOException {			
			do {
				try {
					incomingResponce = (Responce ) input.readObject();
					handleResponce(incomingResponce);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} while (true);
		}
		
		//what i have to do with responce
		private void handleResponce(Responce responce) {
			showMessage(responce.statusString);
			List<Cinema> myList = responce.cinemaArray;
			for (Cinema cinema : myList) {
				showMessage(Integer.toString(cinema.hallNumber));
			}
			
			switch (responce.responceType) {
			case status:
				showMessage(responce.statusString);
				break;
			case sendArray:
				switch (responce.objectType) {
				case cinema:
					cinemas = new ArrayList();
					cinemas = responce.cinemaArray;
					for (Cinema cinema : cinemas) {
						System.out.print(cinema.objectId + "\t" + cinema.name + "\t" + cinema.address + "\t" + Integer.toString(cinema.hallNumber) + "\n");
					}
					break;
				case movie:
					movies = new ArrayList();
					movies = responce.movieArray;
					break;
				case session:
					sessions = new ArrayList();
					sessions = responce.sessionArray;
					break;	
				}
			}
		}
		
		private void sendRequest(Query query) {
			try {
				output.writeObject(query);
				//output.flush();
				showMessage("Query - succeded");
			} catch (IOException ioexception) {
				showMessage("Query failed");
			}
		}
		
		//close all stuff
		private void closeAll() {
			showMessage("\n Closing all \n");
			try {
				output.close();
				input.close();
				connectionSocket.close();
				isActive = false;
			} catch(IOException ioexception) {
				ioexception.printStackTrace();
			}
		}
				
		//display messages that brings some info
		private void showMessage(final String text) {
			SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								infoPanel.append(text);
							}
						}
					);
		}
}
