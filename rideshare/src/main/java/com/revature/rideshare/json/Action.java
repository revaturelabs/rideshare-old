package com.revature.rideshare.json;

import java.util.ArrayList;

public class Action {
	private String name;
	private String text;
	private String type;
	private String value;
	private ArrayList<Option> options;
	
	public Action() {}
	
	public Action(String name, String text, String type, ArrayList<Option> options) {
		super();
		this.name = name;
		this.text = text;
		this.type = type;
		this.options = options;
	}
	
	public Action(String name, String text, String type, String value) {
		super();
		this.name = name;
		this.text = text;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<Option> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<Option> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "Action [name=" + name + ", text=" + text + ", type=" + type + ", value=" + value + ", options="
				+ options + "]";
	}
}
