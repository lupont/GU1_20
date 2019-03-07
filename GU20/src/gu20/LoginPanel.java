package gu20;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LoginPanel {
	
	private GUIController guiC;
	
	private JFrame frame;
	
	private JTextField tfUserField;

	
	
	public LoginPanel(GUIController guiC) {
		this.guiC = guiC;

		putInFrame(initPanel());
	}
	
	/**
	 * Initializes a new panel with a label, textfield and button
	 * @return New JPanel
	 */
	private JPanel initPanel() {
		JPanel panel = new JPanel();
		
		Font titleFont = new Font("Helvetica", Font.BOLD, 20);
		JLabel lblTitle = new JLabel("Chat Program");
		lblTitle.setFont(titleFont);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new LoginListener());
		
		tfUserField = new JTextField();
		tfUserField.setPreferredSize(new Dimension(180, 30));
		tfUserField.setText("Enter username");
		tfUserField.addActionListener(new LoginListener());
		
		panel.setPreferredSize(new Dimension(200,100));
		
		panel.add(lblTitle);
		panel.add(Box.createVerticalGlue());
		panel.add(tfUserField);
		panel.add(Box.createVerticalGlue());
		panel.add(btnLogin);
		
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
			guiC.login(tfUserField.getText());
		}	
	}
}