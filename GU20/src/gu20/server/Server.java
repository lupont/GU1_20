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
import gu20.UnsentMessages;
import gu20.Clients;
import gu20.Helpers;
import gu20.Message;

public class Server implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	public static final String LOGGER_PATH = "logs/" + Server.class.getName() + ".log";
	
	private Clients clients;
	private UnsentMessages unsentMessages;
	
	private ServerSocket serverSocket;
	
	private Thread server = new Thread(this);
	private List<ClientListener> clientListeners;

	public Server(int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		clients = new Clients();
		clientListeners = new ArrayList<>();
		unsentMessages = new UnsentMessages();
		
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
		LOGGER.log(Level.INFO, "Server is waiting for clients...");

		while (true) {
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
							System.out.println(listener.getUser() == null ? "Server sent update, but user had not been set." : "Server sent update to " + listener.getUser());
							System.out.println(Helpers.joinArray(connectedUsers, ", "));
							os.flush();
						}
						catch (IOException ex) {}
					}
				}
			}
		}).start();
	}
	
	private void sendMessage(Message message) {
		new Thread(() -> {
			ObjectOutputStream os;
			for (MockUser recipient : message.getRecipients()) {
				for (ClientListener listener : clientListeners) {
					if (listener.getUser().getUsername().equals(recipient.getUsername())) {
						os = listener.getOutputStream();
						
						if (!listener.getSocket().isClosed()) {
							try {
								os.writeUTF("MESSAGE");
								ArrayList<Message> messages = new ArrayList<>();
								messages.add(message);
								os.writeObject(messages);
								os.flush();
								System.out.println(message.getSender() + " Sent message to recipients: " + recipient);
							} 
							catch (IOException e) {
								System.out.println(e);
							} 
							catch (NullPointerException e) {
								System.out.println(e);
							} 
						}
						else {
							// TODO: Add message to unsent queue
							System.out.println(listener.getUser().getUsername() + " socket is closed"); 
							unsentMessages.put(listener.getUser(), message);
						}
					}	
				}	
			}
		}).start();
	}
	
	private void handleUnsentMessages(ClientListener listener) {
		try {
			ObjectOutputStream os = listener.getOutputStream();
			ArrayList<Message> messages = unsentMessages.remove(listener.getUser());
			
			if (messages != null && messages.size() > 0) {
				os.writeUTF("MESSAGE");
				
				os.writeObject(messages);
				
				os.flush();
				
				System.out.println("Sent unsent messages to " + listener.getUser());
//				unsentMessages.put(listener.getUser(), null);
			}
		}
		catch (IOException ex) {}
	}
	
	private class ClientListener extends Thread {
		// The client's socket
		private Socket socket;
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		private MockUser user;
		
		public Socket getSocket() {
			return socket;
		}
		
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
						System.out.println(user.getUsername() + " at (" + socket.getInetAddress().toString() + ") connected to the server.");
						Helpers.printClients(clients);
						
						updateUserList(user, "CONNECTED");
						handleUnsentMessages(this);
					}
				}
				
				while (true) {
					// If it is a DISCONNECT request...
					try {
						String response = inputStream.readUTF();
						
						if (response.equals("DISCONNECT")) {
							
							// ... the next part of the request should contain a User object.
							Object obj = inputStream.readObject();
							MockUser user = (MockUser) obj;
							
							clients.put(user, null);
							System.out.println(user.getUsername() + " at (" + socket.getInetAddress().toString() + ") disconnected from the server.");
							Helpers.printClients(clients);
//							updateUserList(user, "DISCONNECTED");
							
							interrupt();
							return;
						}
						else if(response.equals("MESSAGE")) {
							Object obj = inputStream.readObject();
							List<Message> messages = (ArrayList) obj;
							
							//send
							sendMessage(messages.get(0));
	
						}
					}
					catch (EOFException ex) {
						continue;
					}
					catch (SocketException ex) {
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
			finally {
				try {
					outputStream.close();
					inputStream.close();
//					synchronized (clientListeners) {
//						clientListeners.remove(this);
//					}
				}
				catch (IOException ex) {}
			}
		}
	}
}
