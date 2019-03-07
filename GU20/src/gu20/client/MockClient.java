package gu20.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import gu20.GUIController;
import gu20.Helpers;
import gu20.MockUser;

/**
 * A mock of how a client could be implemented, used for testing the Server.
 * @author lupont
 *
 */
public class MockClient implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(MockClient.class.getName());
	private static final String LOGGER_PATH = "logs/" + MockClient.class.getName() + ".log";
	
	private MockUser user;

	private String ip;
	private int port;

	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private Thread client = new Thread(this);
	
	public MockClient(MockUser user, String ip, int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		this.user = user;
		this.ip = ip;
		this.port = port;
		
		client.start();
	}
	
	@Override
	public void run() {
		connect();
	}
	
	public String getUsername() { return user.getUsername(); }
	
	private void connect() {
		try {
			socket = new Socket(ip, port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
						
			outputStream.writeUTF("CONNECT");
			outputStream.writeObject(user);
			outputStream.flush();
		}
		catch (IOException ex) {
			System.out.println("IO exception lol");
			ex.printStackTrace();
		}
		finally {
			handleCommunication();
		}
	}
	
	private void handleCommunication() {
		while (true) {
			try {
				listenForUpdate();
			}
			catch (IOException ex) {
				return;
			}
			catch (ClassNotFoundException ex) {}
		}
	}
	
	private void listenForUpdate() throws IOException, ClassNotFoundException {
		if (inputStream.available() <= 0) {
			return;
		}

		System.out.println("Reading");
		String header = inputStream.readUTF();
		System.out.println("Read: " + header);
		
		if (header.equals("UPDATE")) {
			Object obj = inputStream.readObject();
			MockUser[] users = (MockUser[]) obj;
			System.out.println("Got users: " + Helpers.joinArray(users, ", "));
		}
	}
	
	public void disconnect() {
		if (socket.isClosed()) {
			System.out.println("Client is already disconnected.");
			return;
		}
		
		try {			
			outputStream.writeUTF("DISCONNECT");
			outputStream.writeObject(user);
			outputStream.flush();
		}
		catch (IOException ex) {
			System.out.println("Exception on disconnect: " + ex.getMessage());
		}
		finally {
			try { 
				if (socket != null) {
					socket.close(); 
					System.out.println("Disconnected from the server.");
				}
			}
			catch (IOException ex) {}
		}
	}
}
