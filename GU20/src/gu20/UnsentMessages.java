package gu20;

import java.util.ArrayList;
import java.util.HashMap;

public class UnsentMessages {

	
	private HashMap<MockUser, ArrayList<Message>> unsentMessages;
	
	
	public UnsentMessages() {
		
		unsentMessages = new HashMap<>();
	}
	
	public synchronized ArrayList<Message> put(MockUser user, Message message){
		
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
	
	public synchronized ArrayList<Message> get(MockUser user){
		ArrayList<Message> messages = unsentMessages.get(user);
		
		return unsentMessages.get(user);

	}
	
	public synchronized ArrayList<Message> remove(MockUser user) {
		ArrayList<Message> messages = unsentMessages.remove(user);
		return messages;
	}
	
	public synchronized ArrayList<Message> pop(MockUser user) {
		ArrayList<Message> popped = unsentMessages.get(user);
		unsentMessages.put(user, null);
		return popped;
	}
	
	public synchronized boolean containsKey(MockUser user) {
		return unsentMessages.containsKey(user);
	}
}
