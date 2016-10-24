package Client;

import javax.swing.JFrame;

public class ClientApp {
	
	public static void main(String[] args) {
		//AdditionView stef;
		//stef = new AdditionView("127.0.0.1");
		//stef.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//stef.startRunning();	
		
		Main stef;
		stef = new Main("127.0.0.1");
		stef.startRunning();
	}
}
