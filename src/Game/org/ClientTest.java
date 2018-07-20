package Game.org;
import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client testClient;
		/* The IP address here indicates that the server is local,
		 * That is the machine with the server app is the same as the one with the client app 
		 * */ 
		testClient = new Client("127.0.0.1"); // sends connection request to server machine with this IP
		testClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testClient.startRunning();
	}

}
