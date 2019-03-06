package gu20.client;

import java.io.IOException;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) throws InterruptedException {
		MockUser user = new MockUser("ogardoga1r221", null);
		MockClient client = new MockClient(user, "localhost", 12345);
		
		Thread.sleep(7000);
		client.disconnect();
	}
}
