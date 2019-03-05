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
import gu20.Helpers;
import gu20.client.MockClient;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/" + Server.class.getName() + ".log";
	
	// The usernames of the currently connected users.
	private List<MockClient> connectedClients;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<MockClient, List<Message>> users;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	
	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		connectedClients = new ArrayList<>();
		users = new HashMap<>();
		
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
				new ClientListener(socket).start();
			}
			catch (IOException ex) {}
		}
		
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
	
	public void mockMessageSending() {
		Message message = new Message(null, null, "Hello world!", null);
		new MessageHandler(message).start();
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
		
		private void onConnect(MockClient client, ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
			if (client == null) {
				outputStream.writeUTF("CONNECT_FAILED");
				outputStream.writeUTF("User could not be parsed from object.");
				outputStream.flush();

				LOGGER.log(Level.WARNING, "A connection attempt failed because the object could not be parsed as a User.");
				return;
			}
			
			// If the user who wants to sign in is using a username that is already signed in, give error message.
			if (connectedClients.stream().anyMatch(u -> {
				return u.getUsername().equals(client.getUsername());
			})) {
				outputStream.writeUTF("CONNECT_FAILED");
				outputStream.writeUTF("Username in use.");
				outputStream.flush();
				
				LOGGER.log(Level.WARNING, "Client attempted to sign in with occupied username: {0}", client);
				return;	
			}

			// If the server does not already know the user, add it to the HashMap.
			if (!users.containsKey(client)) {
				users.put(client, new ArrayList<>());								
			}
			
			// Add the user to the currently connected ones.
			connectedClients.add(client);
			
			// Tell the client that the connection went well.
			outputStream.writeUTF("CONNECT_ACCEPTED");
			outputStream.flush();
			
			LOGGER.log(Level.INFO, "User connected: {0}.", client);
			LOGGER.log(Level.INFO, "Number of connected users: {0}", connectedClients.size());
		}
		
		private void onDisconnect(MockClient client, ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
			if (client == null) {

				// The object was not a user instance, tell the client that the disconnection failed.
				outputStream.writeUTF("DISCONNECT_FAILED");
				outputStream.flush();
				
				LOGGER.log(Level.WARNING, "A disconnection attempt failed because the object could not be parsed as a User.");
				return;
			}
			
			Iterator<MockClient> iterator = connectedClients.iterator();

			while (iterator.hasNext()) {
				MockClient u = iterator.next();
				if (u.getUsername().equals(client.getUsername())) {
					iterator.remove();

					// Tell the client that the disconnection went well.
					outputStream.writeUTF("DISCONNECT_ACCEPTED");
					outputStream.flush();
					
					LOGGER.log(Level.INFO, "User disconnected: {0}.", client);
					LOGGER.log(Level.INFO, "Number of connected users: {0}", connectedClients.size());
					return;
				}
			}
			
			outputStream.writeUTF("DISCONNECT_FAILED");
			outputStream.flush();
			
			LOGGER.log(Level.WARNING, "User tried to disconnect, but was not connected: {0}.", client);
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
					
					if (obj instanceof MockClient) {
						onConnect((MockClient) obj, outputStream, inputStream);
						new UpdateHandler().start();
					}
					else {
						onConnect(null, outputStream, inputStream);
					}
				}
				// If it is a DISCONNECT request...
				else if (method.equals("DISCONNECT")) {
					
					// ... the next part of the request should contain a User object.
					Object obj = inputStream.readObject();
					
					if (obj instanceof MockClient) {
						onDisconnect((MockClient) obj, outputStream, inputStream);
						new UpdateHandler().start();
					}
					else {
						onDisconnect(null, outputStream, inputStream);
					}
				}
				// If MESSAGE request
				else if (method.equals("MESSAGE")) {
					Message message = (Message) inputStream.readObject();

					new MessageHandler(message).start();
				}
			}
			catch (ClassNotFoundException | IOException ex) {}
			
			try { socket.close(); }
			catch (IOException ex) {}
		}
	}
}
