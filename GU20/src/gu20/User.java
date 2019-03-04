package gu20;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class User implements Serializable {
	private static final long serialVersionUID = 918248172419L;

	private String username;
	private ImageIcon avatar;
	
	public User(String username, ImageIcon avatar) {
		this.username = username;
		this.avatar = avatar;
	}
}
