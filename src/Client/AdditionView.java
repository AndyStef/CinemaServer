package Client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Model.Cinema;
import Model.Movie;
import Model.ObjectType;
import Model.Query;
import Model.QueryType;
import Model.Responce;
import Model.Session;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class AdditionView extends JFrame {

	private JPanel contentPane;
	private JTextArea InfoPanel;
	
	
	
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

	/**
	 * Launch the application.
	 */
	
	
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

	/**
	 * Create the frame.
	 */
	public AdditionView(String host) {
		serverIP = host;
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 659, 435);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setVisible(true);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 338, 647, 69);
		contentPane.add(scrollPane);
		
		InfoPanel = new JTextArea();
		scrollPane.setViewportView(InfoPanel);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(638, 338, 15, 69);
		contentPane.add(scrollBar);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 647, 323);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Cinema", null, panel, null);
		panel.setLayout(null);
		
		JButton btnPlus = new JButton("Plus");
		btnPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Query query = new Query(QueryType.add, ObjectType.cinema);
				Cinema cinema = new Cinema(7, "Te5st22", "h2melnytsk2ogo", 10);
				query.cinema = cinema;
				sendRequest(query);
				try {
					incomingResponce = (Responce ) input.readObject();
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//TODO: - implement callback for responce
				handleResponce(incomingResponce);
			}
		});
		btnPlus.setBounds(6, 16, 117, 29);
		panel.add(btnPlus);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Query query = new Query(QueryType.find, ObjectType.cinema);
				sendRequest(query);
				try {
					incomingResponce = (Responce ) input.readObject();
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				handleResponce(incomingResponce);
			}
		});
		btnNewButton.setBounds(162, 85, 117, 29);
		panel.add(btnNewButton);
		
		JButton button = new JButton("2");
		button.setBounds(172, 121, 117, 29);
		panel.add(button);
		
		JButton button_1 = new JButton("3");
		button_1.setBounds(162, 162, 117, 29);
		panel.add(button_1);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Movie", null, panel_1, null);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Session", null, panel_2, null);
		panel_2.setLayout(null);
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
				if (incomingResponce != null) {
					handleResponce(incomingResponce);
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} while (isActive);
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
				responce = null;
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
							InfoPanel.append(text);
						}
					}
				);
	}
}
