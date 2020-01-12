package com.onpositive.slacklogs.model;

import com.onpositive.analitics.model.ICounter;

public class ReactionCounter implements ICounter{

	@Override
	public int count(Object o) {
		Message.Reaction r=(Message.Reaction) o;
		return r.count;
	}

}
