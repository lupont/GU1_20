package gu20.server;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gu20.Helpers;

public class MockServerPanel extends JPanel {
	private JTextArea taLog;
	
	public MockServerPanel() {
		setLayout(new BorderLayout());
		
		try {
			add(new JLabel("IP: " + Helpers.getFirstNonLoopbackIPv4Address()), BorderLayout.NORTH);
		}
		catch (Exception ex) {}
		
		taLog = new JTextArea();
		taLog.setEditable(false);
		
		add(new JScrollPane(taLog), BorderLayout.CENTER);
		
		new Thread(new Runnable() {
			public void run() {
				try (final RandomAccessFile file = new RandomAccessFile(Server.LOGGER_PATH, "r")) {
					while (true) {
						String line = file.readLine();
						
						if (line != null) {
							taLog.append(line + "\n");
							taLog.setCaretPosition(taLog.getDocument().getLength());
						}
						else {
							Thread.sleep(1000);
						}
					}
				}
				catch (IOException | InterruptedException ex) {}
			}
		}).start();
	}
}
