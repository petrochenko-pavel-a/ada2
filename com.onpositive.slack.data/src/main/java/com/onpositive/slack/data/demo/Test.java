package com.onpositive.slack.data.demo;

import com.onpositive.slack.client.Client;
import com.onpositive.slack.data.IConversation;
import com.onpositive.slack.data.IConversationHandler;
import com.onpositive.slack.data.IMessage;

public class Test {

	
	public static void main(String[] args) {
		new Client(new IConversationHandler() {
			
			@Override
			public boolean handle(IMessage message, IConversation conversation) {
				//ParsedQuery parse = engine.parse(message.text());
				//QueryResult execute = parse.execute();
				//conversation.reply(parse.toString());
				return false;
			}
		}).start();
	}

}
