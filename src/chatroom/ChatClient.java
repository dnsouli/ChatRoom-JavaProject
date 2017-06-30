package chatroom;
import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Danny Nsouli & Tim Wood (Contributer)
 */
public class ChatClient extends ChatWindow {

	// Inner class used for networking
	private Communicator comm;

	// GUI Objects
	private JTextField serverTxt;
	private JTextField nameTxt;
	private JButton connectB;
	private JTextField messageTxt;
	private JButton sendB;

	public ChatClient() {
		super();
		this.setTitle("Chat Client");
		printMsg("Chat Client Started.");

		// GUI elements at top of window
		// Need a Panel to store several buttons/text fields
		serverTxt = new JTextField("localhost");
		serverTxt.setColumns(15);
		nameTxt = new JTextField("Name");
		nameTxt.setColumns(10);
		connectB = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(serverTxt);
		topPanel.add(nameTxt);
		topPanel.add(connectB);
		contentPane.add(topPanel, BorderLayout.NORTH);

		// GUI elements and panel at bottom of window
		messageTxt = new JTextField("");
		messageTxt.setColumns(40);
		sendB = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageTxt);
		botPanel.add(sendB);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// Resize window to fit all GUI components
		this.pack();

		// Setup the communicator so it will handle the connect button
		Communicator comm = new Communicator();
		connectB.addActionListener(comm);
		sendB.addActionListener(comm);


	}

	/**
	 * This inner class handles communication with the server.
	 */
	class Communicator implements ActionListener, Runnable {
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		private int port = 2113;

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			if (actionEvent.getActionCommand().compareTo("Connect") == 0) {
				connect();

			} else if (actionEvent.getActionCommand().compareTo("Send") == 0) {
				sendMsg(messageTxt.getText());
			}
		}

		/**
		 * Connect to the remote server and setup input/output streams.
		 */
		public void connect() {
			try {
				socket = new Socket(serverTxt.getText(), port);
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				Thread t = new Thread(this);	//starting a thread that would help with receiving messages from the server to send to all cleints
				t.start();
				sendMsg("Hello server, my name is " + nameTxt.getText()); //making it clear that someone joined the chat by adding their name

			} catch (IOException e) {
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		public void run() {		//new run method implemented in order for the client to be constantly reading messages sent from the server to display in the chat boxes of all clients
			try {
				while (true) {
					// read a message from the server
					readMsg();
				}
			} catch (IOException e) {
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}


		/**
		 * Receive and display a message
		 */
		public void readMsg() throws IOException {
			String s = reader.readLine();
			printMsg(s);
		}

		/**
		 * Send a string
		 */
		public void sendMsg(String s) {    //modify for messages to be formatted as "name: message"

			if (s.contains("/name")) {		//if the string sent by a client has /name in it

				String[] array = s.split(" ");	//the string will be split into parts and then newName will be set as the text in the Name text box
				nameTxt.setText(array[1]);


			} else {
				writer.println(nameTxt.getText() + ": " + s);  //the name will always be before the message to indicate who's sending messages to the server
			}
		}
	}


		public static void main(String args[]) {

			new ChatClient();

		}

	}
