package gu20.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gu20.Message;
import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) throws InterruptedException {
		Map<String, String> addresses = new HashMap<>();
		addresses.put("local", "10.2.4.70");

		MockUser[] ul = { new MockUser("client12", null), new MockUser("client23", null)};
		MockUser user = new MockUser("1234", null);
		
		MockClient client = new MockClient(user, "10.2.14.136", 12345);
		Thread.sleep(1000);
		MockClient c1 = new MockClient(ul[0], "10.2.14.136", 12345);
		Thread.sleep(1000);
		MockClient c2 = new MockClient(ul[1], "10.2.14.136", 12345);
		Thread.sleep(1000);

		Message m = new Message(user, ul,"This is a message", null);
		
		Thread.sleep(1000);

		client.sendMessage(m);
	}
}
