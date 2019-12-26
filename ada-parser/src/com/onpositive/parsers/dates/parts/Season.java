package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum Season {
	SPRING("весна", "весны", "весной", "весне", "весну"),
	SUMMER("лето", "лета", "летом", "лету"),
	FALL("осень", "осени", "осенью", "осени"),
	WINTER("зима", "зимы", "зимой", "зиме", "зиму");
	
	private List<String> forms;
	
	private Season(String... forms) {
		this.forms = Lists.newArrayList(forms);
	}
	
	public List<String> getForms() {
		return Collections.unmodifiableList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (Season currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}
	
	public static Season fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		
		String lowercasedNameForm = nameForm.toLowerCase();
		
		for (Season currentValue : Season.values()) {
			List<String> forms = currentValue.getForms();
			
			for (String currentForm : forms) {
				if (lowercasedNameForm.equals(currentForm)) {
					return currentValue;
				}
			}
		}
		
		return null;
	}
}