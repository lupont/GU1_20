package gu20.server;

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

import gu20.MockUser;
import gu20.Message;
import gu20.Helpers;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/log.log";
	
	// The usernames of the currently connected users.
	private List<MockUser> connectedUsers;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<MockUser, List<Message>> users;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	
	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
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
					if (obj instanceof MockUser) {
						
						MockUser user = (MockUser) obj;
						
						// If the user who wants to sign in is using a username that is already signed in, give error message.
						if (connectedUsers.stream().anyMatch(u -> {
							return u.getUsername().equals(user.getUsername());
						})) {
							outputStream.writeUTF("CONNECT_FAILED");
							outputStream.writeUTF("Username in use.");
							outputStream.flush();
							
							LOGGER.log(Level.WARNING, "Client attempted to sign in with occupied username: {0}", user);
							return;	
						}

						// If the server does not already know the user, add it to the HashMap.
						if (!users.containsKey(user)) {
							users.put(user, new ArrayList<>());								
						}
						
						// Add the user to the currently connected ones.
						connectedUsers.add(user);
						
						// Tell the client that the connection went well.
						outputStream.writeUTF("CONNECT_ACCEPTED");
						outputStream.flush();
						
						LOGGER.log(Level.INFO, "User connected: {0}.", user);
						LOGGER.log(Level.INFO, "Number of connected users: {0}", connectedUsers.size());
					}
					else {
						
						// The object was not a User instance, tell the client that the connection failed.
						outputStream.writeUTF("CONNECT_FAILED");
						outputStream.writeUTF("User could not be parsed from object.");
						outputStream.flush();

						LOGGER.log(Level.WARNING, "A connection attempt failed because the object could not be parsed as a User.", obj);
					}
				}
				// If it is a DISCONNECT request...
				else if (method.equals("DISCONNECT")) {
					
					// ... the next part of the request should contain a User object.
					Object obj = inputStream.readObject();
					
					// Make sure that it is a user.
					if (obj instanceof MockUser) {
						MockUser user = (MockUser) obj;
						
						// Remove the user from the currently connected ones.
//						connectedUsers.remove(user);
						Iterator<MockUser> iterator = connectedUsers.iterator();
						boolean hasRemoved = false;
						while (iterator.hasNext()) {
							MockUser u = iterator.next();
							if (u.getUsername().equals(user.getUsername())) {
								iterator.remove();

								// Tell the client that the disconnection went well.
								outputStream.writeUTF("DISCONNECT_ACCEPTED");
								outputStream.flush();
								
								LOGGER.log(Level.INFO, "User disconnected: {0}.", user);
								LOGGER.log(Level.INFO, "Number of connected users: {0}", connectedUsers.size());
								hasRemoved = true;
							}
						}
						
						if (!hasRemoved) {
							outputStream.writeUTF("DISCONNECT_FAILED");
							outputStream.flush();
							
							LOGGER.log(Level.WARNING, "User tried to disconnect, but was not connected: {0}.", user);
						}
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
