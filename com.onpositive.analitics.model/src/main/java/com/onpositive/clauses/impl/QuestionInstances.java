package com.onpositive.clauses.impl;

import com.onpositive.analitics.model.IType;

public class QuestionInstances extends AllInstancesOf{

	private final boolean direct;

	public boolean isDirect() {
		return direct;
	}

	public QuestionInstances(IType tp,boolean direct) {
		super(tp);
		this.direct=direct;
	}

	@Override
	public String toString() {
		if (direct) {
			return super.toString()+"?";
		}
		else {
			return "?"+super.toString();
		}
	}
}
