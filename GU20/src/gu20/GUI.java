package gu20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class GUI extends JPanel implements ActionListener {
	private JButton btnAdd = new JButton("Add");
	private JButton btnSend = new JButton("Send");
	private Font fontButtons = new Font("Sanserif", Font.PLAIN, 21);
	private JTextField tfWriteMessage = new JTextField();
	
	private JPanel titlePanel;

	
	public GUI() {
		this.setPreferredSize(new Dimension(500, 300));
		setLayout(new BorderLayout());
		
		titlePanel = new TitlePanel("Test Testsson"); //Test username
		add(titlePanel, BorderLayout.NORTH);
		
		contactTest();
		
		btnAdd.setPreferredSize(new Dimension(80, 20));
		btnAdd.setFont(fontButtons);
        
		btnSend.setPreferredSize(new Dimension(80, 20));
		btnSend.setFont(fontButtons);
		
		tfWriteMessage.setPreferredSize(new Dimension(200, 50));
		
		
		
		
		
		
		
		

		btnAdd.addActionListener(this);
		btnSend.addActionListener(this);
		
		add(btnAdd);
		add(btnSend);
		add(tfWriteMessage);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		
	}

	public static void main(String[] args) {
		JFrame window = new JFrame("Chatt Window");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(new GUI());
		window.pack();
		window.setVisible(true);
	}
	
	/**
	 * Title panel to be displayed on top of page.
	 * Contains program title and username.
	 * @author Alexander Libot
	 *
	 */
	private class TitlePanel extends JPanel {
		private JLabel lblTitle;
		private JLabel lblUser;
		
		/**
		 * 
		 * @param username Username of person logged in
		 */
		public TitlePanel(String username) {
			lblTitle = new JLabel("Chat Program");
			lblUser = new JLabel(username);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(lblTitle);
			add(Box.createHorizontalGlue());
			add(lblUser);
			
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		}
	}
	
	/**
	 * Panel containing contacts, including their latest messages
	 * @author Alexander Libot
	 *
	 */
	private class ContactPanel extends JPanel {
		private ArrayList<Contact> contacts;
		
		public ContactPanel(ArrayList<Contact> contacts) {
			this.contacts = contacts;
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			for (Contact contact : contacts) {
				add(contact);
			}
		}
	}
	
	/**
	 * Test class for ContactPanel, to be replaced later.
	 * @author Alexander Libot
	 */
	private class Contact extends JPanel {
		private JLabel lblName;
		private JLabel lblLatestMessage;
		private JLabel lblTime;
		
		private JPanel pnlContactHead;
		
		public Contact(String name, String latestMessage, String time) {
			lblName = new JLabel(name);
			lblLatestMessage = new JLabel(formatPreview(latestMessage));
			lblTime = new JLabel(time);
			
			pnlContactHead = new JPanel();
			pnlContactHead.setLayout(new BoxLayout(pnlContactHead, BoxLayout.X_AXIS));
			pnlContactHead.add(lblName);
			pnlContactHead.add(Box.createHorizontalGlue());
			pnlContactHead.add(lblTime);
			
			setLayout(new BorderLayout());
			add(pnlContactHead, BorderLayout.NORTH);
			add(lblLatestMessage, BorderLayout.CENTER);	
			
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			setMaximumSize(new Dimension(200, 100));
		}
		
		/**
		 * Formats an input string to have right amount of characters per row and number of rows
		 * @param inputString
		 * @return
		 */
		private String formatPreview(String inputString) {
			char[] inputChars = inputString.toCharArray();
			StringBuilder sb = new StringBuilder();
			String outputString = null;
			
			for (int i = 0; i < inputString.length(); i++) {
				if (i < 25)
					sb.append(inputChars[i]);
				else if (i == 25)
					sb.append('\n');
				else if (i > 25)
					sb.append(inputChars[i-1]);
			}
			outputString = sb.toString();
			return outputString;
		}
	}
	
	private class MessagePanel extends JPanel {
		
		public MessagePanel() {
			setLayout(new BorderLayout());
		}
	}
	
	private class MessagesPanel extends JPanel {
		
	}
	
	private class InputPanel extends JPanel {
		private JTextField tfInput;
		private JButton btnSend;
		
		public InputPanel() {
			tfInput = new JTextField();
			btnSend = new JButton("Send");
			
		}
	}

	//Test of ContactPanel and Contact
	public void contactTest() {
		Contact contact1 = new Contact("Jim Halpert", "Bears, beats, Battlestar Gallactica", "10:12");
		Contact contact2 = new Contact("Stanley", "Are you out of your god damned mind?", "igår");
		Contact contact3 = new Contact("Michael Scott", "You miss 100% of the shots you don't take", "2019-02-20");
		
		ArrayList<Contact> testContacts = new ArrayList<Contact>();
		testContacts.add(contact1);
		testContacts.add(contact2);
		testContacts.add(contact3);
		
		ContactPanel cp = new ContactPanel(testContacts);
		
		this.add(cp, BorderLayout.WEST);
	}

}



