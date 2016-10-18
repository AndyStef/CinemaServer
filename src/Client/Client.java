package Client;

import java.awt.BorderLayout;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import Model.ObjectType;
import Model.Query;
import Model.QueryType;
import Model.Responce;

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
	
	//constructor
	public Client(String host) {
		super("Client Andy");
		serverIP = host;
		infoPanel = new JTextArea();
		add(new JScrollPane(infoPanel), BorderLayout.CENTER);
		setSize(300, 150);
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
			sendRequest(new Query(QueryType.find, ObjectType.cinema));
			
			do {
				try {
					incomingResponce = (Responce ) input.readObject();
					//TODO: - implement callback for responce
					handleResponce(incomingResponce);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} while (isActive);
		}
		
		private void handleResponce(Responce responce) {
			System.out.print("Success");
		}
		
		private void sendRequest(Query query) {
			try {
				output.writeObject(query);
				output.flush();
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
