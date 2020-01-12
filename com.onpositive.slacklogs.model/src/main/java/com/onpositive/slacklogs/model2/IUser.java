package com.onpositive.slacklogs.model2;

import java.util.List;


public interface IUser {

	List<IChannel>channels();
	
	List<IMessage>messages();
}
