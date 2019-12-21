package com.onpositive.clauses.impl;

import com.ada.model.Context;
import com.onpositive.analitics.model.Builtins;
import com.onpositive.clauses.ISelector;

public class ThemInstances extends AllInstancesOf{

	protected ISelector actualSelector;
	
	public ISelector getActualSelector() {
		return actualSelector;
	}

	public void setActualSelector(ISelector actualSelector) {
		this.actualSelector = actualSelector;
	}

	public ThemInstances() {
		super(Builtins.ALLMATCH);
	}
	@Override
	public String toString() {
		if (actualSelector==null){
			return "THEM(UNSOLVED)";
		}
		return "them("+actualSelector.domain().toString()+"*"+")";
	}

	@Override
	public void initFromContext(Context ct) {
		this.actualSelector=ct.getThem();
		super.initFromContext(ct);
	}
	
	

}
