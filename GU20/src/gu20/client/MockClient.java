package gu20.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gu20.Helpers;
import gu20.Message;
import gu20.MockUser;

/**
 * A mock of how a client could be implemented, used for testing the Server.
 * @author lupont
 *
 */
public class MockClient implements Serializable {
	private static final long serialVersionUID = 235235245L;
	private static final Logger LOGGER = Logger.getLogger(MockClient.class.getName());
	private static final String LOGGER_PATH = "logs/" + MockClient.class.getName() + ".log";

	private MockUser user;

	private String ip;
	private int port;
	
	private ServerListener serverListener;
	
	public MockClient(MockUser user, String ip, int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		this.user = user;
		this.ip = ip;
		this.port = port;
	}
	
	public synchronized String getUsername() { return user.getUsername(); }
	
	public void connect() {
		new Thread(new Runnable() {
			public void run() {
				try (
					Socket socket = new Socket(ip, port);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				) {
					outputStream.writeUTF("CONNECT");
					outputStream.writeObject(MockClient.this);
					outputStream.flush();

					String response = inputStream.readUTF();
					
					if (response.equals("CONNECT_ACCEPTED")) {
						System.out.println("You are now connected to the server.");
						serverListener = new ServerListener();
						serverListener.start();
					}
					else if (response.equals("CONNECT_FAILED")) {
						String reason = inputStream.readUTF();
						System.out.println("Connection failed: " + reason);
					}
					else {
						System.out.println("Unknown response. This should not happen.");
					}
					
					response = inputStream.readUTF();
					
					if (response.equals("UPDATE")) {
						System.out.println("1");
						
						StringBuilder builder = new StringBuilder();
						builder.append(getUsername() + " says: ");
						
						while (true) {
							try {
								MockClient client = (MockClient) inputStream.readObject();
								System.out.println(client.getUsername());
								builder.append(client.getUsername() + ", ");
							}
							catch (EOFException ex) {
								break;
							}
						}
						
						System.out.println("2");
						LOGGER.log(Level.INFO, builder.toString());
					}
				}
				catch (IOException | ClassNotFoundException ex) {}
			}
		}).start();
	}
	
	public void disconnect() {
		new Thread(new Runnable() {
			public void run() {
				try (
					Socket socket = new Socket(ip, port);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				) {
					outputStream.writeUTF("DISCONNECT");
					outputStream.writeObject(MockClient.this);
					outputStream.flush();
					
					String response = inputStream.readUTF();
					
					if (response.equals("DISCONNECT_ACCEPTED")) {
						System.out.println("You are now disconnected from the server.");
						serverListener.interrupt();
						serverListener = null;
						LOGGER.log(Level.INFO, "Stopped listening.");
					}
					else if (response.equals("DISCONNECT_FAILED")) {
						System.out.println("Disconnection failed. Please try again!");
					}
					else {
						System.out.println("Unknown response. This should not happen.");
					}
				}
				catch (IOException ex) {}
			}
		}).start();
	}
	
	private class ServerListener extends Thread {
		
		@Override
		public void run() {
			LOGGER.log(Level.INFO, "Listening...");
			
			while (!Thread.interrupted()) {
				try (
					Socket socket = new Socket(ip, port);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				) {
					socket.setSoTimeout(10 * 1000);
					String response = inputStream.readUTF();
					
					if (response.equals("MESSAGE")) {
						Message message = (Message) inputStream.readObject();
						System.out.println(message.getText());
						
						LOGGER.log(Level.INFO, getUsername() + " got message: " + message.getText());
					}
				}
				catch (IOException | ClassNotFoundException ex) {}
			}
		}
	}
}
