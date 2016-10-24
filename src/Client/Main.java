package Client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Model.Cinema;
import Model.Movie;
import Model.ObjectType;
import Model.Query;
import Model.QueryType;
import Model.Responce;
import Model.Session;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTextField cinemaIdTextField;
	private JTextField cinemaNameTextField;
	private JTextField cinemaAddressTextField;
	private JTextField cinemaHallsTextField;
	private JTextField textField_4;
	private JTable table_1;
	private JTable table_2;
	private JTextField movieNumberTextField;
	private JTextField movieNameTextField;
	private JTextField movieGenreTextField;
	private JTextField movieDurationTextField;
	private JTextField movieProducerTextField;
	private JTextField textField_10;
	private JTextField sessionIdTextField;
	private JTextField sessionCostTextField;
	private JTextField sessionFormatTextField;
	private JTextField sessionMovieIdTextField;
	private JTextField sessionCinemaIdTextField;
	private JTextField sessionTimeTextField;
	private JTextField textField_17;
	
	
	//Variables 
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
	DefaultTableModel model;
	DefaultTableModel modelMovie;
	DefaultTableModel sessionMovie;

	/**
	 * Launch the application.
	 */


	/**
	 * Create the frame.
	 */
	public Main(String host) {
		serverIP = host;
		setTitle("Cinema catalogue");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 843, 606);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 817, 548);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Кінотеатри", null, panel, null);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 484, 490);
		panel.add(scrollPane);
		
		
		 model = new DefaultTableModel() { 
	            String[] cinemaTable = {"Номер", "Назва", "Адреса", "Зали"}; 

	            @Override 
	            public int getColumnCount() { 
	                return cinemaTable.length; 
	            } 

	            @Override 
	            public String getColumnName(int index) { 
	                return cinemaTable[index]; 
	            } 
	        }; 
	        
		table = new JTable(model);
		scrollPane.setViewportView(table);
		
		JLabel label = new JLabel("");
		Image img = new ImageIcon(this.getClass().getResource("/cinema.png")).getImage();
		label.setIcon(new ImageIcon(img));
		label.setBounds(595, 6, 122, 134);
		panel.add(label);
		
		JLabel lblId = new JLabel("Номер");
		lblId.setBounds(502, 152, 61, 16);
		panel.add(lblId);
		
		JLabel lblName = new JLabel("Ім'я");
		lblName.setBounds(502, 180, 61, 16);
		panel.add(lblName);
		
		JLabel lblAddress = new JLabel("Адреса");
		lblAddress.setBounds(502, 208, 61, 16);
		panel.add(lblAddress);
		
		JLabel lblHallNumber = new JLabel("Кількість залів");
		lblHallNumber.setBounds(502, 236, 94, 16);
		panel.add(lblHallNumber);
		
		cinemaIdTextField = new JTextField();
		cinemaIdTextField.setBounds(597, 152, 120, 26);
		panel.add(cinemaIdTextField);
		cinemaIdTextField.setColumns(10);
		
		cinemaNameTextField = new JTextField();
		cinemaNameTextField.setColumns(10);
		cinemaNameTextField.setBounds(597, 175, 120, 26);
		panel.add(cinemaNameTextField);
		
		cinemaAddressTextField = new JTextField();
		cinemaAddressTextField.setColumns(10);
		cinemaAddressTextField.setBounds(597, 203, 120, 26);
		panel.add(cinemaAddressTextField);
		
		cinemaHallsTextField = new JTextField();
		cinemaHallsTextField.setColumns(10);
		cinemaHallsTextField.setBounds(597, 231, 120, 26);
		panel.add(cinemaHallsTextField);
		
		JButton addCinemaButton = new JButton("");
		addCinemaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cinemaId = cinemaIdTextField.getText();
				
				if(cinemaId == "") {
					showMessage("Введіть ,будь ласка, номер кінотеатру");
					return;
				}
				
				String cinemaName = cinemaNameTextField.getText();
				
				if (cinemaName == "") {
					showMessage("Введіть ,будь ласка, назву кінотеатру");
					return;
				}
				
				String cinemaAddress = cinemaAddressTextField.getText();
				
				if (cinemaAddress == "") {
					showMessage("Введіть ,будь ласка, адресу кінотеатру");
					return;
				}
				String cinemaHalls = cinemaHallsTextField.getText();
				
				if (cinemaHalls == "") {
					showMessage("Введіть ,будь ласка, кількість залів у кінотеатрі");
					return;
				}
				
				Cinema cinema = new Cinema(Integer.parseInt(cinemaId), cinemaName, cinemaAddress, Integer.parseInt(cinemaHalls));
				Query query = new Query(QueryType.add, ObjectType.cinema);
				query.cinema = cinema;
				sendRequest(query);
				
			}
		});
		Image img2 = new ImageIcon(this.getClass().getResource("/movieAdd.png")).getImage();
		addCinemaButton.setIcon(new ImageIcon(img2));
		addCinemaButton.setBounds(731, 195, 35, 29);
		panel.add(addCinemaButton);
		
		JButton refreshCinemaButton = new JButton("Оновити список");
		refreshCinemaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				 
				Query query = new Query(QueryType.find, ObjectType.cinema);
				sendRequest(query);
			}
		});
		refreshCinemaButton.setBounds(502, 276, 233, 29);
		panel.add(refreshCinemaButton);
		
		JButton deleteCinemaButton = new JButton("Видалити обраний кінотеатр");
		deleteCinemaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int index = table.getSelectedRow();
				Cinema toDelete = cinemas.get(index);
				Query query = new Query(QueryType.delete, ObjectType.cinema);
				query.cinema = toDelete;
				sendRequest(query);
			}
		});
		deleteCinemaButton.setBounds(502, 317, 233, 29);
		panel.add(deleteCinemaButton);
		
		JButton button_2 = new JButton("Фільтрувати");
		button_2.setBounds(502, 356, 233, 29);
		panel.add(button_2);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Кількість залів"}));
		comboBox.setBounds(502, 392, 233, 27);
		panel.add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {">", "=", "<"}));
		comboBox_1.setBounds(502, 431, 61, 27);
		panel.add(comboBox_1);
		
		textField_4 = new JTextField();
		textField_4.setBounds(572, 430, 163, 26);
		panel.add(textField_4);
		textField_4.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Кіна", null, panel_1, null);
		panel_1.setLayout(null);
		
		
		
		 modelMovie = new DefaultTableModel() { 
            String[] movieTable = {"Номер", "Назва", "Жанр", "Тривалість", "Продюсер","Зараз показують"}; 

            @Override 
            public int getColumnCount() { 
                return movieTable.length; 
            } 

            @Override 
            public String getColumnName(int index) { 
                return movieTable[index]; 
            } 
        };
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 6, 497, 490);
		panel_1.add(scrollPane_1);
		
		
		
		table_1 = new JTable(modelMovie);
		scrollPane_1.setViewportView(table_1);
		
		JLabel label_1 = new JLabel("");
		Image img3 = new ImageIcon(this.getClass().getResource("/video.png")).getImage();
		label_1.setIcon(new ImageIcon(img3));
		
		
		
		label_1.setBounds(595, 6, 128, 128);
		panel_1.add(label_1);
		
		JLabel label_2 = new JLabel("Номер");
		label_2.setBounds(515, 157, 61, 16);
		panel_1.add(label_2);
		
		JLabel lblNewLabel_1 = new JLabel("Назва");
		lblNewLabel_1.setBounds(515, 187, 61, 16);
		panel_1.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Жанр");
		lblNewLabel_2.setBounds(515, 215, 61, 16);
		panel_1.add(lblNewLabel_2);
		
		JLabel label_3 = new JLabel("Тривалість");
		label_3.setBounds(515, 243, 70, 16);
		panel_1.add(label_3);
		
		JLabel label_4 = new JLabel("Продюсер");
		label_4.setBounds(515, 271, 70, 16);
		panel_1.add(label_4);
		
		JLabel label_5 = new JLabel("Зараз у показі");
		label_5.setBounds(515, 299, 90, 16);
		panel_1.add(label_5);
		
		movieNumberTextField = new JTextField();
		movieNumberTextField.setBounds(601, 152, 122, 26);
		panel_1.add(movieNumberTextField);
		movieNumberTextField.setColumns(10);
		
		movieNameTextField = new JTextField();
		movieNameTextField.setColumns(10);
		movieNameTextField.setBounds(601, 182, 122, 26);
		panel_1.add(movieNameTextField);
		
		movieGenreTextField = new JTextField();
		movieGenreTextField.setColumns(10);
		movieGenreTextField.setBounds(601, 210, 122, 26);
		panel_1.add(movieGenreTextField);
		
		movieDurationTextField = new JTextField();
		movieDurationTextField.setColumns(10);
		movieDurationTextField.setBounds(601, 238, 122, 26);
		panel_1.add(movieDurationTextField);
		
		movieProducerTextField = new JTextField();
		movieProducerTextField.setColumns(10);
		movieProducerTextField.setBounds(601, 266, 122, 26);
		panel_1.add(movieProducerTextField);
		
		JCheckBox movieActualityCheckbox = new JCheckBox("");
		movieActualityCheckbox.setBounds(636, 299, 70, 23);
		panel_1.add(movieActualityCheckbox);
		
		JButton addMovieButton = new JButton("");
		addMovieButton.setIcon(new ImageIcon(img2));
		
		
		addMovieButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String movieId = movieNumberTextField.getText();
					
				String movieName = movieNameTextField.getText();
				
				String movieGenre = movieGenreTextField.getText();
				
				String movieDuration = movieDurationTextField.getText();
				
				String movieProducer = movieProducerTextField.getText();
				
				boolean movieActuality = movieActualityCheckbox.isSelected();
	
				Movie movie = new Movie(Integer.parseInt(movieId), movieName, movieGenre, Integer.parseInt(movieDuration), movieProducer, movieActuality);
				
				Query query = new Query(QueryType.add, ObjectType.movie);
				query.movie = movie;
				sendRequest(query);
				
			}
		});
		addMovieButton.setBounds(725, 215, 35, 29);
		panel_1.add(addMovieButton);
		
		JButton refreshMovieButton = new JButton("Оновити список");
		refreshMovieButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Query query = new Query(QueryType.find, ObjectType.movie);
				sendRequest(query);
			}
		});
		refreshMovieButton.setBounds(515, 327, 233, 29);
		panel_1.add(refreshMovieButton);
		
		JButton deleteMovie = new JButton("Видалити обраний фільм");
		deleteMovie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				int index = table_1.getSelectedRow();
				Movie toDelete = movies.get(index);
				Query query = new Query(QueryType.delete, ObjectType.movie);
				query.movie = toDelete;
				sendRequest(query);
		
			}
		});
		deleteMovie.setBounds(515, 368, 233, 29);
		panel_1.add(deleteMovie);
		
		JButton button_6 = new JButton("Фільтрувати");
		button_6.setBounds(515, 407, 233, 29);
		panel_1.add(button_6);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Жанр", "Тривалість"}));
		comboBox_2.setBounds(515, 439, 233, 27);
		panel_1.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"=", ">", "<"}));
		comboBox_3.setBounds(515, 469, 61, 27);
		panel_1.add(comboBox_3);
		
		textField_10 = new JTextField();
		textField_10.setBounds(588, 468, 160, 26);
		panel_1.add(textField_10);
		textField_10.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Сеанси", null, panel_2, null);
		panel_2.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(6, 6, 520, 490);
		panel_2.add(scrollPane_2);
		
		
		 sessionMovie = new DefaultTableModel() { 
            String[] sessionTable = {"Номер", "Ціна", "Формат", "Номер кінотеатру", "Час показу","Номер кіна"}; 

            @Override 
            public int getColumnCount() { 
                return sessionTable.length; 
            } 

            @Override 
            public String getColumnName(int index) { 
                return sessionTable[index]; 
            } 
        };
		
		
		
		table_2 = new JTable(sessionMovie);
		scrollPane_2.setViewportView(table_2);
		
		JLabel lblNewLabel = new JLabel("");
		Image img4 = new ImageIcon(this.getClass().getResource("/ticket.png")).getImage();
		lblNewLabel.setIcon(new ImageIcon(img4));
		lblNewLabel.setBounds(603, 6, 128, 128);
		panel_2.add(lblNewLabel);
		
		JLabel label_6 = new JLabel("Номер");
		label_6.setBounds(538, 146, 61, 16);
		panel_2.add(label_6);
		
		JLabel label_7 = new JLabel("Ціна");
		label_7.setBounds(538, 174, 61, 16);
		panel_2.add(label_7);
		
		JLabel label_8 = new JLabel("Формат");
		label_8.setBounds(538, 202, 61, 16);
		panel_2.add(label_8);
		
		JLabel label_9 = new JLabel("Номер кіна");
		label_9.setBounds(538, 230, 72, 16);
		panel_2.add(label_9);
		
		JLabel label_10 = new JLabel("Номер театру");
		label_10.setBounds(538, 258, 87, 16);
		panel_2.add(label_10);
		
		JLabel label_11 = new JLabel("Час");
		label_11.setBounds(538, 286, 61, 16);
		panel_2.add(label_11);
		
		sessionIdTextField = new JTextField();
		sessionIdTextField.setBounds(630, 146, 130, 26);
		panel_2.add(sessionIdTextField);
		sessionIdTextField.setColumns(10);
		
		sessionCostTextField = new JTextField();
		sessionCostTextField.setColumns(10);
		sessionCostTextField.setBounds(630, 174, 130, 26);
		panel_2.add(sessionCostTextField);
		
		sessionFormatTextField = new JTextField();
		sessionFormatTextField.setColumns(10);
		sessionFormatTextField.setBounds(630, 202, 130, 26);
		panel_2.add(sessionFormatTextField);
		
		sessionMovieIdTextField = new JTextField();
		sessionMovieIdTextField.setColumns(10);
		sessionMovieIdTextField.setBounds(630, 230, 130, 26);
		panel_2.add(sessionMovieIdTextField);
		
		sessionCinemaIdTextField = new JTextField();
		sessionCinemaIdTextField.setColumns(10);
		sessionCinemaIdTextField.setBounds(630, 253, 130, 26);
		panel_2.add(sessionCinemaIdTextField);
		
		sessionTimeTextField = new JTextField();
		sessionTimeTextField.setColumns(10);
		sessionTimeTextField.setBounds(630, 281, 130, 26);
		panel_2.add(sessionTimeTextField);
		
		JButton refreshSessionButton = new JButton("Оновити список");
		refreshSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Query query = new Query(QueryType.find, ObjectType.session);
				sendRequest(query);
			}
		});
		refreshSessionButton.setBounds(527, 316, 233, 29);
		panel_2.add(refreshSessionButton);
		
		JButton deleteSessionButton = new JButton("Видалити обраний сеанс");
		deleteSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int index = table_2.getSelectedRow();
				Session toDelete = sessions.get(index);
				Query query = new Query(QueryType.delete, ObjectType.session);
				query.session = toDelete;
				sendRequest(query);
				
			}
		});
		deleteSessionButton.setBounds(527, 357, 233, 29);
		panel_2.add(deleteSessionButton);
		
		JButton button_9 = new JButton("Фільтрувати");
		button_9.setBounds(527, 396, 233, 29);
		panel_2.add(button_9);
		
		JButton addSessionButton = new JButton("");
		addSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				String sessionId = sessionIdTextField.getText();
				
				String sessionCost = sessionCostTextField.getText();
				
				String sessionFormat = sessionFormatTextField.getText();
				
				String sessionMovieId = sessionMovieIdTextField.getText();
				
				String sessionCinemaId = sessionCinemaIdTextField.getText();
				
				String sessionTime = sessionTimeTextField.getText();
	
				DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
				Session session = null;
				try {
					session = new Session(Integer.parseInt(sessionId), Integer.parseInt(sessionCost), sessionFormat, Integer.parseInt(sessionMovieId), Integer.parseInt(sessionCinemaId), (java.sql.Date) format.parse(sessionTime));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				
				Query query = new Query(QueryType.add, ObjectType.session);
				query.session = session;
				sendRequest(query);
					
			}
		});
		addSessionButton.setIcon(new ImageIcon(img2));
		addSessionButton.setBounds(755, 217, 35, 29);
		panel_2.add(addSessionButton);
		
		JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setModel(new DefaultComboBoxModel(new String[] {"Ціна"}));
		comboBox_4.setBounds(538, 431, 222, 27);
		panel_2.add(comboBox_4);
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setModel(new DefaultComboBoxModel(new String[] {">", "=", "<"}));
		comboBox_5.setBounds(538, 469, 61, 27);
		panel_2.add(comboBox_5);
		
		textField_17 = new JTextField();
		textField_17.setBounds(601, 470, 159, 26);
		panel_2.add(textField_17);
		textField_17.setColumns(10);
		
		setVisible(true);
	}
	
	
	//connect to server
			public void startRunning() {
				try {
					connectToServer();
					setupStreams();
					whileActive();
				}catch(EOFException eofexception) {
					showMessage("\n Server terminated the connection");
				} catch(IOException ioexception) {
					ioexception.printStackTrace();
				}finally {
					closeAll();
				}
			}
		
			private void connectToServer() throws IOException {
				//showMessage("Attempt to connect.. .. \n");
				connectionSocket = new Socket(InetAddress.getByName(serverIP), 3111);
				//showMessage("Connected to " + connectionSocket.getInetAddress().getHostAddress());
			}
			
			//get stream to send and receive data
			private void setupStreams() throws IOException{
				output = new ObjectOutputStream(connectionSocket.getOutputStream());
				//output.flush();
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
							Object[] data = {cinema.objectId, cinema.name, cinema.address, cinema.hallNumber};
							model.addRow(data);
						}
		
						break;
					case movie:
						movies = new ArrayList();
						movies = responce.movieArray;
						for (Movie movie: movies ){
							Object[] data = {movie.objectId, movie.name, movie.genre, movie.duration, movie.producer, movie.isCurrentlyShown};
							modelMovie.addRow(data);
						}
						
						break;
					case session:
						sessions = new ArrayList();
						sessions = responce.sessionArray;
						for(Session session: sessions) {
							Object[] data = {session.objectId, session.cost, session.format, session.cinemaId, session.date, session.movieId};
							sessionMovie.addRow(data);
						}
						break;	
					}
				}
			}
	
	private void sendRequest(Query query) {
		try {
			output.writeObject(query);
			//output.flush();
			//showMessage("Query - succeded");
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
				} catch(IOException ioexception) {
					ioexception.printStackTrace();
				}
			}
	
	//display messages that brings some info
			private void showMessage(final String text) {
				SwingUtilities.invokeLater(
							new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(null, text);
								}
							}
						);
			}
	
}
