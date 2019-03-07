package gu20.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import gu20.GUIController;
import gu20.Helpers;
import gu20.Message;
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
	
	private GUIController guiC;

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
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername() { return user.getUsername(); }
	
	private void connect() throws IOException {
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
			listen();
		}
	}
	
	private void listen() throws IOException {
		//might fix if something breaks
//		if (inputStream.available() <= 0) {
//			return;
//		}
		while (true) {
			try {
				
				String header = inputStream.readUTF();
				if (header.equals("UPDATE")) {
					handleUpdate();
				}
				else if(header.equals("MESSAGE")) {
					handleMessage();
				}
			}
			catch (IOException ex) {
//				System.out.println("io exception mockclient");
				continue;
			}
			catch (ClassNotFoundException ex) {}
		}
	}
	
	private void handleUpdate() throws IOException, ClassNotFoundException {
		Object obj = inputStream.readObject();
		MockUser user = (MockUser) obj;
		String action = inputStream.readUTF();
		obj = inputStream.readObject();
		
		MockUser[] users = (MockUser[]) obj;
		System.out.println(user + " got users: " + Helpers.joinArray(users, ", "));
		guiC.onlineUsers(users);
	}
	
	private void handleMessage() throws ClassNotFoundException, IOException {
		Object obj = inputStream.readObject();
		Message message = (Message) obj;
		System.out.println(user + " recieved message from: " + message.getSender() + " : " + message.getText());
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
					System.out.println(user + " disconnected from the server.");
				}
			}
			catch (IOException ex) {}
		}
	}
	
	public void sendMessage(Message message) {
		try {
			outputStream.writeUTF("MESSAGE");
			outputStream.writeObject(message);
			outputStream.flush();
		} 
		catch (IOException e) {
			System.out.println("Exception send message (client)");
		}
	}
	
	public void setGUIController(GUIController guiC) {
		this.guiC = guiC;
	}
}
