package com.onpositive.nlp.lexer;

import java.util.ArrayList;
import java.util.HashMap;

public class SynonimForms {
	
	protected static HashMap<String, String>synonims=new HashMap<>();
	
	protected static HashMap<String, ArrayList<String>>backLinks=new HashMap<>();
	
	public static boolean isPossibleReplacement(String text,String actual) {
		if (synonims.containsKey(text)) {
			if (synonims.get(text).equals(actual)) {
				return true;
			}
		}
		return false;		
	}

	public static void register(String string, String c) {
		synonims.put(c, string);
		ArrayList<String> arrayList = backLinks.get(string);
		if (arrayList==null) {
			arrayList=new ArrayList<>();
			backLinks.put(string, arrayList);
		}
		arrayList.add(c);
	}
}
