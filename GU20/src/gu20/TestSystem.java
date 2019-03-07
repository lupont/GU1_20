package gu20;

import java.util.HashMap;
import java.util.Map;

import gu20.client.MockClient;
import gu20.server.RunServer;

public class TestSystem {
	public static void main(String[] args) {
		RunServer.main(args);
		
		new GUIController();
		new GUIController();
	}
}