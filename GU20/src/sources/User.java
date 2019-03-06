package sources;

import java.util.ArrayList;

public class User {

	private String username;
	private ArrayList<User> contacts;
	
	private Message latestMessage;
	
	public User(String username) {
		this.username = username;
		contacts = new ArrayList<User>();
	}
	
	public void addContact(User user) {
		contacts.add(user);
	}
	
	public String getUsername() {
		return username;
	}
	
	public ArrayList<User> getContacts() {
		return contacts;
	}
	
	public void setLatestMessage(Message message) {
		latestMessage = message;
	}
	
	public Message getLatestMessage() {
		return latestMessage;
	}
}
