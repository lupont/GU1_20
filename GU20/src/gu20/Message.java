package gu20;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Message implements Serializable {
	private static final long serialVersionUID = 91582891225L;
	
	private String text;
	private ImageIcon image;
	
	public Message(String text, ImageIcon image) {
		this.text = text;
		this.image = image;
	}

}
