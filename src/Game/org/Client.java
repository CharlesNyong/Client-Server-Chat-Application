package Game.org;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	private int textFieldWidth, textFieldHeight;
	
	public Client(String host){
		super("Client");
		// serverIP is used to identify the server computer the client talks to
		serverIP = host; // server IP for client to connect to
		userText = new JTextField();
		textFieldWidth = 100;
		textFieldHeight = 27;
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		
		userText.setPreferredSize(new Dimension(textFieldWidth,textFieldHeight));
		userText.setBackground(Color.LIGHT_GRAY);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setForeground(Color.black);
		add(new JScrollPane(chatWindow));
		setSize(320, 380);
		setVisible(true);
	}
	
	// connect to server
	public void startRunning(){
		try{
			connectToserver();
			setupStreams(); // setup streams that will be used for chatting
			getMessageFromServer(); // recieves messages
		}catch(EOFException eofException){
			showMessage("\n Client terminated connection");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	
	
	private void connectToserver()throws IOException{
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	// get stream to send and recieve data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush(); // clear output buffer so that message can be sent
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup !\n");
	}
	
	private void getMessageFromServer()throws IOException{
		//String message = " You are now connected! ";
		enableTyping(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n message sending error!");
			}
		}while(!message.equals("Charles - END")); // keep reading user text until this is typed
	}

	private void closeConnection(){
		showMessage("\n Closing connections .... \n");
		enableTyping(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch (IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	// send message to server
	private void sendMessage(String messageToSend){
		try{
			output.writeObject("Client - " + messageToSend);
			output.flush(); // flush buffer of any left over bytes
			showMessage("\nClient - " + messageToSend);
		}catch (IOException ioException){
			chatWindow.append("\n Error: error sending message!");
		}	
	}
	
	private void showMessage(final String textToDisplay){
		// update the chatWindow(textArea) using the thread below
		SwingUtilities.invokeLater(
			new Runnable(){ // new thread
				public void run(){
					chatWindow.append(textToDisplay);
				}
			}
		);
	}

	// enable typing ability for this user
	private void enableTyping(final boolean canType){
		SwingUtilities.invokeLater(
			new Runnable(){ // new thread
				public void run(){
					userText.setEditable(canType);
				}
			}
		);
	}

}
