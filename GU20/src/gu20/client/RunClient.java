package gu20.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gu20.Message;
import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) throws InterruptedException {
		Map<String, String> addresses = new HashMap<>();
//		addresses.put("local", "10.2.4.70");
		addresses.put("local", "localhost");
		MockUser[] ul = { new MockUser("client1", null), new MockUser("client2", null)};
		
		MockUser user = new MockUser("123", null);
		
		MockClient client = new MockClient(user, "localhost", 12345);
		MockClient c1 = new MockClient(ul[0], "localhost", 12345);
		MockClient c2 = new MockClient(ul[1], "localhost", 12345);
		Message m = new Message(user, ul,"This is a message", null);
		
		Thread.sleep(1000);
		client.sendMessage(m);
		

	}
}
