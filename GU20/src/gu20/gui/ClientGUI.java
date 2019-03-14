package gu20.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gu20.Helpers;
import gu20.entities.User;
/**
 * A mock for how the GUI might be implemented, used for testing GUIController
 * @author Alexander Libot
 *
 */
@SuppressWarnings("serial")
public class ClientGUI extends JPanel implements GUIInterface {
	
	private JFrame frame;
	
	private TitlePanel titlePanel;
	private UsersPanel usersPanel;
	private MessagePanel messagePanel;
	private MessagesPanel messagesPanel;
	private InputPanel inputPanel;
	
	private String username;
	private GUIController controller;
	private List<String> selectedUsers;
	
	private File image = null;

	public ClientGUI(GUIController guiC) {
		this("Test Testsson", guiC);
	}
	
	/**
	 * Constructs a new GUI, initializes all the elements and puts them in a frame.
	 * @param username Username of the person logged in
	 * @param guiC Controller-instance to receive and send updates
	 */
	public ClientGUI(String username, GUIController guiC) {
		this.username = username;
		this.controller = guiC;
		
		initGUI();
	}
	
	/**
	 * @inheritDoc
	 */
	public void viewNewMessage(User sender, String message, ImageIcon image, String[] recipients) {
		messagesPanel.addMessage(sender, message, image, recipients);
		System.out.println("viewNewMessage: received message from " + sender);
		updateUI();
	}
	
	/**
	 * @inheritDoc
	 */
	public void addOnlineUsers(String[] onlineUsers) {
		usersPanel.addOnlineUsers(onlineUsers);
		updateUI();
	}
	
	/**
	 * @inheritDoc
	 */
	public void addContacts(String[] contacts) {
		usersPanel.addContacts(contacts);
		updateUI();
	}
	
	/**
	 * @inheritDoc
	 */
	public void addAvatar(ImageIcon avatar) {
		titlePanel.addAvatar(avatar);
	}
	
	/**
	 * Initializes all panels in the GUI
	 */
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
	
	/**
	 * Initializes a new frame and puts the main panel in the frame
	 */
	private void putInFrame() {
		frame = new JFrame("Chatt Window");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setIconImage(new ImageIcon("res/images/icon.png").getImage());
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new CloseListener());
		frame.setVisible(true);	
	}
	
	/**
	 * Title panel to be displayed on top of page.
	 * Contains program title, username and avatar (if set)
	 */
	private class TitlePanel extends JPanel implements MouseListener {
		private JLabel lblTitle;
		private JLabel lblUser;
		private JLabel lblAvatar;
		
		/**
		 * Creates a new title bar, only to be called from initGUI()
		 * @param username Username of the user
		 */
		private TitlePanel(String username) {
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
		
		/**
		 * Displays an avatar, if user has set one
		 * @param avatar Image to be displayed as avatar
		 */
		private void addAvatar(ImageIcon avatar) {
			if (avatar != null)
				lblAvatar.setIcon(avatar);
		}
		
		/*
		 * Removed access to atm for not working
		 */
		@SuppressWarnings("unused")
		private void chooseFile() {
			JFileChooser fc = new JFileChooser();
			int returnValue = fc.showOpenDialog(this);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				controller.addProfilePicture(fc.getSelectedFile());
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
//			chooseFile(); //Not working atm
		}

		//Not used
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
	}
	
	/**
	 * Component-panel for user-panel, online-panel and onlinebutton-panel
	 */
	private class UsersPanel extends JPanel {
		
		private ContactPanel contactPanel;
		private OnlinePanel onlinePanel;
		private ContactButtonPanel buttonsPanel;
		
		/**
		 * Creates a new userspanel, only to be called from initUI()
		 */
		private UsersPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			buttonsPanel = new ContactButtonPanel();
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
		
		/**
		 * Replaces the contacts in contact-panel
		 * @param contacts A new string-array of usernames to be displayed
		 */
		private void addContacts(String[] contacts) {
			contactPanel.updateUsers(contacts);
		}

		/**
		 * Replaces the online users in online-panel
		 * @param onlineUsers A new string-array of usernames to be displayed
		 */
		private void addOnlineUsers(String[] onlineUsers) {
			onlinePanel.updateUsers(onlineUsers);
		}
		
		/**
		 * Deselects all users in the specified list
		 * Used, when changing lists
		 * @param list The list to deselect all users from (online/contacts)
		 */
		private void deselectAll(String list) {
			if (list.equals("online"))
				onlinePanel.deselectAll();
			else if (list.equals("contacts"))
				contactPanel.deselectAll();
		}
	}
	
	/**
	 * Abstract class containing a list of string-objects and ability to deselect all
	 * Classes that inherit this must implement updateUsers(String users)
	 */
	private abstract class AbstractUserPanel extends JPanel implements ListSelectionListener {
		JLabel header;

		DefaultListModel<String> listModel;
		JList<String> userList;
		
		ContactButtonPanel buttonsPanel;
		
		/**
		 * Can only be called from subclasses. Creates a new panel with a list
		 * @param header Title of the list
		 * @param buttonsPanel An instance of ContactButtonPanel to change text of button
		 */
		private AbstractUserPanel(String header, ContactButtonPanel buttonsPanel) {
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			Font headerFont = new Font("Helvetica", Font.BOLD, 14);
			this.header = new JLabel(header);
            this.header.setFont(headerFont);
            add(this.header, BorderLayout.NORTH);
            
			listModel = new DefaultListModel<>();
			userList = new JList<>(listModel);
			userList.addListSelectionListener(this);
			add(new JScrollPane(userList), BorderLayout.CENTER);
			
			this.buttonsPanel = buttonsPanel;
		}
		
		/**
		 * Deselect all selected elements in list
		 */
		protected void deselectAll() {
			userList.clearSelection();
		}
		
		/**
		 * Update the list with a new array of string-objects
		 * @param users A string-array of usernames to display
		 */
		protected abstract void updateUsers(String[] users);
	}
	
	/**
	 * Inherits AbstractUserPanel, 
	 */
	private class ContactPanel extends AbstractUserPanel {
		
		/**
		 * Creates a new ContactPanel. Calls superclass-constructor
		 * @param header Title of the list
		 * @param buttonsPanel ContactButtonPanel-instance to edit
		 */
		public ContactPanel(String header, ContactButtonPanel buttonsPanel) {
			super(header, buttonsPanel);
		}

		/**
		 * @inheritDoc
		 */
		protected void updateUsers(String[] contacts) {
			
			if (contacts != null) {
				listModel.clear();
				for (String contact : contacts)
					listModel.addElement(contact);
				ArrayList<String> list = Collections.list(listModel.elements());
				Collections.sort(list);
				listModel.clear();
				for(String s:list) { 
					listModel.addElement(s); 
				}
			}
		}
		
		/**
		 * When an element/elements is selected from list.
		 * Sets the instance-variable selectedUsers to a list containing the selected elements.
		 * Deselects all elements from online list.
		 * Changes text of button to "Remove contact"
		 * Sets the sendbutton to enabled
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedUsers = userList.getSelectedValuesList();
			usersPanel.deselectAll("online");
			buttonsPanel.setAddButtonText("Remove contact");
			inputPanel.toggleSendButton(true);
		}
	}
	
	/**
	 * Inherits AbstractUserPanel
	 */
	private class OnlinePanel extends AbstractUserPanel {
		
		private boolean playSound = false;
		
		/**
		 * Creates a new OnlinePanel. Calls superclass-constructor
		 * @param header Title of the list
		 * @param buttonsPanel ContactButtonPanel-instance to edit
		 */
		public OnlinePanel(String header, ContactButtonPanel buttonsPanel) {
			super(header, buttonsPanel);
		}
		
		/**
		 * @inheritDoc
		 */
		public void updateUsers(String[] users) {
			
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
			
			//Don't play sound when you login
			if (playSound)
				Helpers.play("res/sounds/login.wav");
			else
				playSound = true;
		}

		/**
		 * When an element/elements is selected from list.
		 * Sets the instance-variable selectedUsers to a list containing the selected elements.
		 * Deselects all elements from contact list.
		 * Changes text of button to "Add contact".
		 * Sets the sendbutton to enabled.
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedUsers = userList.getSelectedValuesList();
			usersPanel.deselectAll("contacts");
			buttonsPanel.setAddButtonText("Add contact");
			inputPanel.toggleSendButton(true);
		}
	}
	
	/**
	 * Panel containing ability to add/remove contact and logout
	 */
	private class ContactButtonPanel extends JPanel implements ActionListener {
		private JButton btnAddContact;
		private JButton btnLogout;
		
		/**
		 * Creates a new ContactButtonPanel. Adds buttons to add/remove contact and logout.
		 */
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
		
		/**
		 * Sets the text of add/remove contact button
		 * @param text The text to be displayed on button
		 */
		public void setAddButtonText(String text) {
			btnAddContact.setText(text);
		}

		/**
		 * Invoked if a button is clicked.
		 * Logs out if Logout-button is clicked.
		 * Adds/removes a user to/from contacts.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnLogout)) {
//				controller.logout();
				frame.dispose();
			} else if (e.getSource().equals(btnAddContact)) {
				if (selectedUsers != null) {
					for (String selectedUser : selectedUsers) {
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
	
	/**
	 * Class containging messagespanel and inputpanel
	 */
	private class MessagePanel extends JPanel {

		/**
		 * Creates a new MessagePanel, creates a new MessagesPanel and a new InputPanel, and adds them to the panel
		 */
		public MessagePanel() {
			setLayout(new BorderLayout());
			
			messagesPanel = new MessagesPanel();
			inputPanel = new InputPanel();
			
			add(messagesPanel, BorderLayout.CENTER);
			add(inputPanel, BorderLayout.SOUTH);
		}
	}
	
	/**
	 * Class to show messages
	 */
	private class MessagesPanel extends JPanel {
		private ArrayList<Message> messages;
		private JPanel messageList;
		
		/**
		 * Creates a new MessagesPanel. Contains a scrollable list with message-objects.
		 */
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
		
		/**
		 * Add a new message to the panel
		 * @param sender The sender of the message
		 * @param text The text of the message (might be null)
		 * @param image The image of the message (might be null)
		 * @param recipients The recipients of the message
		 */
		public void addMessage(User sender, String text, ImageIcon image, String[] recipients) {
			
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridwidth = GridBagConstraints.REMAINDER;
            gbc2.weightx = 1;
            gbc2.fill = GridBagConstraints.HORIZONTAL;
			
			if (!text.equals("")) {
				Message textMessage = new Message(sender, text, recipients);
				messages.add(textMessage);
				messageList.add(textMessage, gbc2, -1); //Adds messages to the bottom
			}
            
			if (image != null) {
				Message imageMessage = new Message(sender, image, recipients);
				messages.add(imageMessage);
				messageList.add(imageMessage, gbc2, -1); //Adds messages to the bottom
			}
			
			if (!username.equals(sender.getUsername())) {
				Helpers.play("res/sounds/message.wav");
			}
		}
	}
	
	/**
	 * A panel containing abilities to write and send messages/images
	 */
	private class InputPanel extends JPanel implements ActionListener {
		private JTextField tfInput;
		private JButton btnSend;
		private JButton btnImage;
		
		/**
		 * Creates a new panel
		 */
		public InputPanel() {
			tfInput = new JTextField();
			tfInput.addActionListener(this);
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
		
		/**
		 * Sends a message or opens file chooser for choosing an image to send
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnSend) || e.getSource().equals(tfInput)) { //If send button is pressed
				String message = tfInput.getText();

				if (selectedUsers == null)
					return;

				controller.sendMessage(message, selectedUsers, image);
				image = null;
				tfInput.setText(null);
				btnImage.setText("Choose image");
				
			} else if (e.getSource().equals(btnImage)) { //If chose image button is pressed
				chooseImage();
			}
		}
		
		/**
		 * Opens a file chooser to let user select an image to send
		 */
		private void chooseImage() {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Choose an image to send, jpg or png");
			int returnValue = fc.showOpenDialog(fc);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				image = fc.getSelectedFile();
				btnImage.setText("Image chosen");
			}
		}
		
		/**
		 * Toggles the send button to enabled or disabled
		 * @param toggle True if enabled or false if disabled
		 */
		public void toggleSendButton(boolean toggle) {
			btnSend.setEnabled(toggle);
		}
	}
	
	/**
	 * A panel with message to be displayed in MessagePanel
	 */
	private class Message extends JPanel implements MouseListener {
		private JLabel lblSender;
		private JLabel lblMessage;
		private JLabel lblAvatar;
		
		private JPanel pnlSender;
		private JPanel pnlMessage;
		
		private String[] recipients;
		
		private RecipientsPanel recipientsPanel;
		
		private ImageIcon fullsizePicture;
		
		/**
		 * Private constructor to be called from other constructors.
		 * Creates a panel containing a sender panel (with name and ev. avatar) and a panel containging the message (text or image).
		 * @param sender The sender of the message
		 * @param recipients The recipients of the message
		 */
		private Message(User sender, String[] recipients) {
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
		
		/**
		 * Creates a new Message containing an image
		 * @param sender The sender of the message
		 * @param message The message to be displayed
		 * @param recipients The recipients of the message
		 */
		public Message(User sender, String message, String[] recipients) {
			this(sender, recipients);
			
			lblMessage = new JLabel(message);
			pnlMessage.add(lblMessage);

			add(pnlMessage);
		}
		
		/**
		 * Creates a new Message containing an image
		 * @param sender The sender of the message
		 * @param image The image to be displayed
		 * @param recipients The recipients of the message
		 */
		public Message(User sender, ImageIcon image, String[] recipients) {
			this(sender, recipients);
			
			fullsizePicture = image;
			
			ImageIcon rescaledImage = new ImageIcon(Helpers.getScaledImage(image.getImage(), 80));
			lblMessage = new JLabel(rescaledImage);
			lblMessage.addMouseListener(this);
			
			pnlMessage.add(lblMessage);
			
			add(pnlMessage);
		}

		/**
		 * Creates and views a new recipientsPanel with all recipients to the message
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			if (e.getSource().equals(pnlSender)) {
				Point point = pnlSender.getLocationOnScreen();
				point.setLocation(point.getX(), point.getY() + pnlSender.getHeight());
				recipientsPanel = new RecipientsPanel(recipients, point);
			}
		}
		
		/**
		 * Disposes the recipientsPanel
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource().equals(pnlSender))
					recipientsPanel.dispose();
		}
		
		/**
		 * Opens an image fullsize 
		 */
		public void mouseClicked(MouseEvent e) {
			if (e.getSource().equals(lblMessage) && fullsizePicture != null) {
				viewPictureFullSize();
			}
		}
		
		//Not used
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		
		private void viewPictureFullSize() {
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			double width = screenSize.getWidth();
			double height = screenSize.getHeight();
			
			ImageIcon displayPicture;
			if (fullsizePicture.getIconWidth() > width || fullsizePicture.getIconHeight() > height) {
				displayPicture = new ImageIcon(Helpers.getScaledImage(fullsizePicture.getImage(), (int)height));
			} else {
				displayPicture = fullsizePicture;
			}

			JLabel lblPicture = new JLabel(displayPicture);
			JPanel pnlPicture = new JPanel();
			pnlPicture.add(lblPicture);
			
			JFrame frame = new JFrame();
			
			pnlPicture.addMouseListener(new MouseListener() {

				
				@Override
				public void mousePressed(MouseEvent e) {
					frame.dispose();
				}

				public void mouseClicked(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});

			frame.setUndecorated(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(pnlPicture);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}
	
	/**
	 * Logs out if user closes window
	 */
	private class CloseListener implements WindowListener {

		@Override
		public void windowClosed(WindowEvent e) {
			controller.logout();
		}

		//Not used
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
	}
	
}