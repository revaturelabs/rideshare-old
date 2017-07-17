package com.revature.rideshare.json;

import java.util.ArrayList;

public class Attachment {
	private String text;
	private String fallback;
	private String callback_id;
	private String color;
	private String attachment_type;
	private ArrayList<Action> actions;
	
	public Attachment() {}
	
	public Attachment(String text, String fallback, String callback_id, String color, String attachment_type, ArrayList actions) {
		this.text = text;
		this.fallback = fallback;
		this.callback_id = callback_id;
		this.color = color;
		this.attachment_type = attachment_type;
		this.actions = actions;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFallback() {
		return fallback;
	}

	public void setFallback(String fallback) {
		this.fallback = fallback;
	}

	public String getCallback_id() {
		return callback_id;
	}

	public void setCallback_id(String callback_id) {
		this.callback_id = callback_id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getAttachment_type() {
		return attachment_type;
	}

	public void setAttachment_type(String attachment_type) {
		this.attachment_type = attachment_type;
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	@Override
	public String toString() {
		return "Attachment [text=" + text + ", fallback=" + fallback + ", callback_id=" + callback_id + ", color="
				+ color + ", attachment_type=" + attachment_type + ", actions=" + actions + "]";
	}
}