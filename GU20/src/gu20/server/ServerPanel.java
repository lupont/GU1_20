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

/**
 * A simple GUI for the server, fetching from the server's log file.
 * @author Oskar Molander, Pontus Laos
 *
 */
public class ServerPanel extends JPanel {
	private JTextArea taLog;

	/**
	 * Constructs a new panel and begins fetching from the log file.
	 */
	public ServerPanel() {		
		setLayout(new BorderLayout());
		
		JPanel pnlTimePickers = new JPanel();
		pnlTimePickers.setLayout(new BoxLayout(pnlTimePickers, BoxLayout.X_AXIS));
		
		try {
			pnlTimePickers.add(new JLabel("IP: " + Helpers.getFirstNonLoopbackIPv4Address()));
		}
		catch (Exception ex) {}
		
		JTextField tfStartTime = new JTextField();
		tfStartTime.setToolTipText("2019-03-12 07:58:47.816");
		JTextField tfEndTime = new JTextField();
		tfEndTime.setToolTipText("YYYY-MM-dd HH:mm:ss.SSS");
		
		JButton btnFilterByTime = new JButton("Filter");
//		btnFilterByTime.addActionListener(event -> {
//			String start = tfStartTime.getText();
//			String end = tfEndTime.getText();
//			List<String> lines = Helpers.readLogBetween(Server.LOGGER_PATH, start, end);
//			for (String line : lines) {
//				System.out.println(line);
//			}
//		});
		
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
						String utf8 = new String(line.getBytes("ISO-8859-1"), "UTF-8");
						taLog.append(utf8 + "\n");
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
