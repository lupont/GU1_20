package gu20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import sources.Message;
import sources.User;



public class GUI extends JPanel {
	
	private User user;
	
	private GUIController controller;
	
	private JPanel titlePanel;
	private JPanel contactPanel;
	private MessagePanel messagePanel;

	public GUI() {
		this(new User("Test Testsson"));
	}
	public GUI(User user) {
		this.setPreferredSize(new Dimension(700, 300));
		setLayout(new BorderLayout());
		
		this.user = user;
		
		titlePanel = new TitlePanel(user.getUsername()); //Test username
		add(titlePanel, BorderLayout.NORTH);
		
		contactPanel = new ContactPanel(contactTest()); //Test contacts
//		contactPanel = new ContactPanel(null);
		add(contactPanel, BorderLayout.WEST);
		
		messagePanel = new MessagePanel(messagesTest());
		add(messagePanel, BorderLayout.CENTER);
		
		putInFrame();
	}
	
	public void setController(GUIController controller) {
		this.controller = controller;
	}
	
	public void newMessage(String sender, String strMessage) {
		Message message = new Message(sender, strMessage);
		messagePanel.addMessage(message);
		updateUI();
	}

	private void putInFrame() {
		JFrame window = new JFrame("Chatt Window");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(this);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	/**
	 * Title panel to be displayed on top of page.
	 * Contains program title and username.
	 * @author Alexander Libot
	 *
	 */
	@SuppressWarnings("serial")
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
		
		
		public ContactPanel(ArrayList<Contact> contacts) {
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			add(new PreviewPanel(contacts), BorderLayout.CENTER);
			add(new ContactButtonPanel(), BorderLayout.SOUTH);
		}
	}
	
	private class PreviewPanel extends JPanel implements MouseListener {
		private JPanel contactList;
		private ArrayList<Contact> contacts;
		
		public PreviewPanel(ArrayList<Contact> contacts) {
			this.contacts = contacts;
			setLayout(new BorderLayout());
			
			contactList = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.weighty = 1;
            contactList.add(new JPanel(), gbc);
			
            add(new JScrollPane(contactList));
			
            try {
			for (Contact contact : this.contacts) {
	            GridBagConstraints gbc2 = new GridBagConstraints();
	            gbc2.gridwidth = GridBagConstraints.REMAINDER;
	            gbc2.weightx = 1;
	            gbc2.fill = GridBagConstraints.HORIZONTAL;
				contactList.add(contact, gbc2, 0);
				
				contact.addMouseListener(this);
			}
            } catch (NullPointerException ex) {}
		}
		
		public void defaultContacts() {
			for (Contact contact : this.contacts) {
				contact.setBackground(null);
				contact.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			updateUI();
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			defaultContacts();
			
			JPanel clickedPanel = (Contact) e.getSource();	
			clickedPanel.setBorder(BorderFactory.createLoweredBevelBorder());
			
			controller.changeContact(clickedPanel.getName());
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ContactButtonPanel extends JPanel {
		private JButton btnNewChat;
		private JButton btnLogout;
		
		public ContactButtonPanel() {
			btnNewChat = new JButton("New Chat");
			btnLogout = new JButton("Logout");
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createHorizontalGlue());
			add(btnNewChat);
			add(btnLogout);
			add(Box.createHorizontalGlue());
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
		
		public String getName() {
			return lblName.getText();
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
			
			sb.append("<html>");
			
			for (int i = 0; i < inputString.length(); i++) {
				if (i < 25)
					sb.append(inputChars[i]);
				else if (i == 25) {
					sb.append("<br>");
				}
				else if (i > 25)
					sb.append(inputChars[i-1]);
			}
			sb.append("</html>");
			outputString = sb.toString();
			return outputString;
		}
	}
	
	private class MessagePanel extends JPanel {
		
		private MessagesPanel messagesPanel;
		
		public MessagePanel(ArrayList<Message> messages) {
			setLayout(new BorderLayout());
			
			messagesPanel = new MessagesPanel(messages);
			add(messagesPanel, BorderLayout.CENTER);
			add(new InputPanel(), BorderLayout.SOUTH);
		}
		
		public void addMessage(Message message) {
			messagesPanel.addMessage(message);
		}
	}
	
	private class MessagesPanel extends JPanel {
		private ArrayList<Message> messages;
		private JPanel messageList;
		
		public MessagesPanel(ArrayList<Message> messages) {
			this.messages = messages;
			setLayout(new BorderLayout());
			messageList = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.weighty = 1;
            messageList.add(new JPanel(), gbc);
			
            add(new JScrollPane(messageList));
			
			
			for (Message message : this.messages) {
	            GridBagConstraints gbc2 = new GridBagConstraints();
	            gbc2.gridwidth = GridBagConstraints.REMAINDER;
	            gbc2.weightx = 1;
	            gbc2.fill = GridBagConstraints.HORIZONTAL;
				messageList.add(message, gbc2, -1);
			}
		}
		
		public void addMessage(Message message) {
			messages.add(message);
			
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridwidth = GridBagConstraints.REMAINDER;
            gbc2.weightx = 1;
            gbc2.fill = GridBagConstraints.HORIZONTAL;
			messageList.add(message, gbc2, -1);
		}
	}
	
	private class InputPanel extends JPanel implements ActionListener {
		private JTextField tfInput;
		private JButton btnSend;
		
		public InputPanel() {
			tfInput = new JTextField();
			btnSend = new JButton("Send");
			btnSend.addActionListener(this);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(tfInput);
			add(btnSend);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = tfInput.getText();
			
			controller.sendMessage(message);
			
		}
	}
	
	private class Message extends JPanel {
		private JLabel lblSender;
		private JLabel lblMessage;
		
		private JPanel pnlSender;
		private JPanel pnlMessage;
		
		public Message(String sender, String message) {
			lblSender = new JLabel(sender);
			lblMessage = new JLabel(message);
			
			pnlSender = new JPanel();
			pnlSender.add(lblSender);
			
			pnlMessage = new JPanel();
			pnlMessage.add(lblMessage);
			
			pnlSender.setAlignmentX(Component.LEFT_ALIGNMENT);
			if (sender.equals(user.getUsername()))
				pnlSender.setBackground(Color.RED);
			else
				pnlSender.setBackground(Color.BLUE);

			pnlSender.setMaximumSize(new Dimension(200, 30));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			add(pnlSender);
			add(pnlMessage);
		}
	}

	//Test of ContactPanel and Contact
//	private ArrayList<Contact> contactTest() {
//		Contact contact1 = new Contact("Jim Halpert", "Bears, beats, Battlestar Gallactica", "10:12");
//		Contact contact2 = new Contact("Stanley", "Are you out of your god damned mind?", "igår");
//		Contact contact3 = new Contact("Michael Scott", "You miss 100% of the shots you don't take", "2019-02-20");
//		
//		ArrayList<Contact> testContacts = new ArrayList<Contact>();
//		testContacts.add(contact1);
//		testContacts.add(contact2);
//		testContacts.add(contact3);
//		
//		return testContacts;
//	}
	
	private ArrayList<Contact> contactTest() {
		User tempUser;
		sources.Message tempMessage;
		Calendar tempDate;
		
		tempUser = user.getContacts().get(0);
		tempMessage = tempUser.getLatestMessage();
		tempDate = tempMessage.getDate();
		Contact contact1 = new Contact(tempUser.getUsername(), tempMessage.getMessage(), formatDate(tempDate));
		
		tempUser = user.getContacts().get(1);
		tempMessage = tempUser.getLatestMessage();
		tempDate = tempMessage.getDate();
		Contact contact2 = new Contact(tempUser.getUsername(), tempMessage.getMessage(), formatDate(tempDate));
		
		tempUser = user.getContacts().get(2);
		tempMessage = tempUser.getLatestMessage();
		tempDate = tempMessage.getDate();
		Contact contact3 = new Contact(tempUser.getUsername(), tempMessage.getMessage(), formatDate(tempDate));
		
		ArrayList<Contact> testContacts = new ArrayList<Contact>();
		testContacts.add(contact1);
		testContacts.add(contact2);
		testContacts.add(contact3);
		return testContacts;
	}
	
	private String formatDate(Calendar date) {
		String strDate, strCurrentDate;
		
		DateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd");
		
		Calendar currentDate = Calendar.getInstance();

		strDate = formatDate.format(date.getTime());
		strCurrentDate = formatDate.format(currentDate.getTime());
		
		if (strDate.equals(strCurrentDate))
			return "Today";
		
		return strDate;
	}
	
	private ArrayList<Message> messagesTest() {
		Message message1 = new Message("Jim Halpert", "What kind of bear is best");
		Message message2 = new Message("Dwight Shrute", "That's a ridiculous question");
		Message message3 = new Message("Jim Halpert", "False, black bear");
		Message message4 = new Message("Dwight Shrute", "That's debateable, there's basically two schools of thougt");
		Message message5 = new Message("Jim Halpert", "Fact: Bears eats beats");
		Message message6 = new Message("Jim Halpert", "Bears, beats, Battlestar Gallactica");
		
		ArrayList<Message> testMessages = new ArrayList<Message>();
		testMessages.add(message1);
		testMessages.add(message2);
		testMessages.add(message3);
		testMessages.add(message4);
		testMessages.add(message5);
		testMessages.add(message6);
		
		return testMessages;
	}
	
}



