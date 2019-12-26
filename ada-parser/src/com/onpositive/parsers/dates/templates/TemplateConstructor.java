package com.onpositive.parsers.dates.templates;

import java.util.ArrayList;
import java.util.List;

public class TemplateConstructor {
	protected static String oneOf(String... args) {
		List<String> vals = new ArrayList<String>();
		for (String arg : args) {
			vals.add(arg);
		}
		
		return oneOf(vals);
	}
	protected static String oneOf(List<String> potentialValues) {
		return oneOf(potentialValues, false);
	}
		
	protected String oneOf(List<String> potentialValues, String groupName) {
		return  oneOf(potentialValues,groupName,true);
	}
	protected static String oneOf(List<String> potentialValues, String groupName, boolean capturing) {
		StringBuilder result = new StringBuilder();
		result.append("(");
		if (!capturing){
			result.append("?:");
		}
		else if (!groupName.isEmpty()) {
			result.append("?<").append(groupName).append(">");
		}
		
		for (int i =0; i < potentialValues.size(); i++) {
			String value = potentialValues.get(i);
			result.append(value);
			if (i < potentialValues.size() - 1) {
				result.append("|");
			}
		}
		
		result.append(")");
		
		return result.toString();
	}
	protected static String oneOf(List<String> potentialValues, boolean capturing) {

		return  oneOf(potentialValues,"",capturing);
	}

	protected String simpleBrackets(String regexp) {
		return "(" + regexp + ")";
	}
	protected String brackets(String regexp) {
		return "(?:" + regexp + ")";
	}
	
	protected String optional(String regexp) {
		return "(?:" + regexp + ")?";
	}
	
	protected String some(String regexp) {
		return "(?:" + regexp + ")*";
	}
	
	protected String oneOrMore(String regexp) {
		return "(?:" + regexp + ")+";
	}
}
