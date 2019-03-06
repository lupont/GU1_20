package gu20.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gu20.Message;
import gu20.MockUser;
import gu20.Clients;
import gu20.Helpers;
import gu20.client.MockClient;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/" + Server.class.getName() + ".log";
	
	// The usernames of the currently connected users.
	private List<MockClient> connectedClients;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<MockClient, List<Message>> users;
	private Clients clients;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	
	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		connectedClients = new ArrayList<>();
		users = new HashMap<>();
		clients = new Clients();
		
		try {
			this.serverSocket = new ServerSocket(port);
		} 
		catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "The server socket could not be created.", ex);
		}
		
		server.start();
	}
	
	@Override
	public void run() {
		LOGGER.log(Level.INFO, "Server is waiting for clients...");

		while (true) {
			try {
				Socket socket = serverSocket.accept();
				// TODO: add socket to a list/map?
				new ClientListener(socket).start();
			}
			catch (IOException ex) {}
		}	
	}

	public void mockMessageSending() {
		Message message = new Message(null, null, "Hello world!", null);
		new MessageHandler(message).start();
	}
	
	/**
	 * Responsible for handling messages sent by users, as well as send updates to them.
	 * @author lupont & Ogar
	 *
	 */
	private class MessageHandler extends Thread {
		private Message message;
		
		public MessageHandler(Message message) {
			this.message = message;
			// TODO: set the dates on the message
		}
		
		@Override
		public void run() {
			System.out.println("Server is handling messages...");
			
			try (
				Socket socket = serverSocket.accept();
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			) {
				outputStream.writeUTF("MESSAGE");
				outputStream.writeObject(message);
				outputStream.flush();
							
				LOGGER.log(Level.INFO, "Sent message.");
			}
			catch (IOException ex) {}
		}
	}
	
	private class UpdateHandler extends Thread {
		@Override
		public void run() {
			
		}
	}
	
	/**
	 * Constantly listens for clients connecting and disconnecting.
	 * @author lupont & ingen annan
	 *
	 */
	private class ClientListener extends Thread {		
		private Socket socket;
		
		public ClientListener(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {			
			try (
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			) {
				// Get the request type from the stream.
				String method = inputStream.readUTF();
				
				// If it is a CONNECT request...
				if (method.equals("CONNECT")) {
					
					// ... the next part of the request should contain a User object.
					Object obj = inputStream.readObject();
					MockUser user = (MockUser) obj;
					
					if (!clients.containsKey(user) || clients.get(user) == null) {
						clients.put(user, socket);
						System.out.println(user.getUsername() + " at (" + socket.getInetAddress().toString() + ") connected to the server.");
						Helpers.printClients(clients);
					}
				}
				
				while (true) {
					// If it is a DISCONNECT request...
					try {
						if (inputStream.readUTF().equals("DISCONNECT")) {
							
							// ... the next part of the request should contain a User object.
							Object obj = inputStream.readObject();
							MockUser user = (MockUser) obj;
							
							clients.put(user, null);
							System.out.println(user.getUsername() + " at (" + socket.getInetAddress().toString() + ") disconnected from the server.");
							Helpers.printClients(clients);
							interrupt();
							return;
						}
					}
					catch (EOFException ex) {
						continue;
					}
				}
			}
			catch (EOFException ex) {
				ex.printStackTrace();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			catch (ClassNotFoundException ex) {}
		}
	}
}
