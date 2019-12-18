package com.onpositive.slack.data.impl;

import java.io.Serializable;

import com.onpositive.slack.data.IMessage;

public class Message implements IMessage,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user;
	private String text;
	
	public Message(String user, String text) {
		super();
		this.user = user;
		this.text = text;
	}

	@Override
	public String from() {
		return user;
	}

	@Override
	public String text() {
		return text;
	}

}
