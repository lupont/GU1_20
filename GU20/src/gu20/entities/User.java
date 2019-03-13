package gu20.entities;

import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * A mock of how a user could be implemented, used for testing the Server.
 * @author Oskar Molander, Pontus Laos
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = -6871151100638230L;
	
	private String username;
	private ImageIcon avatar;
	
	/**
	 * Constructs a new User with the given username and avatar.
	 * @param username The user's username.
	 * @param avatar The user's avatar.
	 */
	public User(String username, ImageIcon avatar) {
		this.username = username;
		this.avatar = avatar;
	}
	
	/**
	 * @return The user's username.
	 */
	public String getUsername() { 
		return username; 
	}
	
	/**
	 * @return The user's avatar.
	 */
	public ImageIcon getAvatar() {
		return avatar;
	}
	
	/**
	 * Sets the user's avatar.
	 * @param avatar The avatar to set to the user.
	 */
	public void setAvatar(ImageIcon avatar) {
		this.avatar = avatar;
	}
	
	/**
	 * Uses the username.
	 */
	@Override
	public int hashCode() {
		return username.hashCode();
	}
	
	/**
	 * Tests if the user has the same username as the given object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof User)) {
			return false;
		}
		return username.equals(((User) obj).getUsername());
	}
	
	/**
	 * Returns the user's username.
	 */
	@Override
	public String toString() {
		return username;
	}
}
