package gu20;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
	
	private int port = 12345;
	
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
	
	/*
	 * TODO Used for testing, skips login and creates an avatar
	 */
	public GUIController(String username, String address, String avatarPath) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new LoginPanel(GUIController.this, username, address, avatarPath);
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

				gui.addOnlineUsers(Helpers.mockUsersToString(onlineUsers));
				gui.addContacts(retrieveContacts(client.getUsername()));
				gui.addAvatar(user.getAvatar());
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
			gui.addOnlineUsers(Helpers.mockUsersToString(onlineUsers));	
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
		
//		client.setContacts(client.getUsername(), contacts);
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
				if (!username.equals(contact))
					tempUsers[index++] = user.getUsername();		
			}
			
			contacts = tempUsers;
			gui.addContacts(contacts);
//			client.setContacts(client.getUsername(), contacts);
		}
	}

	/**
	 * Receives message from GUI and sends it to client.
	 * Must change parameter to Message-object, or create a message-object
	 * @param message
	 */
	public void sendMessage(String strMessage, List<String> recipientUsernames, File file) {
		MockUser[] receivers = new MockUser[recipientUsernames.size()];
		int counter = 0;

		for (String recipientUsername : recipientUsernames) {
			for (MockUser user : onlineUsers) {
				if (recipientUsername.equals(user.getUsername()))
					receivers[counter] = user;
			}
			if (receivers[counter] == null)
				receivers[counter] = new MockUser(recipientUsername, null);
			counter++;
		}
		
		ImageIcon imageIcon = null;
		if (file != null) {
			try {
				Image image = ImageIO.read(file);
				System.out.println(image.getHeight(null) + "x" + image.getWidth(null));
				imageIcon = new ImageIcon(Helpers.getScaledImage(image, 80));
			} catch (IOException ex) {
				
			}
		}
		
		Message message = new Message(user, receivers, strMessage, imageIcon);
		client.sendMessage(message);
		receiveMessage(message);
	}
	
	/**
	 * Receives message from client and sends it to GUI
	 * Must change parameter to Message-object
	 * @param message
	 */
	public void receiveMessage(Message message) {
		gui.viewNewMessage(message.getSender(), message.getText(), message.getImage(), Helpers.mockUsersToString(message.getRecipients()));
	}
	
	/**
	 * Logout, disconnects client and opens new login window
	 */
	public void logout() {
		client.setContacts(client.getUsername(), contacts);
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
	public void login(String username, String host, File file) {
		ImageIcon avatar;
		
		try {
			avatar = new ImageIcon(Helpers.getScaledImage(Helpers.convertFileToImage(file), 40));
		} catch (IOException | NullPointerException ex ) {
			avatar = null;
		}
		
		user = new MockUser(username, avatar);
		client = new MockClient(user, addresses.get(host), port);
		client.setGUIController(this);
		
		openGUI();
	}
	
	public void addProfilePicture(File file) {
		ImageIcon avatar;

		try {
			avatar = new ImageIcon(Helpers.getScaledImage(Helpers.convertFileToImage(file), 40));
			
			user.setAvatar(avatar);
			
			gui.addAvatar(user.getAvatar());
			
		} catch (IOException e) {
			System.out.println("Couldn't set file as avatar");
		}
	}
}
