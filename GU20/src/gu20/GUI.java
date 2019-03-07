package gu20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A mock for how gui might be implemented, user for testing GUIController
 * @author Alexander Libot
 *
 */
@SuppressWarnings("serial")
public class GUI extends JPanel {
	
	private JFrame frame;
	
	private GUIController controller;
	
	private TitlePanel titlePanel;
	private JPanel contactPanel;
	private MessagePanel messagePanel;
	private MessagesPanel messagesPanel;
	private InputPanel inputPanel;
	
	private OnlinePanel onlinePanel;
	
	private String username;
	private String selectedUser;

	public GUI(GUIController guiC) {
		this("Test Testsson", guiC);
	}
	
	public GUI(String username, GUIController guiC) {
		this.username = username;
		this.controller = guiC;
		
		this.setPreferredSize(new Dimension(700, 300));
		setLayout(new BorderLayout());
		
		titlePanel = new TitlePanel(username); //Test username
		add(titlePanel, BorderLayout.NORTH);
		
		contactPanel = new UsersPanel(null); //Test contacts
		add(contactPanel, BorderLayout.WEST);
		
		messagePanel = new MessagePanel();
		add(messagePanel, BorderLayout.CENTER);
		
		putInFrame();
	}
	
	public void viewNewMessage(String sender, String message) {
		messagesPanel.addMessage(sender, message);
		updateUI();
	}

	private void putInFrame() {
		frame = new JFrame("Chatt Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void disposeFrame() {
		frame.dispose();
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
		
		public String getUsername() {
			return lblUser.getText();
		}
	}
	
	/**
	 * Panel containing contacts
	 * @author Alexander Libot
	 *
	 */
	private class UsersPanel extends JPanel {
		
		
		public UsersPanel(ArrayList<ContactTest> contacts) {
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			JPanel tempPanel = new JPanel();
			ContactButtonPanel buttonsPanel = new ContactButtonPanel();
			
			tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.Y_AXIS));
			tempPanel.add(new ContactPanel(null, buttonsPanel));
			
			onlinePanel = new OnlinePanel(buttonsPanel);
			tempPanel.add(onlinePanel);
			
			add(tempPanel, BorderLayout.CENTER);
			add(buttonsPanel, BorderLayout.SOUTH);
		}
	}
	
	private class ContactPanel extends JPanel implements MouseListener {

		private JLabel header;
		private JPanel contactList;
		private ArrayList<ContactTest> contacts;
		private ContactButtonPanel buttonsPanel;
		
		public ContactPanel(ArrayList<ContactTest> contacts, ContactButtonPanel buttonsPanel) {
			this.contacts = contacts;
			this.buttonsPanel = buttonsPanel;
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			contactList = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.weighty = 1;
            contactList.add(new JPanel(), gbc);
			
            add(new JScrollPane(contactList), BorderLayout.CENTER);
			
            try {
			for (ContactTest contact : this.contacts) {
	            GridBagConstraints gbc2 = new GridBagConstraints();
	            gbc2.gridwidth = GridBagConstraints.REMAINDER;
	            gbc2.weightx = 1;
	            gbc2.fill = GridBagConstraints.HORIZONTAL;
				contactList.add(contact, gbc2, 0);
				contact.addMouseListener(this);
			}
            } catch (NullPointerException ex) {}
            
            Font headerFont = new Font("Helvetica", Font.BOLD, 14);
            header = new JLabel("Contacts");
            header.setFont(headerFont);
            add(header, BorderLayout.NORTH);

		}

		@Override
		public void mouseClicked(MouseEvent e) {}


		@Override
		public void mousePressed(MouseEvent e) {}


		@Override
		public void mouseReleased(MouseEvent e) {
			JLabel selectedLabel = (JLabel) e.getSource();
			
			
			for (ContactTest contact : contacts) {
				contact.setBackground(null);
				contact.setBorder(null);
			}
			
			selectedLabel.setBackground(Color.GREEN);
			selectedLabel.setBorder(BorderFactory.createLoweredBevelBorder());

			
			buttonsPanel.setAddButtonText("Remove contact");
		}

		@Override
		public void mouseEntered(MouseEvent e) {}


		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	class OnlinePanel extends JPanel implements ListSelectionListener {
		
		private JLabel header;
		private JPanel contactList;
		private ArrayList<JLabel> contacts = new ArrayList<JLabel>();
		
		private DefaultListModel<String> listModel;
		private JList<String> onlineList;
		
		private ContactButtonPanel buttonsPanel;
		
		private GridBagConstraints gbc = new GridBagConstraints();
		
		public OnlinePanel(ContactButtonPanel buttonsPanel) {
			this.buttonsPanel = buttonsPanel;
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			listModel = new DefaultListModel<>();
			onlineList = new JList<>(listModel);   
			onlineList.addListSelectionListener(this);
			add(onlineList, BorderLayout.CENTER);
			
            Font headerFont = new Font("Helvetica", Font.BOLD, 14);
            header = new JLabel("Online");
            header.setFont(headerFont);
            add(header, BorderLayout.NORTH);
		}
		
		public void addOnlineUsers(MockUser[] users) {
			
			listModel.clear();
			
			for (MockUser user : users) {
				if (!user.getUsername().equals(username))
					listModel.addElement(user.getUsername());
			}
			
			updateUI();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedUser = onlineList.getSelectedValue();
			if (selectedUser == null)
				inputPanel.toggleSendButton(false);
			else
				inputPanel.toggleSendButton(true);
		}
	}
	
	/*
	 * Legacy
	 */
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
	
	private class ContactButtonPanel extends JPanel implements ActionListener {
		private JButton btnAddContact;
		private JButton btnLogout;
		
		public ContactButtonPanel() {
			btnAddContact = new JButton("Add contact");
			btnLogout = new JButton("Logout");
			btnLogout.addActionListener(this);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createHorizontalGlue());
			add(btnAddContact);
			add(btnLogout);
			add(Box.createHorizontalGlue());
		}
		
		public void disableAddButton() {
			btnAddContact.setEnabled(false);
		}
		
		public void enableAddButton() {
			btnAddContact.setEnabled(true);
		}
		
		public void setAddButtonText(String text) {
			btnAddContact.setText(text);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnLogout)) {
				controller.logout();
				frame.dispose();
			}
				
				
		}
	}
	
	/*
	 * Legacy
	 */
	private class ContactTest extends JLabel {
		
		private boolean contact;
		
		public ContactTest(String username) {
			setText(username);
			contact = false;
		}
		
		public void addContact() {
			contact = true;
		}
		
		public void removeContact() {
			contact = false;
		}
	}
	
	/*
	 * Legacy
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
		
//		private MessagesPanel messagesPanel;
		
		public MessagePanel() {
			setLayout(new BorderLayout());
			
			messagesPanel = new MessagesPanel();
			inputPanel = new InputPanel();
			
			add(messagesPanel, BorderLayout.CENTER);
			add(inputPanel, BorderLayout.SOUTH);
		}
		
//		public void addMessage(String sender, String message) {
//			messagesPanel.addMessage(message);
//		}
	}
	
	private class MessagesPanel extends JPanel {
		private ArrayList<Message> messages;
		private JPanel messageList;
		
		public MessagesPanel() {
			messages = new ArrayList<>();
			setLayout(new BorderLayout());
			messageList = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.weighty = 1;
            messageList.add(new JPanel(), gbc);
			
            add(new JScrollPane(messageList));
			
			
//			for (Message message : this.messages) {
//	            GridBagConstraints gbc2 = new GridBagConstraints();
//	            gbc2.gridwidth = GridBagConstraints.REMAINDER;
//	            gbc2.weightx = 1;
//	            gbc2.fill = GridBagConstraints.HORIZONTAL;
//				messageList.add(message, gbc2, -1);
//			}
		}
		
		public void addMessage(String sender, String text) {
			Message message = new Message(sender, text);
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
			
			if (selectedUser == null)
				return;
			
			controller.sendMessage(message, selectedUser);
		}
		
		public void toggleSendButton(boolean toggle) {
			btnSend.setEnabled(toggle);
		}
		
	}
	
	public OnlinePanel getOnelinePanel() {
		return onlinePanel;
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
			if (sender.equals(titlePanel.getUsername()))
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
}



