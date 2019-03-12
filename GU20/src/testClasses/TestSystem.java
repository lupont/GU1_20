package testClasses;

import gu20.GUIController;
import gu20.server.RunServer;

public class TestSystem {
	public static void main(String[] args) {
		RunServer.main(args);

		new GUIController("alex", "localhost");
		new GUIController("pontus", "localhost");
		new GUIController("oskar", "localhost");
		new GUIController("p√§rla", "localhost");
		
		
//		String[] names = { "alex", "pontus", "oskar", "p‰rla", "racquel", "sara", "foobar", "johndoe", "baz", "johnsmith" };
//
//		for (int i = 0; i < 10; i++) {
//			new GUIController(names[i], "localhost");
//		}
	}
}