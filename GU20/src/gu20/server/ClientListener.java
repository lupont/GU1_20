package gu20.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import gu20.entities.Message;
import gu20.entities.User;


public class ClientListener extends Thread {
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private Server server;
	
	private User user;
	
	/**
	 * @return The socket connected to the client.
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Constructs a new ClientListener and creates input and output streams from the given socket.
	 * @param socket The socket of which to create the streams.
	 */
	public ClientListener(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException ex) {
			System.out.println("Error initializing the streams.");
		}
	}
	
	/**
	 * @return The user the client is connected as.
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @return The output stream of the connected client. Used to communicate directly with this user.
	 */
	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}
	
	/**
	 * Waits for a client to connect, and continually listens for disconnect and message requests.
	 */
	@Override
	public void run() {
		try {
			String method = inputStream.readUTF();
			
			if (method.equals("CONNECT")) {
				Object obj = inputStream.readObject();
				User user = (User) obj;
				Server.LOGGER.log(Level.INFO, String.format("%s (@%s) connected to the server.", user.getUsername(), socket.getInetAddress().toString()));
				
				Iterator<ClientListener> iterator = server.clientListeners.iterator();
				
				while (iterator.hasNext()) {
					ClientListener cl = iterator.next();
					
					if (user.equals(cl.getUser())) {
						iterator.remove();
						System.out.println("removed from clientlisteners: " + server.clientListeners.size());
					}
					
					server.clientListeners.add(this);
					System.out.println("Added to clientlisteners: " + server.clientListeners.size());
				}
				
				if (server.clients.get(user) == null) {
					server.clients.put(user, socket);
					this.user = user;
					server.updateUserList(user, "CONNECTED");
				}

				if (server.unsentMessages.containsKey(user)) {
					server.handleUnsentMessages(this);
				}
			}
			
			while (!Thread.interrupted()) {
				try {
					String response = inputStream.readUTF();
					
					if (response.equals("DISCONNECT")) {
						Object obj = inputStream.readObject();
						User user = (User) obj;
						
						server.clients.put(user, null);
						Server.LOGGER.log(Level.INFO, String.format("%s (@%s) disconnected to the server.", user.getUsername(), socket.getInetAddress().toString()));
						server.updateUserList(user, "DISCONNECTED");
//						interrupt();
						return;
					}
					else if (response.equals("MESSAGE")) {
						Object obj = inputStream.readObject();
						List<Message> messages = (ArrayList) obj;
						
						for (Message message : messages) {
							message.setServerReceived(Calendar.getInstance());
						}
						
						server.sendMessage(messages.get(0));

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