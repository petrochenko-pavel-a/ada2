package com.onpositive.slack.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.github.seratch.jslack.api.methods.MethodsClient;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatUpdateResponse;
import com.github.seratch.jslack.api.model.block.ActionsBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;

public abstract class UpdatableMessage {

	private String ts;
	
	protected HashMap<String, Action>actionBlocks=new HashMap<>();

	private String channel;
	
	protected String uid=UUID.randomUUID().toString();

	protected void update(MethodsClient client) {
		try {
			this.actionBlocks.clear();
			ChatUpdateResponse chatUpdate = client.chatUpdate(r -> {
				r.ts(ts);
				r.channel(channel);
				r.blocks(getBlocks());
				return r;
			});
			ts = chatUpdate.getTs();
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
	}
	protected void send(MethodsClient client,String channel) {
		try {
			this.actionBlocks.clear();
			this.channel=channel;
			ChatPostMessageResponse chatUpdate = client.chatPostMessage(r -> {
				
				r.blocks(getBlocks());
				r.channel(channel);
				return r;
			});
			ts = chatUpdate.getTs();
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
	}
	protected boolean menuOption(String value) {
		return false;		
	}

	protected List<LayoutBlock> getBlocks() {
		ArrayList<Action> actions = new ArrayList<>();
		ArrayList<LayoutBlock>content=new ArrayList<>();
		fillContent(content);
		fillActions(actions);
		
		if (!actions.isEmpty()) {
			ActionsBlock ab=new ActionsBlock();
			for (Action a : actions) {
				ButtonElement bl=new ButtonElement();
				String uniqueID = UUID.randomUUID().toString();
				bl.setText(new PlainTextObject(a.getCaption(), false));
				bl.setActionId(uniqueID);
				actionBlocks.put(uniqueID, a);
				ab.getElements().add(bl);
			}			
			content.add(ab);
		}
		return content;
	}

	protected abstract void fillContent(ArrayList<LayoutBlock> content);

	protected abstract void fillActions(ArrayList<Action> actions) ;

}
