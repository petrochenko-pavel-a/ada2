package com.onpositive.slack.client;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.MethodsClient;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.rtm.RTMClient;
import com.github.seratch.jslack.lightning.App;
import com.github.seratch.jslack.lightning.context.builtin.ActionContext;
import com.github.seratch.jslack.lightning.handler.builtin.BlockActionHandler;
import com.github.seratch.jslack.lightning.request.builtin.BlockActionRequest;
import com.github.seratch.jslack.lightning.response.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onpositive.slack.data.IConversationHandler;
import com.onpositive.slack.data.IMessenger;
import com.onpositive.slack.data.impl.ConversationManager;
import com.onpositive.slack.data.impl.Message;

public class Client {
	
	protected HashMap<String, Action>actions=new HashMap<>();
	
	protected HashMap<String, UpdatableMessage>settings=new HashMap<>();
	
	protected HashMap<Action, UpdatableMessage>messages=new HashMap<>();
	
	protected BlockActionHandler ew = new BlockActionHandler() {

		@Override
		public Response apply(BlockActionRequest req, ActionContext context)
				throws IOException, SlackApiException {
			List<com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload.Action> actions2 = req.getPayload().getActions();
			actions2.forEach(v->{
				String actionId = v.getActionId();
				if (settings.containsKey(actionId)) {
					UpdatableMessage updatableMessage = settings.get(actionId);
					boolean menuOption = updatableMessage.menuOption(v.getSelectedOption().getValue());
					if (menuOption) {
						rerender(updatableMessage);
					}
				}					
				if (actions.containsKey(actionId)) {
					Action action = actions.get(actionId);
					boolean run = action.run();
					if (run) {
						UpdatableMessage updatableMessage = messages.get(action);
						rerender(updatableMessage);
					}
				}
			});
			return Response.ok();
		}

		private void rerender(UpdatableMessage updatableMessage) {
			updatableMessage.actionBlocks.values().forEach(va->{
				messages.remove(va);
			});
			updatableMessage.actionBlocks.keySet().forEach(va->{
				actions.remove(va);
			});
			updatableMessage.actionBlocks.clear();
			updatableMessage.update(methods);
			afterSend(updatableMessage);
		}
	};

	protected MethodsClient methods;

	private IConversationHandler handler;
	
	public Client(IConversationHandler handler) {
		this.handler=handler;
	}
	
	@SuppressWarnings("deprecation")
	public void start() {
		App app = new App();
		String token = System.getenv("SLACK_BOT_API_TOKEN");
		JsonParser jsonParser = new JsonParser();
		Slack slack = new Slack();
		ConversationManager instance = ConversationManager.getInstance();
		instance.add(handler);
		methods = slack.methods(token);
		try (RTMClient rtm = slack.rtm(token)) {
			rtm.connect();
			User connectedBotUser = rtm.getConnectedBotUser();

			
			rtm.addMessageHandler((message) -> {
				JsonObject json = jsonParser.parse(message).getAsJsonObject();
				if (json.get("type") != null) {
					if (json.get("type").getAsString().equals("message")) {
						SlackMessage fromJson = new Gson().fromJson(message, SlackMessage.class);
						if (!fromJson.getUser().equals(connectedBotUser.getId())) {
							instance.handle(new Message(fromJson.getUser(), fromJson.getText()), new IMessenger() {
								
								@Override
								public void reply(String text) {
									try {
										methods.chatPostMessage(r->{
											r.text(text).channel(fromJson.getChannel());
											return r;
										});										
									} catch (IOException | SlackApiException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								@Override
								public void post(UpdatableMessage um) {
									
									um.send(methods,fromJson.getChannel());
									afterSend(um);
								}
							});
						}
					}
				}
			});
			app.blockAction(Pattern.compile(".*"), ew);
			SlackAppServer server = new SlackAppServer(app, 8080);			
			try {
				server.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void afterSend(UpdatableMessage um) {
		if (um.uid!=null) {
			settings.put(um.uid, um);
		}
		actions.putAll(um.actionBlocks);
		um.actionBlocks.values().forEach(v->{
			messages.put(v, um);
		});
	}

//	public static void main(String[] args) {
//		new Client().start();
//	}

	
}
