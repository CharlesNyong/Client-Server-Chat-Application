package Game.org;
import javax.swing.JFrame;

public class ServerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server testServer = new Server();
		testServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testServer.startRunning(); // wait for connection and then start chatting

	}

}
