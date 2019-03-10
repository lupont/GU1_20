package gu20;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChattPanel extends JPanel implements ActionListener {
	private JButton btnAdd = new JButton("Add");
	private JButton btnSend = new JButton("Send");
	private Font fontButtons = new Font("Sanserif", Font.PLAIN, 21);
	private JTextField writemessage = new JTextField();

	public ChattPanel() {
		this.setLayout(null);
		this.setPreferredSize(new Dimension(800, 600));

		// setBound( x(höger eller vänster), y(upp eller ner), 300(storlek i längd),
		// 50(storlek i höjd) )
		btnAdd.setBounds(-2, 553, 300, 50);
		btnAdd.setFont(fontButtons);

		btnSend.setBounds(702, 553, 100, 50);
		btnSend.setFont(fontButtons);

		writemessage.setBounds(300, 553, 420, 50);

		btnAdd.addActionListener(this);
		btnSend.addActionListener(this);
		writemessage.addActionListener(this);

		add(btnAdd);
		add(btnSend);
		add(writemessage);
	}

	public void actionPerformed(ActionEvent e) {

	}

	public static void main(String[] args) {
		JFrame window = new JFrame("Chatt Window");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(new MockGUI());
		window.pack();
		window.setVisible(true);
	}

}
	
	
	
	
	
