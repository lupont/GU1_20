package gu20.server;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) {
		MockUser user = new MockUser("ogardogar", null);
		MockClient client = new MockClient(user, "localhost", 12345);
		
		client.connect();
	}
}
