package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.analitics.model.IEntity;
import com.onpositive.analitics.model.Labels;
import com.onpositive.analitics.model.java.InverseOf;
import com.onpositive.analitics.model.java.Property;
import com.onpositive.analitics.model.java.TimeProp;

@Labels({"message","сообщение","пост","писулька"})
@TimeProp("date")
public class Message implements Serializable,IEntity{

	public Message(User user,String text) {
		this.from=user;
		this.text=text;
	}
	
	@Labels("reaction")
	public static class Reaction implements Serializable,IEntity{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		String name;
		
		User[] users;
		int count;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Property()
	@Labels({"from","by"})
	@InverseOf("messages")
	protected User from;
	protected String text;
	
	@Property
	@InverseOf("messages")
	protected Channel channel;
	protected Reaction[] reactions;

	protected String ts;
	protected String thread_ts;

	
	public boolean isInThread() {
		return thread_ts!=null;
	}
	
	public List<Message>threadMessages(){
		return channel.messages.stream().filter(x->x.thread_ts==ts).collect(Collectors.toList());
	}
	
	@Property
	public List<Reaction>reactions(){
		if (reactions==null) {
			return Collections.emptyList();
		}
		return Arrays.asList(reactions);
	}
	
	@Property
	@Labels("date")
	public LocalDateTime date() {
		String ts=this.ts.substring(0,this.ts.indexOf('.'));
		long parseLong = Long.parseLong(ts);
		Instant instant = Instant.ofEpochSecond(parseLong) ;
		return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
	}
}
