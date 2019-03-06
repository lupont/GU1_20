package gu20.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gu20.Helpers;

public class MockServerPanel extends JPanel {
	private JTextArea taLog;
	private Server server;
	
	public MockServerPanel(Server server) {
		this.server = server;
		
		setLayout(new BorderLayout());
		
		try {
			add(new JLabel("IP: " + Helpers.getFirstNonLoopbackIPv4Address()), BorderLayout.NORTH);
		}
		catch (Exception ex) {}
		
		JButton button = new JButton("Send message");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
//				server.mockMessageSending();
			}
		});
		add(button, BorderLayout.SOUTH);
		
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
