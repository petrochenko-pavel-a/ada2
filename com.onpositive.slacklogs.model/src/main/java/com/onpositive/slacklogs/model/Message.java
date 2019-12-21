package com.onpositive.slacklogs.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Message implements Serializable{

	public Message(User user,String text) {
		this.from=user;
		this.text=text;
	}
	
	public static class Reaction implements Serializable{
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
	
	protected User from;
	protected String text;
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
	
	public List<Reaction>reactions(){
		if (reactions==null) {
			return Collections.emptyList();
		}
		return Arrays.asList(reactions);
	}
	
	public Instant getDate() {
		ts=ts.substring(0,ts.indexOf('.'));
		long parseLong = Long.parseLong(ts);
		Instant instant = Instant.ofEpochSecond(parseLong) ;
		return instant;
	}
}
