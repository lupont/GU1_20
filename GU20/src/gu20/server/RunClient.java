package gu20.server;

import gu20.MockUser;

public class RunClient {
	public static void main(String[] args) {
		MockUser user = new MockUser("lol2", null);
		MockClient client = new MockClient(user, "localhost", 12345);
		
		client.connect();
		
//		try {
//			Thread.sleep(5);
//			client.disconnect();
//		}
//		catch (Exception ex) {}
	}
}
