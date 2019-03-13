package gu20.gui;

import java.awt.Font;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel for showing the recipients of a message
 * @author Alexander Libot
 *
 */
public class RecipientsPanel {
	
	private JFrame frame;
	private String[] recipients;
	private Point inputPoint; //set location relative to this

	/**
	 * Creates a new Recipients panel
	 * @param recipients A list of recipients to add
	 * @param inputPoint Displays the frame from this point
	 */
	public RecipientsPanel(String[] recipients, Point inputPoint) {
		this.recipients = recipients;
		this.inputPoint = inputPoint;
		
		putInFrame(initUI());
	}
	
	/**
	 * Initializes the UI.
	 * Adds a titletext and list of recipients.
	 * If recipients are more than ten, a "and x more" label is added to the bottom
	 * @return The initialized UI
	 */
	private JPanel initUI() {
		JPanel panel = new JPanel();
		JLabel lblTitle = new JLabel("Recipients:");
		Font titleFont = new Font("Helvetica", Font.BOLD, 16);
		lblTitle.setFont(titleFont);
		JLabel[] lblRecipients = new JLabel[recipients.length];
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
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
	
	/**
	 * Creates a new frame, puts a panel in it and make it visible.
	 * Disposes the frame on close.
	 * @param panel
	 */
	private void putInFrame(JPanel panel) {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setLocation(inputPoint);
		frame.setVisible(true);
	}
	
	/**
	 * Disposes the frame.
	 */
	public void dispose() {
		frame.dispose();
	}
}