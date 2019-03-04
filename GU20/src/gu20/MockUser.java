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
	
	public String getUsername() { return username; }
	
	@Override
	public int hashCode() {
		int hash = username.hashCode() * 31;
		hash = hash * (avatar != null ? avatar.hashCode() : 31) * 41;
		return hash;
	}
	
	@Override
	public String toString() {
		return username;
	}
}
