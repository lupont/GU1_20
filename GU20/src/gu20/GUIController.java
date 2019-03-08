package gu20;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import gu20.client.MockClient;

/**
 * Control-class for communication between GUI and client
 * 
 * @author Alexander Libot
 *
 */
public class GUIController {
	
	private MockClient client;
	private MockUser[] onlineUsers;
	private MockUser user;
	
	private GUIInterface gui;
	
	private Map<String, String> addresses = new HashMap<>();
	
	/**
	 * Creates new GUIController and opens new login window
	 */
	public GUIController() {
		addresses.put("local", "localhost");
		addresses.put("atlex-server", "192.168.1.100");
		openLoginWindow();

	}
	
	/**
	 * Not sure if this constructor is needed for anything
	 * @param client
	 */
	public GUIController(MockClient client) {
		this.client = client;
		openLoginWindow();
	}
	
	/**
	 * Opens new login window
	 */
	private void openLoginWindow() {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new LoginPanel(GUIController.this, addresses);
//				new LoginPanel(GUIController.this);
			}
		});
	}

	/**
	 * Opens new GUI window.
	 * Change to correct GUI-window later.
	 */
	private void openGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			gui = new MockGUI(client.getUsername(), GUIController.this);
			gui.addOnlineUsers(onlineUsers);
			}
		});
	}
	
	public void onlineUsers(MockUser[] users) {
		
		onlineUsers = users;
		
		if (gui != null)
			gui.addOnlineUsers(onlineUsers);
		
		
	}
	
	public void addUserToContacts() {
		//TODO Ability to add user to contacts
	}

	/**
	 * Receives message from GUI and sends it to client
	 * Must change parameter to Message-object, or create a message-object
	 * @param message
	 */
	public void sendMessage(String strMessage, String recipientUsername) {
		//TODO Handle more than one recipient
		//TODO Ability to send picture
		MockUser receiver = null;
		for (MockUser user : onlineUsers) {
			if (user.getUsername().equals(recipientUsername))
				receiver = user;
		}
		
		Message message = new Message(user, receiver, strMessage);
		client.sendMessage(message);
		receiveMessage(message);
	}
	
	/**
	 * Receives message from client and sends it to GUI
	 * Must change parameter to Message-object
	 * @param message
	 */
	public void receiveMessage(Message message) {
		//TODO Ability to receive picture
		gui.viewNewMessage(message.getSender(), message.getText());
	}
	
	/**
	 * Logout, disconnects client and opens new login window
	 */
	public void logout() {
		client.disconnect();
		openLoginWindow();
	}
	
	/**
	 * Login-function. Receives users username from textfield and creates a new MockUser-object.
	 * Connects to server.
	 * Opens GUI-window.
	 * @param username String received from Login-textfield
	 */
	public void login(String username, String host) {
		
//		Map<String, String> addresses = new HashMap<>();
//		addresses.put("local", "localhost");
//		addresses.put("atlex-server", "192.168.1.100");
		
		user = new MockUser(username, null);
		client = new MockClient(user, addresses.get(host), 12345);
		client.setGUIController(this);
		
		openGUI();
	}
	
	public void addProfilePicture() {
		//TODO Ability to add profile picture
	}
}
