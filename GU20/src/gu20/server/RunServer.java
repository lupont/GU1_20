package gu20.server;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * A class for running the server.
 * @author Pontus Laos
 *
 */
public class RunServer {
	private static Server server;
	
	/**
	 * Starts a server on port 12345, as well as displays a ServerPanel.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		startServer(12345);
		runGUI();
	}
	
	/**
	 * Constructs a new Server instance with the given port.
	 * @param port The port on which the server should listen.
	 */
	private static void startServer(int port) {
		server = new Server(port);
	}
	
	/**
	 * Creates and displays a JFrame containing a ServerPanel.
	 */
	private static void runGUI() {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(640, 640));
		
		frame.add(new ServerPanel());
		frame.pack();
		frame.setVisible(true);
	}
}
