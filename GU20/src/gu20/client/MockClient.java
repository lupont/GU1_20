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
public class MockClient {
	private static final Logger LOGGER = Logger.getLogger(MockClient.class.getName());
	private static final String LOGGER_PATH = "logs/" + MockClient.class.getName() + ".log";
	
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
		
		try {
			socket = new Socket(ip, port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			new Worker().start();
		}
		catch (IOException ex) {
			
		}
	}
	
	public void disconnect() throws IOException {
		if (socket != null) {
			socket.close();
			System.out.println(user.getUsername() + " disconnected.");
		}
	}
	
	private class Worker extends Thread {
		
		@Override
		public void run() {
			try {
				outputStream.writeObject(user);
				System.out.println("You are now connected to the server.");
				
				while (true) {
				}
			}
			catch (IOException ex) {
				System.out.println("Error: " + ex.getMessage());
			}
			
			try {
				outputStream.writeUTF("DISCONNECT");
				outputStream.flush();
				
				disconnect();
			}
			catch (IOException ex) {
				System.out.println("Error disconnecting.");
			}
		}
	}
}
