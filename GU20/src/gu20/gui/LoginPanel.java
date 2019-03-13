package gu20.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

public class LoginPanel {
	
	private GUIController guiC;
	
	private JFrame frame;
	
	JLabel lblTitle;
	
	private JTextField tfUserField;
	
	private JButton btnChooseAvatar;
	
	private JComboBox<String> cbHosts;
	
	private Map<String, String> addresses;
	
	private File avatar = null;

	/**
	 * Creates a new login-window.
	 * @param guiC The controller-class to communicate to
	 * @param addresses A map of added addresses to connect to.
	 */
	public LoginPanel(GUIController guiC, Map<String, String> addresses) {
		this.guiC = guiC;
		this.addresses = addresses;

		putInFrame(initPanel());
	}
	
	/**
	 * Creates a new loginwindow with a specified title
	 * @param guiC The controller-class to communicate to
	 * @param addresses A map of added addresses to connect to.
	 * @param text Text to be displayed as title
	 */
	public LoginPanel(GUIController guiC, Map<String, String> addresses, String text) {
		this(guiC, addresses);
		changeText(text);
	}
	
	/*
	 * Test constructor, let you login without open login-gui.
	 * To be removed later.
	 */
	public LoginPanel(GUIController guiC, String username, String address) {
		this.guiC = guiC;
		guiC.login(username, address, null);
	}
	
	/*
	 * Test constructor, let you login with an avatar without open login-gui
	 */
	public LoginPanel(GUIController guiC, String username, String address, String avatarPath) {
		this.guiC = guiC;
		guiC.login(username, address, new File(avatarPath));
	}
	
	/**
	 * Changes to text of the title
	 * @param text Text to change title to
	 */
	public void changeText(String text) {
		lblTitle.setText(text);
	}
	
	/**
	 * Initializes a new panel with a label, textfield, combobox with addresses and button
	 * @return New JPanel The initialized Panel
	 */
	private JPanel initPanel() {
		JPanel panel = new JPanel();
		
		Font titleFont = new Font("Helvetica", Font.BOLD, 20);
		lblTitle = new JLabel("Chat Program");
		lblTitle.setFont(titleFont);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new LoginListener());
		
		btnChooseAvatar = new JButton("Choose avatar");
		btnChooseAvatar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				chooseAvatar();
			}
			
		});
		
		tfUserField = new JTextField();
		tfUserField.setPreferredSize(new Dimension(180, 30));
		tfUserField.setText("Enter username");
		tfUserField.addActionListener(new LoginListener());
		
		
		Set<String> set = addresses.keySet();
		String[] cbStrings = new String[set.size()];
		int index = 0;
		
		for (String key : set) {
			cbStrings[index++] = key;
		}
		
		cbHosts = new JComboBox<String>(cbStrings);
		try {
			cbHosts.setSelectedItem("local");
		} catch (Exception ex) {}
		
		panel.setPreferredSize(new Dimension(200,175));
		
		panel.add(lblTitle);
		panel.add(Box.createVerticalGlue());
		panel.add(tfUserField);
		panel.add(Box.createVerticalGlue());
		panel.add(btnChooseAvatar);
		panel.add(btnLogin);
		panel.add(cbHosts);
		
		return panel;
	}
	
	/**
	 * Open filechooser to let user pick an image-file from their harddrive
	 */
	private void chooseAvatar() {
		JFileChooser fc = new JFileChooser();
		int returnValue = fc.showOpenDialog(fc);
		
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			avatar = fc.getSelectedFile();
			btnChooseAvatar.setText("Avatar chosen");
		}
		
	}
	
	/**
	 * Creates a new frame and displays a panel in it.
	 * @param panel Panel to be put in frame
	 */
	private void putInFrame(JPanel panel) {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	/**
	 * Disposes the frame
	 */
	private void disposeFrame() {
		frame.dispose();
	}
	
	/**
	 * Listener for when login-button is pressed
	 */
	private class LoginListener implements ActionListener {

		/**
		 * Disposes frame and calls controllers login-method
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disposeFrame();
			guiC.login(tfUserField.getText(), (String) cbHosts.getSelectedItem(), avatar);
		}	
	}
}