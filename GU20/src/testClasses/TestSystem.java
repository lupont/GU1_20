package testClasses;

import java.util.ArrayList;

import gu20.gui.GUIController;
import gu20.server.RunServer;

public class TestSystem {
	public static void main(String[] args) {
		
		RunServer.main(args);
		
//		To test with many users
//		ArrayList<String> users = new ArrayList<>();
//		
//		String temp = "user";
//		
//		for (int i = 0; i < 20; i++) {
//			users.add(new String(temp + i));
//		}
//		
//		for (String user : users) {
//			new GUIController(user, "localhost");
//		}

//		new GUIController("pontus", "localhost");
//		new GUIController();
//		new GUIController("oskar", "localhost");
//		new GUIController("pärla", "localhost");
		
		
//		String[] names = { "alex", "pontus", "oskar", "p�rla", "racquel", "sara", "foobar", "johndoe", "baz", "johnsmith" };
//
//		for (int i = 0; i < 10; i++) {
//			new GUIController(names[i], "localhost");
//		}
	}
}