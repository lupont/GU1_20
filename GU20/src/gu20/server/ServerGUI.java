package gu20.server;

import java.util.List;

import javax.swing.JPanel;

import gu20.Helpers;

public class ServerGUI extends JPanel {
	
	public ServerGUI() {
		String path = "logs/" + Server.class.getName() + ".log";
		List<String> loggerLines = Helpers.readFile(path);
		
		for (String line : loggerLines) {
			System.out.println("READ: " + line);
		}
	}
	
	public static void main(String[] args) {
		new ServerGUI();
	}
}
