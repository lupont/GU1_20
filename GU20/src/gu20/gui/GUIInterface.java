package gu20.gui;

import javax.swing.ImageIcon;

import gu20.MockUser;

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
	 * Used to receive and display a new message in GUI
	 * @param sender MockUser-object of the sender of the message
	 * @param message The text of the message
	 * @param image The image of the message
	 * @param recipients A string-array of the recipients usernames
	 */
	public void viewNewMessage(MockUser sender, String message, ImageIcon image, String[] recipients);
	
	/**
	 * Receive a list of contacts.
	 * @param contacts Contacts of the user
	 */
	public void addContacts(String[] contacts);
	
	/**
	 * Adds a new avatar
	 * @param avatar An imageicon object to add as avatar
	 */
	public void addAvatar(ImageIcon avatar);
	
}
