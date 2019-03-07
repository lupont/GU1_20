package gu20;

import java.util.ArrayList;
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
	
	private GUI gui;
	
	/**
	 * Creates new GUIController and opens new login window
	 */
	public GUIController() {
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
				new LoginPanel(GUIController.this);
			}
		});
	}

	/**
	 * Opens new GUI window
	 * Change to correct GUI-window later
	 */
	public void openGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			gui = new GUI(client.getUsername(), GUIController.this);
			gui.getOnelinePanel().addOnlineUsers(onlineUsers);
			}
		});

	}
	
	public void onlineUsers(MockUser[] users) {
		if (onlineUsers == null)
			onlineUsers = users;
		else {
//			onlineUsers = newUsers(users);
			gui.getOnelinePanel().addOnlineUsers(users);
		}
	}
			
//	private MockUser[] newUsers(MockUser[] users) {
//		ArrayList<MockUser> newUsersList = new ArrayList<MockUser>();
//		for (MockUser user : users) {
//			if (!containsUser(user))
//				newUsersList.add(user);
//		}
//		
//		MockUser[] newUsers = new MockUser[newUsersList.size()];
//		for (int index = 0; index < newUsersList.size(); index++) {
//			newUsers[index] = newUsersList.get(index);
//		}
//		
//		MockUser[] temp = new MockUser[newUsers.length + users.length];
//		
//		for (int index = 0; index < users.length; index++) {
//			temp[index] = users[index];
//			temp[index+users.length-1] = newUsers[index];
//		}
//		
//		return temp;
//		
//		
//	}
	
	/**
	 * Checks if a users is already in online list
	 * @param user The user to compare
	 * @return true if user is in online list, false if not
	 */
	private boolean containsUser(MockUser user) {
		for (MockUser onlineUser : onlineUsers) {
			if (user.getUsername().equals(onlineUser.getUsername()))
				return true;
		}
		return false;
	}

	
	/**
	 * Receives message from GUI and sends it to client
	 * Must change parameter to Message-object, or create a message-object
	 * @param message
	 */
	public void sendMessage(String message) {
		System.out.println(message);
		receiveMessage(message);
	}
	
	/**
	 * Receives message from client and sends it to GUI
	 * Must change parameter to Message-object
	 * @param message
	 */
	public void receiveMessage(String message) {
		gui.newMessage(client.getUsername(), message);
	}
	
	/**
	 * Logout, disconnects client, disposes GUI and opens new login window
	 */
	public void logout() {
		client.disconnect();
		gui.disposeFrame();
		openLoginWindow();
	}
	
	/**
	 * Login-function. Receives users input username and creates a new MockUser-object.
	 * Connects to server.
	 * Opens GUI-window.
	 * @param username String received from Login-textfield
	 */
	public void login(String username) {
		Map<String, String> addresses = new HashMap<>();
		addresses.put("local", "localhost");
		
		MockUser user = new MockUser(username, null);
		client = new MockClient(user, addresses.get("local"), 12345);
		client.setGUIController(this);
		
		openGUI();
		
	}
}
