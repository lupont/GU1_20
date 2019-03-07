package gu20;

import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * A mock of how a user could be implemented, used for testing the Server.
 * @author lupont
 *
 */
public class MockUser implements Serializable {
	private static final long serialVersionUID = 918248172419L;

	private String username;
	private ImageIcon avatar;
	
	public MockUser(String username, ImageIcon avatar) {
		this.username = username;
		this.avatar = avatar;
	}
	
	public String getUsername() { 
		return username; 
	}
	
	public ImageIcon getAvatar() {
		return avatar;
	}
	
	@Override
	public int hashCode() {
		return username.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MockUser)) {
			return false;
		}
		return username.equals(((MockUser) obj).getUsername());
	}
	
	@Override
	public String toString() {
		return username;
	}
}
