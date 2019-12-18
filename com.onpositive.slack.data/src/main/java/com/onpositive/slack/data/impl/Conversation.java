package com.onpositive.slack.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.slack.client.UpdatableMessage;
import com.onpositive.slack.data.IContext;
import com.onpositive.slack.data.IConversation;
import com.onpositive.slack.data.IMessage;
import com.onpositive.slack.data.IMessenger;

public class Conversation implements IConversation,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected transient IMessenger messanger;
	
	public IMessenger getMessanger() {
		return messanger;
	}

	public void setMessanger(IMessenger messanger) {
		this.messanger = messanger;
	}

	ArrayList<IMessage>messages=new ArrayList<>();
	IContext ctx= new Context();
	
	@Override
	public List<IMessage> messages() {
		return messages;
	}

	@Override
	public IContext getContext() {
		return ctx;
	}

	@Override
	public void reply(String string) {
		this.messanger.reply(string);
	}

	@Override
	public void send(UpdatableMessage message) {
		messanger.post(message);
	}

}
