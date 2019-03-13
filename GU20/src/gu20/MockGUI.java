package gu20;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gu20.entities.User;

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
	private List<String> selectedUserList;
	
	private File image = null;

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
	
	public void viewNewMessage(User sender, String message, ImageIcon image, String[] recipients) {
		messagesPanel.addMessage(sender, message, image, recipients);
		System.out.println("viewNewMessage: received message from " + sender);
		updateUI();
	}
	
	public void addOnlineUsers(String[] onlineUsers) {
		usersPanel.addOnlineUsers(onlineUsers);
		updateUI();
	}
	
	public void addContacts(String[] contacts) {
		usersPanel.addContacts(contacts);
		updateUI();
	}
	
	public void addContact(String contact) {
		usersPanel.addContact(contact);
		updateUI();
	}
	
	public void removeContact(String contact) {
		usersPanel.removeContact(contact);
		updateUI();
	}
	
	public void addAvatar(ImageIcon avatar) {
		titlePanel.addAvatar(avatar);
	}
	
	/**
	 * Title panel to be displayed on top of page.
	 * Contains program title and username.
	 * @author Alexander Libot
	 *
	 */
	private class TitlePanel extends JPanel implements MouseListener {
		private JLabel lblTitle;
		private JLabel lblUser;
		private JLabel lblAvatar;
		
		public TitlePanel(String username) {
			lblTitle = new JLabel("Chat Program");
			lblUser = new JLabel("User:" + username);
			lblAvatar = new JLabel();
			
			lblUser.addMouseListener(this);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(lblTitle);
			add(Box.createHorizontalGlue());
			add(lblUser);
			add(lblAvatar);
			
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		}
		
		public void addAvatar(ImageIcon avatar) {
			if (avatar != null)
				lblAvatar.setIcon(avatar);
		}
		
		private void chooseFile() {
			JFileChooser fc = new JFileChooser();
			int returnValue = fc.showOpenDialog(this);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				controller.addProfilePicture(fc.getSelectedFile());
			}
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			chooseFile();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
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
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			buttonsPanel = new ContactButtonPanel();
//			buttonsPanel.setMinimumSize(new Dimension(150,0));
			contactPanel = new ContactPanel("Contacts", buttonsPanel);
			onlinePanel = new OnlinePanel("Online", buttonsPanel);
			
			JPanel sidePanel = new JPanel();
			
			sidePanel.setLayout(new BoxLayout(sidePanel,BoxLayout.Y_AXIS));
			sidePanel.setMinimumSize(new Dimension(300,0));
			sidePanel.add(contactPanel);	
			sidePanel.add(onlinePanel);
			
			add(sidePanel);
			add(buttonsPanel);
		}
		
		public void addContacts(String[] contacts) {
			contactPanel.addContacts(contacts);
		}
		
		public void addContact(String contact) {
			contactPanel.addContact(contact);
		}
		
		public void removeContact(String contact) {
			contactPanel.removeContact(contact);
		}
		
		public void addOnlineUsers(String[] onlineUsers) {
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
//			add(userList, BorderLayout.CENTER);
			add(new JScrollPane(userList), BorderLayout.CENTER);
			
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

		public void addContacts(String[] contacts) {
			
			if (contacts != null) {
				listModel.clear();
				for (String contact : contacts)
					listModel.addElement(contact);
				ArrayList<String> list = Collections.list(listModel.elements()); // get a collection of the elements in the model
				Collections.sort(list); // sort
				listModel.clear(); // remove all elements
				for(String s:list){ listModel.addElement(s); } // add elements
			}
		}
		
		public void addContact(String contact) {
			listModel.addElement(contact);
		}
		
		public void removeContact(String contact) {
			listModel.removeElement(contact);
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
//			selectedUser = userList.getSelectedValue();
			selectedUserList = userList.getSelectedValuesList();
			usersPanel.deselectAll("online");
			buttonsPanel.setAddButtonText("Remove contact");
			inputPanel.toggleSendButton(true);
		}
	}
	
	class OnlinePanel extends AbstractUserPanel {
		
		public OnlinePanel(String header, ContactButtonPanel buttonsPanel) {
			super(header, buttonsPanel);
		}
		
		public void addOnlineUsers(String[] users) {
			
			listModel.clear();
			
			if (users != null) {
				for (String user : users) {
					if (!user.equals(username))
						listModel.addElement(user);
				}
				ArrayList<String> list = Collections.list(listModel.elements()); // get a collection of the elements in the model
				Collections.sort(list); // sort
				listModel.clear(); // remove all elements
				for(String s:list){ listModel.addElement(s); } // add elements
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedUserList = userList.getSelectedValuesList();
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
			setMinimumSize(new Dimension(200,0));
			add(Box.createHorizontalGlue());
			add(btnAddContact);
			add(Box.createHorizontalGlue());
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
				if (selectedUserList != null) {
					for (String selectedUser : selectedUserList) {
						if (btnAddContact.getText().equals("Add contact")) {
							System.out.println("Trying to add contact");
							controller.addUserToContacts(selectedUser);
						} else if (btnAddContact.getText().equals("Remove contact")) {
							System.out.println("Trying to remove contact");
							controller.removeUserFromContacts(selectedUser);
						}
					}
				}
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
		
		public void addMessage(User sender, String text, ImageIcon image, String[] recipients) {
			
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridwidth = GridBagConstraints.REMAINDER;
            gbc2.weightx = 1;
            gbc2.fill = GridBagConstraints.HORIZONTAL;
			
			if (!text.equals("")) {
				Message textMessage = new Message(sender, text, recipients);
				messages.add(textMessage);
				messageList.add(textMessage, gbc2, -1);
			}
            
			if (image != null) {
				Message imageMessage = new Message(sender, image, recipients);
				messages.add(imageMessage);
				messageList.add(imageMessage, gbc2, -1);
			}
		}
	}
	
	private class InputPanel extends JPanel implements ActionListener {
		private JTextField tfInput;
		private JButton btnSend;
		private JButton btnImage;
		
		public InputPanel() {
			tfInput = new JTextField();
			btnSend = new JButton("Send");
			btnSend.addActionListener(this);
			
			btnImage = new JButton("Choose image");
			btnImage.addActionListener(this);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(tfInput);
			add(btnSend);
			add(btnImage);
			
			toggleSendButton(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnSend)) {
				String message = tfInput.getText();

				if (selectedUserList == null)
					return;

				controller.sendMessage(message, selectedUserList, image);
				image = null;
				btnImage.setText("Choose image");
				
			} else if (e.getSource().equals(btnImage)) {
				chooseImage();
			}
		}
		
		private void chooseImage() {
			JFileChooser fc = new JFileChooser();
			int returnValue = fc.showOpenDialog(fc);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				image = fc.getSelectedFile();
				btnImage.setText("Image chosen");
			}
		}
		
		public void toggleSendButton(boolean toggle) {
			btnSend.setEnabled(toggle);
		}
	}
	
	private class Message extends JPanel implements MouseListener {
		private JLabel lblSender;
		private JLabel lblMessage;
		private JLabel lblAvatar;
		
		private JPanel pnlSender;
		private JPanel pnlMessage;
		
		private String[] recipients;
		
		private RecipientsPanel recipientsPanel;
		
		public Message(User sender, String[] recipients) {
			this.recipients = recipients;
			String strSender;
			pnlSender = new JPanel();

			if (recipients.length > 1) {
				strSender = sender.getUsername() + "*";
			} else
				strSender = sender.getUsername();
			
			pnlSender.addMouseListener(this);
			
			lblSender = new JLabel(strSender);
			lblAvatar = new JLabel();
			
			pnlSender.setLayout(new BoxLayout(pnlSender, BoxLayout.X_AXIS));
			pnlSender.add(lblSender);
			
			if (sender.getAvatar() != null) {
				lblAvatar.setIcon(sender.getAvatar());
				pnlSender.add(lblAvatar);
			}
			
			pnlMessage = new JPanel();
			pnlSender.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			if (sender.getUsername().equals(username))
				pnlSender.setBackground(Color.RED);
			else
				pnlSender.setBackground(Color.BLUE);
			
			pnlSender.setMaximumSize(new Dimension(200, 100));
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			add(pnlSender);
		}
		
		public Message(User sender, String message, String[] recipients) {
			this(sender, recipients);
			
			lblMessage = new JLabel(message);
			pnlMessage.add(lblMessage);

			add(pnlMessage);
		}
		
		public Message(User sender, ImageIcon image, String[] recipients) {
			this(sender, recipients);
			
			lblMessage = new JLabel(image);
			pnlMessage.add(lblMessage);
			
			add(pnlMessage);
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			Point point = pnlSender.getLocationOnScreen();
			point.setLocation(point.getX(), point.getY()+pnlSender.getHeight());
			recipientsPanel = new RecipientsPanel(recipients, point);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			recipientsPanel.dispose();
		}
	}
	
}