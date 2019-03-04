package gu20.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gu20.User;
import gu20.Message;

public class Server {
	// The usernames of the currently connected users.
	private List<String> connectedUsers;
	
	// The HashMap with every user ever connected to the server. The key is the User, and the value is the list of messages the user has sent.
	private HashMap<User, List<Message>> users;
	
	private String ip;
	private int port;
	private ServerSocket serverSocket;
	
	private ClientListener clientListener;
	private MessageHandler messageHandler;
	
	public Server(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
		try {
			this.serverSocket = new ServerSocket(port);
		} 
		catch (IOException ex) {
			Logger.getLogger("serverLogger").log(Level.SEVERE, "Not good server socket.", ex);
		}
		
		this.clientListener = new ClientListener();
		this.clientListener.start();
		
		this.messageHandler = new MessageHandler();
		this.messageHandler.start();
	}
	
	/**
	 * Responsible for handling messages sent by users, as well as send updates to them.
	 * @author lupont & Ogar
	 *
	 */
	private class MessageHandler extends Thread {
		@Override
		public void run() {
			System.out.println("Server is handling messages...");
			
			while (!Thread.interrupted()) {
			}
		}
	}
	
	/**
	 * Constantly listens for clients connecting and disconnecting.
	 * @author lupont & ingen annan
	 *
	 */
	private class ClientListener extends Thread {
		@Override
		public void run() {
			System.out.println("Server is waiting for clients...");
			
			while (!Thread.interrupted()) {
				try (
					Socket socket = serverSocket.accept();
				) {
				}
				catch (IOException ex) {}
			}
		}
	}
}
