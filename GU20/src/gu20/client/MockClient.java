package gu20.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import gu20.MockUser;

/**
 * A mock of how a client could be implemented, used for testing the Server.
 * @author lupont
 *
 */
public class MockClient {
	private MockUser user;

	private String ip;
	private int port;
	
	public MockClient(MockUser user, String ip, int port) {
		this.user = user;
		this.ip = ip;
		this.port = port;
	}
	
	public void connect() {
		new Thread(new Runnable() {
			public void run() {
				try (
					Socket socket = new Socket(ip, port);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				) {
					outputStream.writeUTF("CONNECT");
					outputStream.writeObject(user);
					outputStream.flush();

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
					outputStream.writeObject(user);
					outputStream.flush();
					
					String response = inputStream.readUTF();
					
					if (response.equals("DISCONNECT_ACCEPTED")) {
						System.out.println("You are now disconnected from the server.");
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
}
