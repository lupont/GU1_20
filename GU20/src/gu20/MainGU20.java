package gu20;

public class MainGU20 {
	public static void main(String[] args) {
		User user = new User("ogardogar", null);
		Client client = new Client(user, "localhost", 12345);
		
		client.connect();
		
		try {
			Thread.sleep(2000);
			
			client.disconnect();
		}
		catch (Exception ex) {}
	}
}
