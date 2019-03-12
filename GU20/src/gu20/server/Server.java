package gu20.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import gu20.MockUser;
import gu20.Clients;
import gu20.Helpers;
import gu20.Message;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/" + Server.class.getName() + ".log";
	
	private Clients clients;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	private List<ClientListener> clientListeners;

	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		clients = new Clients();
		clientListeners = new ArrayList<>();
		
		try {
			this.serverSocket = new ServerSocket(port);
			server.start();
		} 
		catch (IOException ex) {
			LOGGER.log(Level.INFO, "The server socket could not be created.", ex);
		}
	}
	
	@Override
	public void run() {
		LOGGER.log(Level.INFO, "Server is up and running at " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort() + ".");

		while (!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();
				
				synchronized (clientListeners) {
					ClientListener clientListener = new ClientListener(socket);
					clientListener.start();
					clientListeners.add(clientListener);
				}
			}
			catch (IOException ex) {
				System.out.println("IO");
			}
		}
	}
	
	public void close() {
		if (server != null) {
			synchronized (clientListeners) {
				for (ClientListener listener : clientListeners) {
					listener.interrupt();
					listener = null;
				}
				clientListeners.clear();
			}
			
			server.interrupt();
			server = null;
			
			LOGGER.log(Level.INFO, "Server stopped.");
		}
	}
	
	private void updateUserList(MockUser user, String action) {
		new Thread(new Runnable() {
			public synchronized void run() {
				MockUser[] connectedUsers = Helpers.getConnectedUsers(clients);
				
				ObjectOutputStream os;
				synchronized (clientListeners) {
					for (ClientListener listener : clientListeners) {
						try {
							os = listener.getOutputStream();
							os.writeUTF("UPDATE");
							os.writeObject(user);
							os.writeUTF(action);
							os.writeObject(connectedUsers);
							os.flush();
						}
						catch (IOException ex) {}
					}
				}
			}
		}).start();
	}
	
	private void sendMessage(Message message) {
		new Thread(new Runnable() {
			public synchronized void run() {
				ObjectOutputStream os;
				for (MockUser recipient : message.getRecipients()) {
					for (ClientListener listener : clientListeners) {
						if (listener.getUser().getUsername().equals(recipient.getUsername())) {
							os = listener.getOutputStream();
							try {
								os.writeUTF("MESSAGE");
								os.writeObject(message);
								os.flush();
							} 
							catch (IOException | NullPointerException e) {} 							
						}
						// TODO: Add message to unsent queue
					}
				}
				LOGGER.log(Level.INFO, String.format(
					"Server sent message from %s to [%s]", 
					message.getSender().getUsername(), 
					Helpers.joinArray(message.getRecipients(), ", ")
				));
			}
		}).start();
	}
	
	private class ClientListener extends Thread {
		// The client's socket
		private Socket socket;
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		private MockUser user;
		
		public ClientListener(Socket socket) {
			this.socket = socket;
			
			try {
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream = new ObjectInputStream(socket.getInputStream());
			}
			catch (IOException ex) {
				System.out.println("Error initializing the streams.");
			}
		}
		
		public MockUser getUser() {
			return user;
		}
		
		public ObjectOutputStream getOutputStream() {
			return outputStream;
		}
		
		@Override
		public void run() {
			try {
				// Get the request type from the stream.
				String method = inputStream.readUTF();
				
				// If it is a CONNECT request...
				if (method.equals("CONNECT")) {
					// ... the next part of the request should contain a User object.
					Object obj = inputStream.readObject();
					MockUser user = (MockUser) obj;
					
					if (!clients.containsKey(user) || clients.get(user) == null) {
						clients.put(user, socket);
						this.user = user;
						LOGGER.log(Level.INFO, String.format("%s (@%s) connected to the server.", user.getUsername(), socket.getInetAddress().toString()));
						updateUserList(user, "CONNECTED");
					}
				}
				
				while (!Thread.interrupted()) {
					// If it is a DISCONNECT request...
					try {
						String response = inputStream.readUTF();
						
						if (response.equals("DISCONNECT")) {
							
							// ... the next part of the request should contain a User object.
							Object obj = inputStream.readObject();
							MockUser user = (MockUser) obj;
							
							clients.put(user, null);
							LOGGER.log(Level.INFO, String.format("%s (@%s) disconnected to the server.", user.getUsername(), socket.getInetAddress().toString()));
							updateUserList(user, "DISCONNECTED");
							
							interrupt();
							return;
						}
						else if(response.equals("MESSAGE")) {
							Object obj = inputStream.readObject();
							Message message = (Message) obj;
							sendMessage(message);
							LOGGER.log(Level.INFO, String.format("Server received a message from %s.", message.getSender().getUsername()));
						}
						
						Thread.sleep(500);
					}
					catch (EOFException | SocketException ex) {
						continue;
					}
					catch (InterruptedException ex) {
						break;
					}
				}
			}
			catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			finally {
				try {
					outputStream.close();
					inputStream.close();
				}
				catch (IOException ex) {}
			}
		}
	}
}
