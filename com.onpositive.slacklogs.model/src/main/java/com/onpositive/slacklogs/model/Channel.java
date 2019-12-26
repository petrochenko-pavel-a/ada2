package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.onpositive.analitics.model.IEntity;
import com.onpositive.analitics.model.KeyProperty;
import com.onpositive.analitics.model.Labels;
import com.onpositive.analitics.model.java.InverseOf;
import com.onpositive.analitics.model.java.Property;

@Labels("channels")
public class Channel implements Serializable,IEntity{

	public Channel(String name2) {
		this.name=name2;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Property
	@KeyProperty
	protected String name;
	
	@Property
	@Labels("messages")
	@InverseOf("channel")
	protected ArrayList<Message>messages=new ArrayList<Message>();

	@Override
	public String toString() {
		return name;
	}
	
}
