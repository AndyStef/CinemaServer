package Client;

import javax.swing.JFrame;

public class ClientApp {
	
	public static void main(String[] args) {
		Client stef;
		stef = new Client("127.0.0.1");
		stef.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		stef.startRunning();
	}
}
