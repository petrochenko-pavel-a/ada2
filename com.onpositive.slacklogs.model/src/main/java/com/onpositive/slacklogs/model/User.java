package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.analitics.model.IEntity;
import com.onpositive.analitics.model.KeyProperty;
import com.onpositive.analitics.model.Labels;
import com.onpositive.analitics.model.Lazy;
import com.onpositive.analitics.model.java.InverseOf;
import com.onpositive.analitics.model.java.Property;
import com.onpositive.slacklogs.model.Message.Reaction;

@Labels({"user","member"})
public class User implements Serializable,IEntity{

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
	
	@Override
	public String toString() {
		return this.real_name;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Property
	protected String name;
	
	@Property
	@KeyProperty
	protected String real_name;
	
	protected String url;
	
	transient Workspace workspace;

	protected List<Message>messages;
	protected List<Reaction>reactions;
	

	@Property
	@Labels("reactions")
	public List<Reaction>reactions(){
		ArrayList<Reaction>rs=new ArrayList<>();
		this.messages().forEach(v->{
			rs.addAll(v.reactions());
		});
		return rs;		
	}
	@Property
	@Labels("reactions")
	public List<Reaction>reacted(){
		ArrayList<Reaction>rs=new ArrayList<>();
		this.messages().forEach(v->{
			rs.addAll(v.reactions());
		});
		return rs;		
	}
	
	@Property
	@Labels("messages")
	@InverseOf("from")
	@Lazy
	public List<Message> messages() {
		if (messages==null) {
			return Collections.emptyList();
		}
		return messages;
	}
}
