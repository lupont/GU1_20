package gu20;

import javax.swing.SwingUtilities;

import sources.Message;
import sources.User;

public class GUIController {
	
	private User user;
	
	private GUI gui;
	private LoginPanel lp;
	
	public GUIController() {
		
	}
	
	public GUIController(User user) {
		this.user = user;
	}
	
	public void setClient(User user) {
		this.user = user;
	}
	
	public void openLogin() {
		lp = new LoginPanel();
		lp.setController(this);
		lp.putInFrame();
	}

	public void openGUI() {
		gui = new GUI(user);
		gui.setController(this);
	}
	
	public void sendMessage(String message) {
		System.out.println(message);
		newMessage(message);
	}
	
	public void newMessage(String message) {
		gui.newMessage(user.getUsername(), message);
	}
	
	public void changeContact(String name) {
		
	}
	
	public static void main(String[] args) {
		GUIController guiC = new GUIController();
		guiC.openLogin();
	}

	public void testInit() {
		
		User user1 = new User("Dwight Schrute");
		User user2 = new User("Stanley");
		User user3 = new User("Michael Scott");
		
		Message message1 = new Message(user, user1, "Test1");
		Message message2 = new Message(user, user2, "Test2");
		Message message3 = new Message(user, user3, "Test3");
		
		user1.setLatestMessage(message1);
		user2.setLatestMessage(message2);
		user3.setLatestMessage(message3);
		
		user.addContact(user1);
		user.addContact(user2);
		user.addContact(user3);
	}
}
