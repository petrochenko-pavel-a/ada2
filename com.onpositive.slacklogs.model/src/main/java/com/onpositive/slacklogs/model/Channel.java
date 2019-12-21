package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable{

	public Channel(String name2) {
		this.name=name2;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String name;
	
	protected ArrayList<Message>messages=new ArrayList<Message>();

	
}
