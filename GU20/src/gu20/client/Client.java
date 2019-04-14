package gu20.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import gu20.gui.GUIController;
import gu20.Helpers;
import gu20.entities.Message;
import gu20.entities.User;

/**
 * Represents a client: a user with a connection to a server.
 * @author Oskar Molander, Pontus Laos
 *
 */
public class Client implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	private static final String LOGGER_PATH = "logs/" + Client.class.getName() + ".log";
	
	private User user;
	
	private GUIController guiC;

	private String ip;
	private int port;

	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private Thread client = new Thread(this);
	
	/**
	 * Constructs a new client instance and starts a thread for communicating with the server.
	 * @param user The user the client should log in as.
	 * @param ip The IP-address of the server.
	 * @param port The port of the server.
	 */
	public Client(User user, String ip, int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		this.user = user;
		this.ip = ip;
		this.port = port;
		
		client.start();
	}
	
	/**
	 * Attempts to connect to the server, and if connected starts listening for updates and messages.
	 */
	@Override
	public void run() {
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return The user's username.
	 */
	public String getUsername() { 
		return user.getUsername(); 
	}
	
	/**
	 * Retrieves contacts from a text-file. Might be rewritten to throw exception later.
	 * @param username Username of user whose contacts is to be retrieved.
	 * @return Array of usernames of added contacts
	 */
	public String[] getContacts(String username) {
		
		String filename = "res/contacts/" + username + ".txt";
		String[] contacts = null;
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
			int length = Integer.parseInt(br.readLine());
			contacts = new String[length];
			
			String contact = br.readLine();
			int index = 0;
			while(contact != null) {
				contacts[index++] = contact;
				contact = br.readLine();
			}	
		} catch (IOException ex) {
			//Empty catch lol
		}	
		return contacts;
	}
	
	/**
	 * Creates or overwrites text-file with contacts.
	 * @param username Username of user whose contacts is to written to file
	 * @param contacts An array of usernames of contacts to be written to file
	 */
	public void setContacts(String username, String[] contacts) {
		String filename = "res/contacts/" + username + ".txt";
		
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8" ) )) {
			int length = contacts == null ? 0 : contacts.length;
			bw.write(String.valueOf(length));
			
			if (length != 0) {
				for (String contact : contacts) {
					bw.newLine();
					bw.write(contact);
				}
			}
			
			bw.flush();
			
		} catch (IOException ex) {
			//Gotta love empty catches
		}
	}
	
	/**
	 * Attempts to connect to the IP-address and port specified in the constructor. If successful, begins to listen for updates and messages.
	 * @throws IOException If the connection can not be made.
	 */
	private void connect() throws IOException {
		try {
			socket = new Socket(ip, port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
						
			outputStream.writeUTF("CONNECT");
			outputStream.writeObject(user);
			outputStream.flush();
		}
		catch (IOException ex) {
			System.out.println("IO exception lol");
			ex.printStackTrace();
		}
		finally {
			listen();
		}
	}
	
	/**
	 * Continually listens for updates and messages from the server.
	 * @throws IOException If something goes wrong when reading from the server.
	 */
	private void listen() throws IOException {
		while (true) {
			try {
				String header = inputStream.readUTF();
				if (header.equals("UPDATE")) {
					handleUpdate();
				}
				else if(header.equals("MESSAGE")) {
					handleMessage();
				}
			}
			catch (IOException ex) {
				continue;
			}
			catch (ClassNotFoundException ex) {}
		}
	}
	
	/**
	 * Handles an update from the server. Expects a User, followed by a String, and then an array of Users.
	 * @throws IOException If something goes wrong when reading from the server.
	 * @throws ClassNotFoundException If the server sends an unserializable object.
	 */
	private void handleUpdate() throws IOException, ClassNotFoundException {
		Object obj = inputStream.readObject();
		User user = (User) obj;
		String action = inputStream.readUTF();
		obj = inputStream.readObject();
		
		User[] users = (User[]) obj;
		System.out.println(user + " got users: " + Helpers.joinArray(users, ", "));
		guiC.onlineUsers(users);
	}
	
	/**
	 * Handles messages from the server. Expects an ArrayList of Messages.
	 * @throws ClassNotFoundException If the server sends an unserializable object.
	 * @throws IOException If something goes wrong when reading from the server.
	 */
	private void handleMessage() throws ClassNotFoundException, IOException {
		Object obj = inputStream.readObject();
		List<Message> messages = (ArrayList) obj;
		for (Message message : messages)
			message.setRecipientsReceived(Calendar.getInstance());
		guiC.receiveMessages(messages);
	}
	
	/**
	 *  Attempts to disconnect from the server. Sends a disconnect request containing the user.
	 */
	public void disconnect() {
		if (socket.isClosed()) {
			System.out.println("Client is already disconnected.");
			return;
		}
		
		try {			
			outputStream.writeUTF("DISCONNECT");
			outputStream.writeObject(user);
			outputStream.flush();
		}
		catch (IOException ex) {
			System.out.println("Exception on disconnect: " + ex.getMessage());
		}
		finally {
			try { 
				if (socket != null) {
					socket.close(); 
					System.out.println(user + " disconnected from the server.");
				}
			}
			catch (IOException ex) {}
		}
	}
	
	/**
	 * Sends a message to the server as an ArrayList.
	 * @param message The message to send.
	 */
	public void sendMessage(Message message) {
		try {
			outputStream.writeUTF("MESSAGE");
			ArrayList<Message> messages = new ArrayList<>();
			messages.add(message);
			outputStream.writeObject(messages);
			outputStream.flush();
		} 
		catch (IOException e) {
			System.out.println("Exception send message (client)");
		}
	}
	
	/**
	 * Sets the GUIController of the client.
	 * @param guiC The GUIController to use.
	 */
	public void setGUIController(GUIController guiC) {
		this.guiC = guiC;
	}
}
