package com.onpositive.slack.client;

public interface IContentProducer {

	String key();
	
	byte[] content();
	
	String mimeType();
}
