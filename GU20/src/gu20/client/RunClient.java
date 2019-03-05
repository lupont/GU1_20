package gu20.client;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) {
		MockUser user = new MockUser("ogardogar2", null);
		MockClient client = new MockClient(user, "localhost", 12345);
		
		client.connect();
	}
}
