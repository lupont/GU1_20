package gu20.client;

import java.io.IOException;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) throws IOException, InterruptedException {
		MockUser user = new MockUser("ogardogar32222", null);
		MockClient client = new MockClient(user, "localhost", 12345);
		
		Thread.sleep(2000);
		client.disconnect();
	}
}
