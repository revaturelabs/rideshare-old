package com.revature.rideshare.json;

import java.util.ArrayList;

public class RideRequestJSON {
	private String channel;
	private String text;
	private String response_type;
	private ArrayList<Attachment> attachments;
	
	public RideRequestJSON() {}
	
	public RideRequestJSON(String channel, String text, String response_type, ArrayList attachments) {
		this.channel = channel;
		this.text = text;
		this.response_type = response_type;
		this.attachments = attachments;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResponse_type() {
		return response_type;
	}

	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "RideRequestJSON [channel=" + channel + ", text=" + text + "]";
	}
}
