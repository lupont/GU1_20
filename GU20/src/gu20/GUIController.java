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
	private MockUser user;
	
	private MockUser[] onlineUsers;
	private String[] contacts;
	
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
	
	/*
	 * TODO Used for testing, skips login-window, to be removed.
	 */
	public GUIController(String username, String address) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new LoginPanel(GUIController.this, username, address);
			}
		});
	}
	
	/**
	 * Opens new login window
	 */
	private void openLoginWindow() {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new LoginPanel(GUIController.this, addresses);
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

				gui.addOnlineUsers(mockUsersToString(onlineUsers));
				gui.addContacts(retrieveContacts(client.getUsername()));
				
			}
		});
	}
	
	/**
	 * Receives update from Client when user logs in or out.
	 * Updates list of online users in gui.
	 * @param users A list on online users
	 */
	public void onlineUsers(MockUser[] users) {
		
			onlineUsers = users;
		
		if (gui != null)
			gui.addOnlineUsers(mockUsersToString(onlineUsers));	
	}
	
	/**
	 * Converts an array of MockUser-objects to an array of String-objects
	 * containing MockUsers' usernames
	 * @param users Array of MockUsers to be converted
	 * @return Array of Strings containing MockUsers' usernames
	 */
	private String[] mockUsersToString(MockUser[] users) {
		if (users != null) {
			String[] strUsers = new String[users.length];
			for (int index = 0; index < users.length; index++) {
				strUsers[index] = users[index].getUsername();
			}
			return strUsers;
		}
		return null;
	}
	
	/**
	 * Retrieves a String-array of usernames a user has saved as contacts.
	 * @param username The username of the current user
	 * @return Array with usernames to the user's contacts
	 */
	private String[] retrieveContacts(String username) {
		
		contacts = client.getContacts(username);

		return contacts;
	}
	
	/**
	 * Adds a new username to list of contacts.
	 * @param username Username to be added to list of contacts
	 */
	public void addUserToContacts(String username) {
		
		//Check if contact is already added
		if (contacts != null) {
			for (String contact : contacts) {
				if (contact.equals(username))
					return;
			}
			String[] tempUsers = new String[contacts.length+1]; //Expand array by 1
			for (int i = 0; i < contacts.length; i++) { //Add all previous contacts
				tempUsers[i] = contacts[i];
			}
			tempUsers[contacts.length] = username; //Add new contact
			contacts = tempUsers;
		} else {
			contacts = new String[] {username}; //If no contacts exist, make new list
		}
		
		client.setContacts(client.getUsername(), contacts);
		gui.addContacts(contacts);
	}
	
	/**
	 * Removes a username from list of contacts.
	 * @param username Username to be removed from list of contacts
	 */
	public void removeUserFromContacts(String username) {
		if (contacts != null) {
			String[] tempUsers = new String[contacts.length - 1];
			int index = 0;

			for (String contact : contacts) {
				if (username.equals(contact))
					gui.removeContact(username);
				else
					tempUsers[index++] = user.getUsername();
			}
			contacts = tempUsers;
			client.setContacts(client.getUsername(), contacts);
		}
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
			if (recipientUsername.equals(user.getUsername()))
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
	 * TODO Check if username is already taken, and if unsent messages need to be imported
	 * @param username String received from Login-textfield
	 */
	public void login(String username, String host) {
		
		user = new MockUser(username, null);
		client = new MockClient(user, addresses.get(host), 12345);
		client.setGUIController(this);
		
		openGUI();
	}
	
	public void addProfilePicture() {
		//TODO Ability to add profile picture
	}
}
