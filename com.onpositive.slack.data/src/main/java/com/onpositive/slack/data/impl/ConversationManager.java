package com.onpositive.slack.data.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.slack.data.IConversationHandler;
import com.onpositive.slack.data.IMessage;
import com.onpositive.slack.data.IMessenger;

public class ConversationManager {

	private ArrayList<IConversationHandler>handlers=new ArrayList<>();
	
	public boolean add(IConversationHandler e) {
		return handlers.add(e);
	}
	public boolean remove(IConversationHandler o) {
		return handlers.remove(o);
	}

	private HashMap<String, Conversation>conversations=new HashMap<>();
	
	public void handle(IMessage message,IMessenger messanger) {
		Conversation cv=conversations.get(message.from());
		if (cv==null) {
			cv=new Conversation();
			conversations.put(message.from(), cv);
		}
		cv.setMessanger(messanger);
		for (IConversationHandler h:handlers) {
			if (h.handle(message, cv)) {
				break;
			}
		}
		cv.messages.add(message);
	}
	
	static ConversationManager instance=new ConversationManager();
	public static ConversationManager getInstance() {
		
		return instance;
	}

}
