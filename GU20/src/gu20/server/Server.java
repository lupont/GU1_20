package gu20.server;

import java.util.HashMap;
import java.util.List;

import gu20.User;
import gu20.Message;

public class Server {
	// The usernames of the currently connected users.
	private List<String> connectedUsers;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<User, List<Message>> users;
	
	private String ip;
	private int port;
	
	private Thread clientListener;
	
	public Server(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
		this.clientListener = new ClientListener();
		this.clientListener.start();
	}
	
	
	private class ClientListener extends Thread {
		@Override
		public void run() {
			while (!Thread.interrupted()) {
				// ...
			}
		}
	}
}
