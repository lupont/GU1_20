package gu20;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class GUI extends JPanel implements ActionListener {
	private JButton btnAdd = new JButton("Add");
	private JButton btnSend = new JButton("Send");
	private Font fontButtons = new Font("Sanserif", Font.PLAIN, 21);
	private JTextField tfWriteMessage = new JTextField();

	
	public GUI() {
		this.setPreferredSize(new Dimension(500, 300));
		btnAdd.setPreferredSize(new Dimension(80, 20));
		btnAdd.setFont(fontButtons);
        
		btnSend.setPreferredSize(new Dimension(80, 20));
		btnSend.setFont(fontButtons);
		
		tfWriteMessage.setPreferredSize(new Dimension(200, 50));
		
		
		
		
		
		
		
		

		btnAdd.addActionListener(this);
		btnSend.addActionListener(this);
		
		add(btnAdd);
		add(btnSend);
		add(tfWriteMessage);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		
	}

	public static void main(String[] args) {
	JFrame window = new JFrame ("Chatt Window");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.add(new GUI());
	window.pack();
	window.setVisible(true);
}



}



