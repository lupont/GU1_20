package gu20.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gu20.GUIController;
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
		
		JPanel pnlTimePickers = new JPanel();
		pnlTimePickers.setLayout(new BoxLayout(pnlTimePickers, BoxLayout.X_AXIS));
		
		JTextField tfStartTime = new JTextField();
		tfStartTime.setToolTipText("2019-03-12 07:58:47.816");
		JTextField tfEndTime = new JTextField();
		tfEndTime.setToolTipText("YYYY-MM-dd HH:mm:ss.SSS");
		
		JButton btnFilterByTime = new JButton("Filter");
		btnFilterByTime.addActionListener(event -> {
			String start = tfStartTime.getText();
			String end = tfEndTime.getText();
			List<String> lines = Helpers.readLogBetween(Server.LOGGER_PATH, start, end);
			for (String line : lines) {
				System.out.println(line);
			}
		});
		
		pnlTimePickers.add(tfStartTime);
		pnlTimePickers.add(tfEndTime);
		pnlTimePickers.add(btnFilterByTime);
		
		add(pnlTimePickers, BorderLayout.NORTH);

		JButton btnAddClient = new JButton("Add client");
		btnAddClient.addActionListener(event -> new GUIController());
		add(btnAddClient, BorderLayout.SOUTH);
		
		taLog = new JTextArea();
		taLog.setEditable(false);
		
		add(new JScrollPane(taLog), BorderLayout.CENTER);
		
		new Thread(() -> {
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
		}).start();
	}
}
