package gu20;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

public class LoginPanel {
	
	private GUIController guiC;
	
	private JFrame frame;
	
	JLabel lblTitle;
	
	private JTextField tfUserField;
	
	private JComboBox<String> cbHosts;
	
	private Map<String, String> addresses;

	
	public LoginPanel(GUIController guiC, Map<String, String> addresses) {
		this.guiC = guiC;
		this.addresses = addresses;

		putInFrame(initPanel());
	}
	
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
		guiC.login(username, address);
	}
	
	public void changeText(String text) {
		lblTitle.setText(text);
	}
	
	/**
	 * Initializes a new panel with a label, textfield and button
	 * @return New JPanel
	 */
	private JPanel initPanel() {
		JPanel panel = new JPanel();
		
		Font titleFont = new Font("Helvetica", Font.BOLD, 20);
		lblTitle = new JLabel("Chat Program");
		lblTitle.setFont(titleFont);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new LoginListener());
		
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
		
		panel.setPreferredSize(new Dimension(200,150));
		
		panel.add(lblTitle);
		panel.add(Box.createVerticalGlue());
		panel.add(tfUserField);
		panel.add(Box.createVerticalGlue());
		panel.add(btnLogin);
		panel.add(cbHosts);
		
		return panel;
	}
	
	private void putInFrame(JPanel panel) {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private void disposeFrame() {
		frame.dispose();
	}
	
	private class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			disposeFrame();
			guiC.login(tfUserField.getText(), (String) cbHosts.getSelectedItem());
		}	
	}
}