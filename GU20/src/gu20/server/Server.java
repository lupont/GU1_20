package gu20.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gu20.User;
import gu20.Message;
import gu20.Helpers;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	
	// The usernames of the currently connected users.
	private List<User> connectedUsers;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<User, List<Message>> users;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	
	public Server(int port) {
		Helpers.addFileHandler(LOGGER, "/home/lupont/Desktop/log.log");
		
		connectedUsers = new ArrayList<>();
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
		@Override
		public void run() {
			System.out.println("Server is handling messages...");
			
			while (!Thread.interrupted()) {
			}
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
			LOGGER.log(Level.INFO, "Server is waiting for clients...");
			
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
					
					// Make sure that it is a user.
					if (obj instanceof User) {
						
						User user = (User) obj;

						// If the server does not already know the user, add it to the HashMap.
						if (!users.containsKey(user)) {
							users.put(user, new ArrayList<>());								
						}
						
						// Add the user to the currently connected ones.
						connectedUsers.add(user);
						
						// Tell the client that the connection went well.
						outputStream.writeUTF("CONNECT_ACCEPTED");
						outputStream.flush();
						
						LOGGER.log(Level.INFO, "User connected.", user);
					}
					else {
						
						// The object was not a User instance, tell the client that the connection failed.
						outputStream.writeUTF("CONNECT_FAILED");
						outputStream.flush();

						LOGGER.log(Level.WARNING, "A connection attempt failed because the object could not be parsed as a User.", obj);
					}
				}
				// If it is a DISCONNECT request...
				else if (method.equals("DISCONNECT")) {
					
					// ... the next part of the request should contain a User object.
					Object obj = inputStream.readObject();
					
					// Make sure that it is a user.
					if (obj instanceof User) {
						User user = (User) obj;
						
						// Remove the user from the currently connected ones.
						connectedUsers.remove(user);
						
						// Tell the client that the disconnection went well.
						outputStream.writeUTF("DISCONNECT_ACCEPTED");
						outputStream.flush();
						
						LOGGER.log(Level.INFO, "User disconnected.", user);
					}
					else {
						
						// The object was not a user instance, tell the client that the disconnection failed.
						outputStream.writeUTF("DISCONNECT_FAILED");
						outputStream.flush();
						
						LOGGER.log(Level.WARNING, "A disconnection attempt failed because the object could not be parsed as a User.", obj);
					}
				}
			}
			catch (ClassNotFoundException ex) {}
			catch (IOException ex) {}
			
			try {
				socket.close();
			}
			catch (IOException ex) {}
		}
	}
}
