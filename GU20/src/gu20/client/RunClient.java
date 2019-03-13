package gu20.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gu20.entities.Message;
import gu20.entities.User;

public class RunClient {
	public static void main(String[] args) throws InterruptedException {
		Map<String, String> addresses = new HashMap<>();
		addresses.put("local", "10.2.4.70");

		User[] ul = { new User("client12", null), new User("client23", null)};
		User user = new User("1234", null);
		
		Client client = new Client(user, "10.2.14.136", 12345);
		Thread.sleep(1000);
		Client c1 = new Client(ul[0], "10.2.14.136", 12345);
		Thread.sleep(1000);
		Client c2 = new Client(ul[1], "10.2.14.136", 12345);
		Thread.sleep(1000);

		Message m = new Message(user, ul,"This is a message", null);
		
		Thread.sleep(1000);

		client.sendMessage(m);
	}
}
