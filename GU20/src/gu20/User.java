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
	
	@Override
	public int hashCode() {
		int hash = username.hashCode() * 31;
		hash = hash * avatar.hashCode() * 41;
		return hash;
	}
}
