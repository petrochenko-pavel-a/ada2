package com.onpositive.slacklogs.model2;

import java.util.List;


public interface IChannel {
	
	String name();

	List<IMessage>messages();

	boolean isPopular();
}
