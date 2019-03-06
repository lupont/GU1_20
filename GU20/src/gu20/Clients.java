package gu20;

import java.net.Socket;
import java.util.HashMap;

import gu20.client.MockClient;

public class Clients {
	private HashMap<MockUser, Socket> clients;
	
	public Clients() {
		clients = new HashMap<>();
	}
	
	public synchronized Socket put(MockUser user, Socket socket) {
		return clients.put(user, socket);
	}
	
	public synchronized Socket get(MockUser user) {
		return clients.get(user);
	}
	
	public synchronized boolean containsKey(MockUser user) {
		return clients.containsKey(user);
	}
	
	public synchronized boolean containsValue(Socket socket) {
		return clients.containsValue(socket);
	}
	
	public synchronized Socket remove(MockUser user) {
		return clients.remove(user);
	}
	
	public synchronized int size() {
		return clients.size();
	}
	
	public synchronized boolean isEmpty() {
		return clients.isEmpty();
	}
}
