package gu20.server;

import java.io.IOException;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gu20.entities.Message;
import gu20.entities.User;
import gu20.UnsentMessages;
import gu20.Clients;
import gu20.Helpers;

/**
 * The server. Handles communication between clients.
 * @author Oskar Molander, Pontus Laos
 *
 */
public class Server implements Runnable {
	public static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/" + Server.class.getName() + ".log";
	
	private Clients clients;
	private UnsentMessages unsentMessages;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	private SynchronizedList<ClientListener> clientListeners;

	/**
	 * Creates server socket and starts the server thread.
	 * @param port The port on which the server socket should be created.
	 */
	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		clients = new Clients();
		clientListeners = new SynchronizedList<>();
		unsentMessages = new UnsentMessages();
		
		try {
			this.serverSocket = new ServerSocket(port);
			server.start();
		} 
		catch (IOException ex) {
			LOGGER.log(Level.INFO, "The server socket could not be created.", ex);
		}
	}
	
	@Override
	public void run() {
		LOGGER.log(Level.INFO, "Server is up and running at " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort() + ".");

		while (!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();
				
				ClientListener clientListener = new ClientListener(socket, this);
				clientListener.start();
			}
			catch (IOException ex) {
				System.out.println("IO");
			}
		}
	}
	
	/**
	 * Updates all the connected users with who is currently connected, and who most recently connected/disconnected.
	 * @param user The user who most recently connected or disconnected.
	 * @param action What the user did, "CONNECTED" if the user connected, "DISCONNECTED" if they disconnected.
	 */
	public void updateUserList(User user, String action) {
		User[] connectedUsers = Helpers.getConnectedUsers(clients);
		
		ObjectOutputStream os;
		for (ClientListener listener : clientListeners) {
			try {
				os = listener.getOutputStream();
				os.writeUTF("UPDATE");
				os.writeObject(user);
				os.writeUTF(action);
				os.writeObject(connectedUsers);
				os.flush();
			}
			catch (IOException ex) {}
		}
	}
	
	/**
	 * Sends a message to its recipients.
	 * @param message The message to be sent. 
	 */
	public void sendMessage(Message message) {
		ObjectOutputStream os;
		
		ArrayList<User> recipients = new ArrayList<>();
		for (User user : message.getRecipients()) {
			recipients.add(user);
		}
		
		for (ClientListener listener : clientListeners) {
			if (!recipients.contains(listener.getUser())) {
				continue;
			}
			
			os = listener.getOutputStream();
				
			if (clients.get(listener.getUser()) != null && !listener.getSocket().isClosed()) {
				try {
					if (message == null) {
						System.out.println();
						System.out.println("Message was null, not sending.");
						System.out.println();
						return;
					}
					os.writeUTF("MESSAGE");
					List<Message> messages = new ArrayList<>();
					messages.add(message);
					os.writeObject(messages);
					os.flush();
					System.out.println(message.getSender() + " Sent message to recipients: " + listener.getUser());
				} 
				catch (IOException e) {
					e.printStackTrace();
				} 
				catch (NullPointerException e) {
					System.out.println(e);
				} 
			}
			else {
				// TODO: Add message to unsent queue
				System.out.println(listener.getUser().getUsername() + " socket is closed"); 
				unsentMessages.put(listener.getUser(), message);
			}
			
			LOGGER.log(Level.INFO, String.format(
				"Server sent message from %s to [%s]", 
				message.getSender().getUsername(), 
				Helpers.joinArray(message.getRecipients(), ", ")
			));
		}
	}
	
	/**
	 * Sends all messages that were sent to a user when they were disconnected.
	 * @param listener The listener of the user who should receive the messages.
	 */
	public void handleUnsentMessages(ClientListener listener) {
		User user = listener.getUser();
		
		if (!unsentMessages.containsKey(user)) {
			return;
		}
		
		try {
			ObjectOutputStream os = listener.getOutputStream();
			ArrayList<Message> messages = unsentMessages.remove(user);
			
			if (messages != null && messages.size() > 0) {
				os.writeUTF("MESSAGE");
				
				os.writeObject(messages);
				
				os.flush();
				
				System.out.println("Sent unsent messages to " + user);
			}
		}
		catch (IOException ex) {}
	}
	
	/**
	 * Checks if there are any listeners of the given user, and removes them before adding a new one.
	 * @param user The user to check for.
	 * @param listener The listener to add.
	 */
	public void clearCache(User user, ClientListener listener) {
		Iterator<ClientListener> iterator = clientListeners.iterator();
		
		while (iterator.hasNext()) {
			ClientListener cl = iterator.next();
			
			if (user.equals(cl.getUser())) {
				iterator.remove();
				System.out.println("removed from clientlisteners: " + clientListeners.size());
			}
		}
		
		clientListeners.add(listener);
		System.out.println("Added to clientlisteners: " + clientListeners.size());
	}
	
	/**
	 * If the given User is valid, assigns the Socket to it and sends an update to the other connected users.
	 * @param user The user that has logged in.
	 * @param socket The user's socket.
	 */
	public void updateIfNewLogin(User user, Socket socket) {
		if (clients.get(user) == null) {
			clients.put(user, socket);
			updateUserList(user, "CONNECTED");
		}	
	}
	
	/**
	 * Assigns the given Socket to the User.
	 * @param user The User to assign the Socket to.
	 * @param socket The Socket to be assigned.
	 */
	public void putUser(User user, Socket socket) {
		clients.put(user, socket);
	}
}
