package gu20;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class User implements Serializable{
	private String username;
	private ImageIcon profilePic;
	
	
	public User(String username, ImageIcon profilePic) {
		this.profilePic = profilePic;
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public ImageIcon getProfilePic() {
		return profilePic;
	}
	
	public int hasCode() {
		return username.hashCode();
	}
	
	public boolean equals(Object obj) {
		return username.equals(obj);
	}

}
