package testClasses;

/**
 * A class for testing the system.
 * @author Alexander Libot
 *
 */
public class TestSystem {
	/**
	 * Starts TestServer once, and TestClient twice.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		TestServer.main(null);
		
		TestClient.main(null);
		TestClient.main(null);
	}
}