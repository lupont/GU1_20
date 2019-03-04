package gu20;

import java.io.Serializable;

import javax.swing.Icon;

public class Message implements Serializable{
	private String text;
	private Icon icon;
	
	public Message(Message message) {
		this.text = message.getText();
		this.icon = message.getIcon();
	}
	
	public Message(String text, Icon icon) {
		this.text = text;
		this.icon = icon;
	}
	
	public String getText() {
		return text;
	}
	
	public Icon getIcon() {
		return icon;
	}

}
