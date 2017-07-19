package com.revature.rideshare.json;

import java.util.ArrayList;

/**
 * This class is used to create a JSON object for passing messages to the user
 */
public class RideRequestJSON {
	
	// Name of the channel, @username, or user_id
	private String channel;
	
	//The basic text of the message. Only required if the message contains zero attachments.
	private String text;
	
	/*
	 * This field cannot be specified for a brand new message and must be used only in response to 
	 * the execution of message button action or a slash command response. 
	 * 
	 * Expects one of two values:
     *     - in_channel — display the message to all users in the channel where a message button was clicked. 
     *                    Messages sent in response to invoked button actions are set to in_channel by default.
     *      - ephemeral — display the message only to the user who clicked a message button. 
     *                    Messages sent in response to Slash commands are set to ephemeral by default.
	 */
	private String response_type;
	
	// Provide a JSON array of attachment objects. Adds additional components to the message. 
	// Messages should contain no more than 20 attachments.
	private ArrayList<Attachment> attachments;
	
	/**
	 * No-arg constructor 
	 */
	public RideRequestJSON() {}
	
	/**
	 * Constructor used for creating a Request JSON object
	 * @param channel
	 * @param text
	 * @param response_type
	 * @param attachments
	 */
	public RideRequestJSON(String channel, String text, String response_type, ArrayList attachments) {
		this.channel = channel;
		this.text = text;
		this.response_type = response_type;
		this.attachments = attachments;
	}

	/**
	 * Gets the channel name that the message will be sent to.
	 * @return the channel name
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Sets the channel name
	 * @param channel
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * Gets the message text.
	 * @return the message text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the message text
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Get the response_type of the message
	 * @return
	 */
	public String getResponse_type() {
		return response_type;
	}

	/**
	 * Sets the response_type of the message
	 * @param response_type
	 */
	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

	/**
	 * Get the list of attachments that the message contains
	 * @return list of attachments
	 */
	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Set the list of attachments that the message contains
	 * @param attachments
	 */
	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * String representation of the Request message.
	 */
	@Override
	public String toString() {
		return "RideRequestJSON [channel=" + channel + ", text=" + text + "]";
	}
}
