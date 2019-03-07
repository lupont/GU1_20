package gu20.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import gu20.Message;
import gu20.User;

public class Client {

	private User user;
	private String ip;
	private int port;
	private ArrayList <Client> contacts;

	public Client(User user, String ip, int port) {
		this.user = user;
		this.ip = ip;
		this.port = port;
	}

	public void connect() {
		ClientThread cT = new ClientThread();
		cT.establishConnection(ip, port);

	}

	public void disconnect() {
		ClientThread cT = new ClientThread();
		cT.disconnect(ip, port);
	}

	public void sendMessage(ArrayList<Client> recievers, Message message) {
		

	}
	
	
	public Message getMessage(Message message) {
		return null;
	}
	
	
	public void addContact(Client contact) {
		contacts.add(contact);
	}
	
	public ArrayList<Client> getContacts(){
		return contacts;
	}
	
	

	private class ClientThread extends Thread {
		private Message message;
		private Socket socket;

		public ClientThread() {

		}
//måste fixa
		public void establishConnection(String ip, int port) {
			try {
				socket = new Socket(ip, port);
				start();

			} catch (IOException e) {
				System.err.println(e);
			}

		}

		
		//ska fixa
		public void disconnect(String ip, int port) {

			try (Socket socket = new Socket(ip, port);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
				outputStream.writeUTF("DISCONNECT");
				outputStream.writeObject(user);
				outputStream.flush();

				String response = inputStream.readUTF();

				if (response.equals("DISCONNECT_ACCEPTED")) {
					System.out.println("You are now disconnected from the server.");
				} else if (response.equals("DISCONNECT_FAILED")) {
					System.out.println("Disconnection failed. Please try again!");
				} else {
					System.out.println("Unknown response. This should not happen.");
				}
			} catch (IOException ex) {

			}

		}

		public void run() {
			System.out.println("Connecting to server");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(message);
				// if(message == DisconnectMessage) {
				socket.close();
				// }
			} catch (IOException e) {
				System.err.println(e);
			}

		}
	}

}
