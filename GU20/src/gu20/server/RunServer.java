package gu20.server;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class RunServer {
	private static Server server;
	
	public static void main(String[] args) {
		startServer(12345);
		runGUI();
	}
	
	private static void startServer(int port) {
		server = new Server(port);
	}
	
	private static void runGUI() {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(640, 640));
		
		frame.add(new MockServerPanel(server));
		frame.pack();
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	server.close();
            }
        });
	}
}
