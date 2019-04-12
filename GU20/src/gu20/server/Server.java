package gu20.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
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
	
	public Clients clients;
	public UnsentMessages unsentMessages;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	public ClientListeners clientListeners;

	/**
	 * Creates server socket and starts the server thread.
	 * @param port The port on which the server socket should be created.
	 */
	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		clients = new Clients();
		clientListeners = new ClientListeners();
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
		try {
			ObjectOutputStream os = listener.getOutputStream();
			ArrayList<Message> messages = unsentMessages.remove(listener.getUser());
			
			if (messages != null && messages.size() > 0) {
				os.writeUTF("MESSAGE");
				
				os.writeObject(messages);
				
				os.flush();
				
				System.out.println("Sent unsent messages to " + listener.getUser());
			}
		}
		catch (IOException ex) {}
	}
	
	/**
	 * Handles the communication with a client.
	 * @author Oskar Molander, Pontus Laos
	 *
	 */
	
	//Flytta till egen klass/struktur
}
