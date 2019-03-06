package gu20.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gu20.Message;
import gu20.MockUser;
import gu20.Clients;
import gu20.Helpers;
import gu20.client.MockClient;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/" + Server.class.getName() + ".log";
	
	private Clients clients;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);

	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		clients = new Clients();
		
		try {
			this.serverSocket = new ServerSocket(port);
			server.start();
		} 
		catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "The server socket could not be created.", ex);
		}
	}
	
	@Override
	public void run() {
		LOGGER.log(Level.INFO, "Server is waiting for clients...");

		while (true) {
			try {
				Socket socket = serverSocket.accept();
				new ClientListener(socket).start();
			}
			catch (IOException ex) {
				System.out.println("IO");
			}
		}	
	}
	
	private class ClientListener extends Thread {
		// The client's socket
		private Socket socket;
		
		public ClientListener(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try (
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); 
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			) {
				Object obj = inputStream.readObject();
				
				if (obj instanceof MockUser) {
				
					MockUser user = (MockUser) inputStream.readObject();
					
					// ... the next part of the request should contain a User object.
					Object obj = inputStream.readObject();
					MockUser user = (MockUser) obj;
					
					if (!clients.containsKey(user) || clients.get(user) == null) {
						clients.put(user, socket);
						System.out.println(user.getUsername() + " at (" + socket.getInetAddress().toString() + ") connected to the server.");
						Helpers.printClients(clients);
					}
				}
				
				while (true) {
					// If it is a DISCONNECT request...
					try {
						if (inputStream.readUTF().equals("DISCONNECT")) {
							
							// ... the next part of the request should contain a User object.
							Object obj = inputStream.readObject();
							MockUser user = (MockUser) obj;
							
							clients.put(user, null);
							System.out.println(user.getUsername() + " at (" + socket.getInetAddress().toString() + ") disconnected from the server.");
							Helpers.printClients(clients);
							interrupt();
							return;
						}
					}
					catch (EOFException ex) {
						continue;
					}
				}
			}
			catch (EOFException ex) {
				ex.printStackTrace();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			catch (ClassNotFoundException ex) {}
		}
	}
}
