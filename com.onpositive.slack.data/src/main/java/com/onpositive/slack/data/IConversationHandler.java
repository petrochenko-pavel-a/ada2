package com.onpositive.slack.data;

public interface IConversationHandler {

	public boolean handle(IMessage message,IConversation conversation);
}
