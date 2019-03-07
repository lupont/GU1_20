package gu20;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import sources.User;

public class LoginPanel extends JPanel {
	
	private GUIController guiC;
	
	private JFrame frame;
	
	private JLabel lblTitle;
	private JTextField tfUserField;
	private JButton btnLogin;
	
	
	public LoginPanel() {
		setPreferredSize(new Dimension(200,100));
		
		Font titleFont = new Font("Helvetica", Font.BOLD, 20);
		lblTitle = new JLabel("Chat Program");
		lblTitle.setFont(titleFont);
		
		tfUserField = new JTextField();
		tfUserField.setPreferredSize(new Dimension(180, 30));
		tfUserField.setText("Enter username");
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new LoginListener());

		add(lblTitle);
		add(Box.createVerticalGlue());
		add(tfUserField);
		add(Box.createVerticalGlue());
		add(btnLogin);
	}
	
	public void putInFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
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
			User user = new User(tfUserField.getText());
			guiC.setClient(user);
			guiC.testInit();
			disposeFrame();
			guiC.openGUI();
		}	
	}
	
	public void setController (GUIController guiC) {
		this.guiC = guiC;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new LoginPanel();
			}
		});
	}

}