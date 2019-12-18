package com.onpositive.slack.data;

import java.util.List;

import com.onpositive.slack.client.UpdatableMessage;

public interface IConversation {

	List<IMessage>messages();
	
	IContext getContext();
	
	
	default IMessage currentMessage() {
		return messages().get(messages().size()-1);
	}

	void reply(String string);
	
	void send(UpdatableMessage message);
}
