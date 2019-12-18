package com.onpositive.slack.data.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.onpositive.slack.data.IContext;

public class Context implements IContext,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected LinkedHashMap<String, Object>settings;
	
	@Override
	public Map<String, Object> settings() {
		return settings;
	}

}
