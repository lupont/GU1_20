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
	private static final Logger LOGGER = Logger.getLogger(MockClient.class.getName());
	private static final String LOGGER_PATH = "logs/" + MockClient.class.getName() + ".log";
	
	private static final long serialVersionUID = 235235245L;

	private MockUser user;

	private String ip;
	private int port;
	
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	public MockClient(MockUser user, String ip, int port) {
		Helpers.addFileHandler(LOGGER, LOGGER_PATH);
		
		this.user = user;
		this.ip = ip;
		this.port = port;
		
		System.out.println("Client constructed.");
	}
	
	public String getUsername() { return user.getUsername(); }
	
	public void connect() {
		System.out.println("Client connecting...");
		try {
			socket = new Socket(ip, port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			
			System.out.println("Created socket and streams.");
			
			outputStream.writeUTF("CONNECT");
			outputStream.writeObject(this);
			outputStream.flush();
			
			System.out.println("Client sent CONNECT request...");

			String response = inputStream.readUTF();
			
			if (response.equals("CONNECT_ACCEPTED")) {
				System.out.println("You are now connected to the server.");
			}
			else if (response.equals("CONNECT_FAILED")) {
				String reason = inputStream.readUTF();
				System.out.println("Connection failed: " + reason);
			}
			else {
				System.out.println("Unknown response. This should not happen.");
			}
		}
		catch (IOException ex) {}
	}
	
	public void disconnect() {
		try {			
			outputStream.writeUTF("DISCONNECT");
			outputStream.writeObject(this);
			outputStream.flush();
			
			String response = inputStream.readUTF();
			
			if (response.equals("DISCONNECT_ACCEPTED")) {
				System.out.println("You are now disconnected from the server.");
				
				outputStream.close();
				inputStream.close();
				socket.close();

				socket = null;
				outputStream = null;
				inputStream = null;
				return;
			}
			else if (response.equals("DISCONNECT_FAILED")) {
				System.out.println("Disconnection failed. Please try again!");
			}
		}
		catch (IOException ex) {}
	}
}
