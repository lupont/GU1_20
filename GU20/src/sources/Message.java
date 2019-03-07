package sources;

import java.util.Calendar;

public class Message {
	private User sender;
	private User receiver;
	private Calendar date;
	
	private String message;
	
	public Message(User sender, User receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		
		this.message = message;
		
		date = Calendar.getInstance();
	}

	public User getSender() {
		return sender;
	}
	
	public String getSenderName() {
		return sender.getUsername();
	}

	public User getReceiver() {
		return receiver;
	}
	
	public String getReceiverName() {
		return receiver.getUsername();
	}

	public Calendar getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}
	
	
}
