package gu20;

import java.util.HashMap;

import sources.Message;
import sources.User;

public class GUIController {
	
	private User user;
	
	private GUI gui;
	
	public GUIController() {
	}
	
	public void setClient(User user) {
		this.user = user;
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
		guiC.setClient(testInit());
		guiC.openGUI();
	}

	public static User testInit() {
		
		User myUser = new User("Jim Halpert");
		
		User user1 = new User("Dwight Schrute");
		User user2 = new User("Stanley");
		User user3 = new User("Michael Scott");
		
		Message message1 = new Message(myUser, user1, "Test1");
		Message message2 = new Message(myUser, user2, "Test2");
		Message message3 = new Message(myUser, user3, "Test3");
		
		user1.setLatestMessage(message1);
		user2.setLatestMessage(message2);
		user3.setLatestMessage(message3);
		
		myUser.addContact(user1);
		myUser.addContact(user2);
		myUser.addContact(user3);
		
		return myUser;
	}
}
