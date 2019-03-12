package gu20;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RecipientsPanel {
	
	private JFrame frame;
	private String[] recipients;
	private Point inputPoint; //set location relative to this

	public RecipientsPanel(String[] recipients, Point inputPoint) {
		this.recipients = recipients;
		this.inputPoint = inputPoint;
		
		putInFrame(initUI());
	}
	
	private JPanel initUI() {
		JPanel panel = new JPanel();
		JLabel lblTitle = new JLabel("Recipients:");
		Font titleFont = new Font("Helvetica", Font.BOLD, 16);
		lblTitle.setFont(titleFont);
		JLabel[] lblRecipients = new JLabel[recipients.length];
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//		panel.setPreferredSize(new Dimension(100,100));
		
		panel.add(lblTitle);
		
		for (int index = 0; index < lblRecipients.length; index++) {
			if (index > 9) {
				int moreUsers = lblRecipients.length-10;
				panel.add(new JLabel("and " + moreUsers + " more..."));
				break;
			}
			lblRecipients[index] = new JLabel(recipients[index]);
			panel.add(lblRecipients[index]);
		}
		return panel;
	}
	
	private void putInFrame(JPanel panel) {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setLocation(inputPoint);
		frame.setVisible(true);
	}
	
	public void dispose() {
		frame.dispose();
	}
}