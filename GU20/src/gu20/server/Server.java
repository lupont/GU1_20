package gu20.server;

import java.io.DataInputStream;
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

public class Server {
	// The usernames of the currently connected users.
	private List<User> connectedUsers;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<User, List<Message>> users;
	
	private String ip;
	private int port;
	private ServerSocket serverSocket;
	
	private ClientListener clientListener;
	private MessageHandler messageHandler;
	
	public Server(String ip, int port) {
		connectedUsers = new ArrayList<>();
		users = new HashMap<>();
		
		this.ip = ip;
		this.port = port;
		
		try {
			this.serverSocket = new ServerSocket(port);
		} 
		catch (IOException ex) {
			Logger.getLogger("serverLogger").log(Level.SEVERE, "Not good server socket.", ex);
		}
		
		this.clientListener = new ClientListener();
		this.clientListener.start();
		
		this.messageHandler = new MessageHandler();
		this.messageHandler.start();
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
		@Override
		public void run() {
			System.out.println("Server is waiting for clients...");
			
			while (!Thread.interrupted()) {
				try (
					Socket socket = serverSocket.accept();
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
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
						}
						else {
							
							// The object was not a User instance, tell the client that the connection failed.
							outputStream.writeUTF("CONNECT_FAILED");

							// TODO: Log...
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
						}
						else {
							
							// The object was not a user instance, tell the client that the disconnection failed.
							outputStream.writeUTF("DISCONNECT_FAILED");
							
							// TODO: Log...
						}
					}
				}
				catch (ClassNotFoundException ex) {}
				catch (IOException ex) {}
			}
		}
	}
}
