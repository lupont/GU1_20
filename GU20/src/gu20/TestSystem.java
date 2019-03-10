package gu20;

import gu20.server.RunServer;

public class TestSystem {
	public static void main(String[] args) {
		RunServer.main(args);
		
		new GUIController("alex", "localhost");
		new GUIController("pontus", "localhost");
	}
}