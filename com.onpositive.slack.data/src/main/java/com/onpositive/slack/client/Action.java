package com.onpositive.slack.client;

public abstract class Action {
	
	protected String caption;
	
	public Action(String caption) {
		super();
		this.caption = caption;
	}

	public final String getCaption() {
		return caption;
	}
	
	public abstract boolean run() ;
}
