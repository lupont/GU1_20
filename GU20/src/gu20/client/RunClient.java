package gu20.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) throws InterruptedException {
		Map<String, String> addresses = new HashMap<>();
		addresses.put("local", "localhost");
		
		MockUser user = new MockUser("123", null);
		MockClient client = new MockClient(user, addresses.get("local"), 12345);
		
		Thread.sleep(2000);
		new MockClient(new MockUser("hellothere", null), addresses.get("local"), 12345);
		
		Thread.sleep(7000);
		
		client.disconnect();
		
		MockClient c1 = new MockClient(new MockUser("abc", null), addresses.get("local"), 12345);
		MockClient c2 = new MockClient(new MockUser("def", null), addresses.get("local"), 12345);
		
		Thread.sleep(3000);
		c2.disconnect();
		c1.disconnect();
	}
}
