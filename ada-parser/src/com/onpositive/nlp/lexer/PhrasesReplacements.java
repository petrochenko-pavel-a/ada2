package com.onpositive.nlp.lexer;

import java.util.HashMap;

public class PhrasesReplacements {

	protected static HashMap<String, String>synonims=new HashMap<>();
	
	public static void replacement(String original,String result) {
		synonims.put(original, result);
	}
	public static  String preprocess(String d) {
		for (String k:synonims.keySet()) {
			d=d.replace((CharSequence)k, synonims.get(k));
		}
		return d;
	}
}
