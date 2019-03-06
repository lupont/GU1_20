package gu20;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFrame;


public class ChattPanel {
	
	public static void main(String[] args) {
	JFrame window = new JFrame ("Chatt Window");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setVisible(true);
	window.setSize(800, 600);
	
	JPanel panel = new JPanel();
	panel.setLayout(null);
	window.add(panel);
	
	JButton btnadd = new JButton("Add");
	btnadd.setBounds(122, 531, 100, 50);
	panel.add(btnadd);
	
	JButton btnsend = new JButton("Send");
	btnsend.setBounds(702, 531, 100, 50);
	panel.add(btnsend);
	
	JTextField writemessage = new JTextField();
	writemessage.setBounds(650, 422, 239, 29);
	panel.add(writemessage);
	writemessage.setColumns(10);
	
	
	
	}
	
}
	

	
	
	
	
	
