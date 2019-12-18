package com.onpositive.slack.data;

import com.onpositive.slack.client.UpdatableMessage;

public interface IMessenger {

	void reply(String text);

	void post(UpdatableMessage um);
}
