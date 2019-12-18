package com.onpositive.slack.client;

public class SlackMessage {
    private String channel;
    
    public String getChannel() {
		return channel;
	}
	
    public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getParentUserId() {
		return parentUserId;
	}
	public void setParentUserId(String parentUserId) {
		this.parentUserId = parentUserId;
	}
	public String getThreadTs() {
		return threadTs;
	}
	public void setThreadTs(String threadTs) {
		this.threadTs = threadTs;
	}
	public String getEventTs() {
		return eventTs;
	}
	public void setEventTs(String eventTs) {
		this.eventTs = eventTs;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	private String user;

    private String text;
    
    private String ts;

    private String parentUserId; // in the case of replies in thread
    private String threadTs; // in the case of replies in thread

    private String eventTs;
    private String channelType; // app_home, channel, group, im, mpim
}
