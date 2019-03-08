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
	private JPanel contactPanel;
	private MessagePanel messagePanel;
	private MessagesPanel messagesPanel;
	private InputPanel inputPanel;
	
	private OnlinePanel onlinePanel;
	
	private String username;
	private GUIController controller;
	private String selectedUser;

	public MockGUI(GUIController guiC) {
		this("Test Testsson", guiC);
	}
	
	public MockGUI(String username, GUIController guiC) {
		this.username = username;
		this.controller = guiC;
		
		this.setPreferredSize(new Dimension(700, 300));
		setLayout(new BorderLayout());
		
		titlePanel = new TitlePanel(username);
		add(titlePanel, BorderLayout.NORTH);
		
		contactPanel = new UsersPanel();
		add(contactPanel, BorderLayout.WEST);
		
		messagePanel = new MessagePanel();
		add(messagePanel, BorderLayout.CENTER);
		
		putInFrame();
	}
	
	public void viewNewMessage(MockUser sender, String message) {
		messagesPanel.addMessage(sender, message);
		updateUI();
	}
	
	public void addOnlineUsers(MockUser[] onlineUsers) {
		onlinePanel.addOnlineUsers(onlineUsers);
	}

	private void putInFrame() {
		frame = new JFrame("Chatt Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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
		
		
		public UsersPanel() {
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			JPanel tempPanel = new JPanel();
			ContactButtonPanel buttonsPanel = new ContactButtonPanel();
			
			tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.Y_AXIS));
			tempPanel.add(new ContactPanel(buttonsPanel));
			
			onlinePanel = new OnlinePanel(buttonsPanel);
			tempPanel.add(onlinePanel);
			
			add(tempPanel, BorderLayout.CENTER);
			add(buttonsPanel, BorderLayout.SOUTH);
		}
	}
	
	private class ContactPanel extends JPanel implements MouseListener {

		private JLabel header;
		private JPanel contactList;
		private ContactButtonPanel buttonsPanel;
		
		public ContactPanel(ContactButtonPanel buttonsPanel) {
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

		private DefaultListModel<String> listModel;
		private JList<String> onlineList;

		public OnlinePanel(ContactButtonPanel buttonsPanel) {
			
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



