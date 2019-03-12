package gu20;

/**
 * List of classes for a GUI to implement to be able to work with GUIController
 * @author Alexander
 *
 */
public interface GUIInterface {
	
	/**
	 * Receive an array of usernames that are online.
	 * @param users 
	 */
	public void addOnlineUsers(String[] users);
	
	/**
	 * Receive a new message.
	 * @param sender Sender of message
	 * @param text Text of message
	 */
	public void viewNewMessage(MockUser sender, String text);
	
	/**
	 * Receive a list of contacts.
	 * @param contacts Contacts of the user
	 */
	public void addContacts(String[] contacts);
	
	/**
	 * Don't implement, this will be removed.
	 * @param contact
	 */
	public void addContact(String contact);
	
	/**
	 * Don't implement, this will be removed.
	 * @param contact
	 */
	public void removeContact(String contact);
	
}
