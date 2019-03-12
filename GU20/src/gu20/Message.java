package gu20;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

public class Message implements Serializable {
	
	private static final long serialVersionUID = -7157985680829782142L;
	
	private MockUser sender;
	private MockUser[] recipients;
	
	private String text;
	private ImageIcon image;
	
	private Calendar serverReceived;
	private Calendar recipientsReceived;
	
	public Message(MockUser sender, MockUser[] recipients, String text, ImageIcon image) {
		this.sender = sender;
		this.recipients = recipients;
		
		this.text = text;
		this.image = image;
	}
	
	/*
	 * Constructor for testing, to be removed later
	 */
	public Message(MockUser sender, MockUser receiver, String text) {
		this(sender, new MockUser[]{receiver}, text, null);
	}
	
	public String getText() { return text; }
	
	public MockUser getSender() { return sender; }
	
	public MockUser[] getRecipients() {
		return recipients;
	}
	
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
	
	public Calendar getServerReceived() {
		return serverReceived;
	}

	public Calendar getRecipientsReceived() {
		return recipientsReceived;
	}
	
	
}
