package gu20;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gu20.client.Client;
import gu20.entities.User;

public class Clients {
	private HashMap<User, Socket> clients;
	
	public Clients() {
		clients = new HashMap<>();
	}
	
	public synchronized void put(User user, Socket socket) {
		clients.put(user, socket);
	}
	
	public synchronized Socket get(User user) {
		return clients.get(user);
	}
	
	public synchronized boolean containsKey(User user) {
		return clients.containsKey(user);
	}
	
	public synchronized boolean containsValue(Socket socket) {
		return clients.containsValue(socket);
	}
	
	public synchronized Socket remove(User user) {
		return clients.remove(user);
	}
	
	public synchronized int size() {
		return clients.size();
	}
	
	public synchronized boolean isEmpty() {
		return clients.isEmpty();
	}
	
	public Set<Map.Entry<User, Socket>> entrySet() {
		return clients.entrySet();
	}
}
