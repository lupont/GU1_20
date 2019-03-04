package gu20;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

import p2.MessageProducer;

public class Client {
	
	private User user;
	private String ip;
	private int port;
	
	
	public Client(User user, String ip, int port) {
		this.user = user; //copy?
		this.ip = ip;
		this.port = port;
	}
	
	public void connect() {
		ClientThread cT = new ClientThread();
		cT.establishConnection(ip, port);
		
	}
	
	public void disconnect() {
		
	}
	
	public void sendMessage(Message message) {
		
	}
	
	
	
	private class ClientThread extends Thread {
		private Message message;
		private Socket socket;

		public ClientThread() {
			
		}
		
		public void EstablishConnection(String ip, int port) {
			try {
				socket = new Socket(ip, port);
				start();
				
			}catch (IOException e) {
				System.err.println(e);
			}
			
		}

		public void run() {
			System.out.println("Connecting to server");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(message);
				//if(message == DisconnectMessage) {
				socket.close();
				//}
			} catch (IOException e) {
				System.err.println(e);
			}

		}
	}

}
