package gu20.server;

import java.awt.Dimension;

import javax.swing.JFrame;

public class RunServer {
	public static void main(String[] args) {
		startServer(12345);
		runGUI();
	}
	
	private static void startServer(int port) {
		new Server(port);
	}
	
	private static void runGUI() {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(640, 640));
		
		frame.add(new MockServerPanel());
		
		frame.pack();
		frame.setVisible(true);
	}
}
