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
import java.util.ArrayList;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A mock for how the GUI might be implemented, used for testing GUIController
 * @author Alexander Libot
 *
 */
@SuppressWarnings("serial")
public class MockGUI extends JPanel implements GUIInterface {
	
	private JFrame frame;
	
	private TitlePanel titlePanel;
	private UsersPanel usersPanel;
	private MessagePanel messagePanel;
	private MessagesPanel messagesPanel;
	private InputPanel inputPanel;
	
	private String username;
	private GUIController controller;
	private String selectedUser;

	public MockGUI(GUIController guiC) {
		this("Test Testsson", guiC);
	}
	
	public MockGUI(String username, GUIController guiC) {
		this.username = username;
		this.controller = guiC;
		
		initGUI();
	}
	
	private void initGUI() {
		this.setPreferredSize(new Dimension(700, 300));
		setLayout(new BorderLayout());
		
		titlePanel = new TitlePanel(username);
		add(titlePanel, BorderLayout.NORTH);
		
		usersPanel = new UsersPanel();
		add(usersPanel, BorderLayout.WEST);
		
		messagePanel = new MessagePanel();
		add(messagePanel, BorderLayout.CENTER);
		
		putInFrame();
	}
	
	private void putInFrame() {
		frame = new JFrame("Chatt Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void viewNewMessage(MockUser sender, String message) {
		messagesPanel.addMessage(sender, message);
		updateUI();
	}
	
	public void addOnlineUsers(MockUser[] onlineUsers) {
		usersPanel.addOnlineUsers(onlineUsers);
		updateUI();
	}

	public void addContact(MockUser contact) {
		usersPanel.addContact(contact);
		updateUI();
	}
	
	//TODO
	public void removeContact(MockUser contact) {
		
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
			lblUser = new JLabel("User:" + username);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(lblTitle);
			add(Box.createHorizontalGlue());
			add(lblUser);
			
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		}
	}
	
	/**
	 * Panel containing contacts
	 * @author Alexander Libot
	 *
	 */
	private class UsersPanel extends JPanel {
		
		private ContactPanel contactPanel;
		private OnlinePanel onlinePanel;
		private ContactButtonPanel buttonsPanel;
		
		
		public UsersPanel() {
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			buttonsPanel = new ContactButtonPanel();
			contactPanel = new ContactPanel("Contacts", buttonsPanel);
			onlinePanel = new OnlinePanel("Online", buttonsPanel);
			
			JPanel sidePanel = new JPanel();
			
			sidePanel.setLayout(new BoxLayout(sidePanel,BoxLayout.Y_AXIS));
			sidePanel.add(contactPanel);	
			sidePanel.add(onlinePanel);
			
			add(sidePanel, BorderLayout.CENTER);
			add(buttonsPanel, BorderLayout.SOUTH);
		}
		
		public void addContact(MockUser contact) {
			contactPanel.addContact(contact);
		}
		
		public void addOnlineUsers(MockUser[] onlineUsers) {
			onlinePanel.addOnlineUsers(onlineUsers);
		}
		
		public void deselectAll(String list) {
			if (list.equals("online"))
				onlinePanel.deselectAll();
			else if (list.equals("contacts"))
				contactPanel.deselectAll();
		}
	}
	
	private abstract class AbstractUserPanel extends JPanel implements ListSelectionListener {
		JLabel header;

		DefaultListModel<String> listModel;
		JList<String> userList;
		
		ContactButtonPanel buttonsPanel;
		
		public AbstractUserPanel(String header, ContactButtonPanel buttonsPanel) {
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			Font headerFont = new Font("Helvetica", Font.BOLD, 14);
			this.header = new JLabel(header);
            this.header.setFont(headerFont);
            add(this.header, BorderLayout.NORTH);
            
			listModel = new DefaultListModel<>();
			userList = new JList<>(listModel);   
			userList.addListSelectionListener(this);
			add(userList, BorderLayout.CENTER);
			
			this.buttonsPanel = buttonsPanel;
		}
		
		public void deselectAll() {
			userList.clearSelection();
		}
	}
	
	private class ContactPanel extends AbstractUserPanel {
		
		public ContactPanel(String header, ContactButtonPanel buttonsPanel) {
			super(header, buttonsPanel);
		}
		
		public void addContact(MockUser contact) {
			listModel.addElement(contact.getUsername());
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			usersPanel.deselectAll("online");
			
			buttonsPanel.setAddButtonText("Remove contact");
			
		}
	}
	
	class OnlinePanel extends AbstractUserPanel {
		
		public OnlinePanel(String header, ContactButtonPanel buttonsPanel) {
			super(header, buttonsPanel);
		}
		
		public void addOnlineUsers(MockUser[] users) {
			
			listModel.clear();
			
			if (users != null && users.length > 0) {
				for (MockUser user : users) {
					if (!user.getUsername().equals(username))
						listModel.addElement(user.getUsername());
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedUser = userList.getSelectedValue();
			usersPanel.deselectAll("contacts");
			buttonsPanel.setAddButtonText("Add contact");
			inputPanel.toggleSendButton(true);
		}
	}
	
	private class ContactButtonPanel extends JPanel implements ActionListener {
		private JButton btnAddContact;
		private JButton btnLogout;
		
		public ContactButtonPanel() {
			btnAddContact = new JButton("Add contact");
			btnAddContact.addActionListener(this);
			btnLogout = new JButton("Logout");
			btnLogout.addActionListener(this);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createHorizontalGlue());
			add(btnAddContact);
			add(btnLogout);
			add(Box.createHorizontalGlue());
		}
		
		public void setAddButtonText(String text) {
			btnAddContact.setText(text);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnLogout)) {
				controller.logout();
				frame.dispose();
			} else if (e.getSource().equals(btnAddContact)) {
				if (selectedUser != null)
					System.out.println(("Trying to add contact"));
					controller.addUserToContacts(selectedUser);
			}
		}
	}
	
	private class MessagePanel extends JPanel {

		public MessagePanel() {
			setLayout(new BorderLayout());
			
			messagesPanel = new MessagesPanel();
			inputPanel = new InputPanel();
			
			add(messagesPanel, BorderLayout.CENTER);
			add(inputPanel, BorderLayout.SOUTH);
		}
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
			
		}
		
		public void addMessage(MockUser sender, String text) {
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
			
			toggleSendButton(false);
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
	
	private class Message extends JPanel {
		private JLabel lblSender;
		private JLabel lblMessage;
		
		private JPanel pnlSender;
		private JPanel pnlMessage;
		
		public Message(MockUser sender, String message) {
			lblSender = new JLabel(sender.getUsername());
			lblMessage = new JLabel(message);
			
			pnlSender = new JPanel();
			pnlSender.add(lblSender);
			
			pnlMessage = new JPanel();
			pnlMessage.add(lblMessage);
			
			pnlSender.setAlignmentX(Component.LEFT_ALIGNMENT);
			if (sender.getUsername().equals(username))
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



