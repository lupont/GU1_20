package gu20;

import java.util.HashMap;

import gu20.client.MockClient;

public class Clients {
	private HashMap<MockUser, MockClient> clients;
	
	public Clients() {
		clients = new HashMap<>();
	}
	
	public synchronized void put(MockUser user, MockClient client) {
		clients.put(user,  client);
	}
	
	public synchronized MockClient get(MockUser user) {
		return clients.get(user);
	}
	
	public synchronized boolean containsKey(MockUser user) {
		return clients.containsKey(user);
	}
	
	public synchronized boolean containsValue(MockClient client) {
		return clients.containsValue(client);
	}
	
	public synchronized MockClient remove(MockUser user) {
		return clients.remove(user);
	}
	
	public synchronized int size() {
		return clients.size();
	}
	
	public synchronized boolean isEmpty() {
		return clients.isEmpty();
	}
}
