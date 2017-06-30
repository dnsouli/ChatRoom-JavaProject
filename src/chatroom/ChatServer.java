package chatroom;
import java.lang.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Danny Nsouli
 */
public class ChatServer extends ChatWindow {

	//private ClientHandler handler;
	public ArrayList<ClientHandler> hlist = new ArrayList<ClientHandler>();

	public ChatServer(){
		super();
		this.setTitle("Chat Server");
		this.setLocation(80,80);


		try {
			// Create a listening service for connections
			// at the designated port number.
			ServerSocket srv = new ServerSocket(2113);
			printMsg("Waiting for a connection");
			while (true) {
				// The method accept() blocks until a client connects.
				//printMsg("Waiting for a connection");
				Socket socket = srv.accept();
				ClientHandler tempHandler = new ClientHandler(socket);
				hlist.add(tempHandler);	//list of handlers created to help with organization for taking on multiple clients
				//handler = new ClientHandler(socket);
				//handler.handleConnection();
				Thread t = new Thread(tempHandler);	//creating thread in order to take of task of reading client messages and sending messages back to all the client
				t.start();
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/** This innter class handles communication to/from one client. */
	class ClientHandler implements Runnable {
		private PrintWriter writer;
		private BufferedReader reader;
		private PrintWriter tempWriter;

		public ClientHandler(Socket socket) {
			try {
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				////hlist.add(this); //adding the current handler to the list
				////String message = readMsg();
				//writer.println(reader); attempt

				////sendMsg(message);



			}
			catch (IOException e){
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
				}
		}
		public void run() {	//run method reads all messages from the client all the time and sends each one back to all clients
			try {
				while(true) {
					// read a message from the client
					String message = readMsg();
					////
					sendMsg(message);
					////
					//writer.println(reader);
				}
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		/** Receive and display a message */
		public String readMsg() throws IOException {
			String s = reader.readLine();
			printMsg(s);
			return s;
		}
		/** Send a string */
		public void sendMsg(String s){
			////writer.println(s);

			for(int i=0; i<hlist.size(); i++){	//iterating through all the handers in order to write their messages to the client
				ClientHandler currentHandler = hlist.get(i);
				tempWriter = currentHandler.writer;
				tempWriter.println(s);

			}


		}

	}

	public static void main(String args[]){

		new ChatServer();

	}
}
