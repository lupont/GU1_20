package gu20;

import java.util.ArrayList;
import java.util.HashMap;

import gu20.entities.Message;
import gu20.entities.User;

public class UnsentMessages {

	
	private HashMap<User, ArrayList<Message>> unsentMessages;
	
	
	public UnsentMessages() {
		
		unsentMessages = new HashMap<>();
	}
	
	public synchronized ArrayList<Message> put(User user, Message message){
		
		//first time
		if(!unsentMessages.containsKey(user) || unsentMessages.get(user) == null) {
			ArrayList<Message> messages = new ArrayList<>();
			messages.add(message);
			return unsentMessages.put(user, messages);
		}
		
		ArrayList<Message> messages = unsentMessages.get(user);
		messages.add(message);
		
		return unsentMessages.put(user, messages);
	}
	
	public synchronized ArrayList<Message> get(User user){
		ArrayList<Message> messages = unsentMessages.get(user);
		
		return unsentMessages.get(user);

	}
	
	public synchronized ArrayList<Message> remove(User user) {
		ArrayList<Message> messages = unsentMessages.remove(user);
		return messages;
	}
	
	public synchronized ArrayList<Message> pop(User user) {
		ArrayList<Message> popped = unsentMessages.get(user);
		unsentMessages.put(user, null);
		return popped;
	}
	
	public synchronized boolean containsKey(User user) {
		return unsentMessages.containsKey(user);
	}
}
