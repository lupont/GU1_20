package gu20;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
	private User user;

	private String ip;
	private int port;
	
	public Client(User user, String ip, int port) {
		this.user = user;
		this.ip = ip;
		this.port = port;
//		new Connection(ip, port).start();
	}
	
	private class Connection extends Thread {
		private String ip;
		private int port;
		
		public Connection(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}
		
		@Override
		public void run() {
			System.out.println("Connection: run");
			try (
				Socket socket = new Socket(ip, port);
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			) {
				System.out.println("Client connecting...");
				outputStream.writeUTF("CONNECT");
				outputStream.writeObject(user);
				outputStream.flush();

				String response = inputStream.readUTF();
				
				if (response != null && response.length() > 0) {
					System.out.println(response);
				}
			}
			catch (IOException ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
		}
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
						System.out.println("Connection failed. Please try again!");
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
