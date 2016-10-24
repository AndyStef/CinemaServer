package Client;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;

import Model.Cinema;
import Model.Movie;
import Model.Query;
import Model.Responce;
import Model.Session;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainView {

	private JFrame frmClient;
	
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
	private JTable table;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					window.frmClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {	
		serverIP = "127.0.0.1";
		frmClient = new JFrame();
		frmClient.setTitle("Planeta Kino");
		frmClient.setBounds(100, 100, 497, 440);
		frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClient.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 438, 266);
		frmClient.getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Cinema", null, panel, null);
		panel.setLayout(null);
		
		DefaultTableModel model = new DefaultTableModel();
		
		
		table = new JTable(model);
		model.addColumn("Lull");
		model.addColumn("new");
		model.addRow(new Object[] {"row1"});
		model.addRow(new Object[]{"row2"});
		table.setBounds(6, 6, 264, 208);
		panel.add(table);
		
		
		
		JButton button = new JButton("+");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("dadasd");
				//frmClient.dispose();
			//	AdditionView addition = new AdditionView();
			//	addition.setVisible(true);
			}
		});
		button.setBounds(294, 6, 117, 29);
		panel.add(button);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(294, 35, 117, 29);
		panel.add(btnDelete);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Перший ", "Другий"}));
		comboBox.setBounds(294, 95, 117, 27);
		panel.add(comboBox);
		
		JLabel lblFilter = new JLabel("Filter");
		lblFilter.setHorizontalAlignment(SwingConstants.CENTER);
		lblFilter.setBounds(321, 76, 61, 16);
		panel.add(lblFilter);
		
		JSlider slider = new JSlider();
		slider.setBounds(294, 134, 117, 29);
		panel.add(slider);
		
		JToggleButton tglbtnBb = new JToggleButton("bb");
		tglbtnBb.setBounds(282, 175, 126, 29);
		panel.add(tglbtnBb);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Movie", null, panel_1, null);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Session", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel label = new JLabel("");
		Image img = new ImageIcon(this.getClass().getResource("/add.png")).getImage();
		label.setIcon(new ImageIcon(img));
		label.setBounds(6, 305, 145, 107);
		frmClient.getContentPane().add(label);
		//startRunning();
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
						//TODO: - implement callback for responce
						handleResponce(incomingResponce);
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
								//	JOptionPane.showMessageDialog(null, text);
									//infoPanel.append(text);
								}
							}
						);
			}
}
