package Game.org;
import java.io.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;

public class Server extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private int textFieldWidth, textFieldHeight; 
	
	public Server(){
		super("Instant Messenger"); // Title of frame
		userText = new JTextField();
		userText.setEditable(false);
		textFieldWidth = 100;
		textFieldHeight = 27;
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText(""); // clear the message field after sending a message
					}
				}
				
		);
		
		userText.setPreferredSize(new Dimension(textFieldWidth,textFieldHeight));
		userText.setBackground(Color.LIGHT_GRAY);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setForeground(Color.black);
		add(new JScrollPane(chatWindow));
		setSize(320, 380); // width, height
		setVisible(true);
	}
	
	// set up and run the server
	public void startRunning(){
		try{
			/* port number identifies where this application is on my computer
			 * so that the client application can access it
			 * */
			server = new ServerSocket(6789, 100);
			while(true){
				try{
					waitForConnection(); // wait for incoming connections
					setupStreams(); // setup streams that will be used for chatting
					getMessageFromClient(); // recieves messages
				}catch(EOFException eofException){
					showMessage("\n Server ended the connection!");
				}finally{
					closeConnection();
				}
				
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	// wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect ...\n");
		connection = server.accept(); // accept connection request from client
		showMessage("Now connected to" +connection.getInetAddress().getHostName()+"\n");
	}
	
	// get stream to send and recieve data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush(); // clear output buffer so that message can be sent
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup !\n");
	}
	
	private void getMessageFromClient() throws IOException{
		String message = " You are now connected! ";
		sendMessage(message);
		enableTyping(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n message sending error!");
			}
		}while(!message.equals("CLIENT - END")); // keep reading user text until this is typed
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
	
	private void sendMessage(String messageToSend){
		try{
			output.writeObject("Charles - " + messageToSend);
			output.flush(); // flush buffer of any left over bytes
			showMessage("\nCharles - " + messageToSend);
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
