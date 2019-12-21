package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class User implements Serializable{

	private String id;

	public User(String id) {
		this.setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String name;
	
	protected String real_name;
	
	protected String url;
	
	transient Workspace workspace;

	List<Message>messages;
	
	

	public List<Message> messages() {
		if (messages==null) {
			messages=workspace.messages().stream().filter(x->x.from==this).collect(Collectors.toList());
		}
		return messages;
	}
}
