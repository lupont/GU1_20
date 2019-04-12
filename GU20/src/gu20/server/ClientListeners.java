package gu20.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientListeners implements Iterable<ClientListener> {
	private List<ClientListener> clientListeners;
	
	public ClientListeners() {
		clientListeners = new ArrayList<>();
	}

	@Override
	public Iterator<ClientListener> iterator() {
		return clientListeners.iterator();
	}
	
	public synchronized boolean add(ClientListener clientListener) {
		return clientListeners.add(clientListener);
	}
	
	public synchronized int size() {
		return clientListeners.size();
	}
}
