package gu20;

import javax.swing.ImageIcon;

import gu20.entities.User;

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
	 * @param recipients Recipients of message
	 */
	public void viewNewMessage(User sender, String text, ImageIcon image, String[] recipients);
	
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
	
	public void addAvatar(ImageIcon avatar);
	
}
