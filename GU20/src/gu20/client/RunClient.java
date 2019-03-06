package gu20.client;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) throws InterruptedException {
		MockUser user = new MockUser("ogardogar221", null);
		MockClient client = new MockClient(user, "localhost", 12345);
		
		Thread.sleep(7000);
		client.disconnect();
	}
}
