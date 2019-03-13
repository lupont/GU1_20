package gu20.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

/**
 * Represents a message between users. Contains the sender, recipients, an optional text, and an optional image. Also sets the date and time when the server receives the message, and when it gets delivered to the recipients.
 * @author Oskar Molander, Pontus Laos
 *
 */
public class Message implements Serializable {
	private static final long serialVersionUID = -7157985680829782142L;
	
	private User sender;
	private User[] recipients;
	
	private String text;
	private ImageIcon image;
	
	private Calendar serverReceived;
	private Calendar recipientsReceived;
	
	/**
	 * Constructs a new message with the given parameters.
	 * @param sender The user who sent the message.
	 * @param recipients The users who should receive the message.
	 * @param text The text in the message.
	 * @param image The image in the message.
	 */
	public Message(User sender, User[] recipients, String text, ImageIcon image) {
		this.sender = sender;
		this.recipients = recipients;
		
		this.text = text;
		this.image = image;
	}
	
	/**
	 * @return The message's text.
	 */
	public String getText() { 
		return text;
	}
	
	/**
	 * @return The user who sent the message.
	 */
	public User getSender() { 
		return sender; 
	}
	
	/**
	 * @return The users who should receive the message.
	 */
	public User[] getRecipients() {
		return recipients;
	}
	
	/**
	 * @return The message's image.
	 */
	public ImageIcon getImage() {
		return image;
	}
	
	/**
	 * Should be called by the server when the server has received a message from a user.
	 * @param date The date and time at which the server received the message.
	 */
	public void setServerReceived(Calendar calendar) {
		this.serverReceived = calendar;
	}
	
	/**
	 * Should be called when the server gets a response from the recipients that they have received the message.
	 * @param date The date and time at which the recipients received the message.
	 */
	public void setRecipientsReceived(Calendar calendar) {
		this.recipientsReceived = calendar;
	}
	
	/**
	 * @return The date and time when the server received the message.
	 */
	public Calendar getServerReceived() {
		return serverReceived;
	}

	/**
	 * @return The date and time when the message has been delivered to the recipients.
	 */
	public Calendar getRecipientsReceived() {
		return recipientsReceived;
	}
	
	
}
