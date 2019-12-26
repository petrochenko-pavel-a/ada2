package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum MonthPart {
	BEGINNING("начало", "начала", "начале", "началу", "началом"),
	END("конец", "конца", "конце", "концу", "концом"),
	MIDDLE("середина", "середины", "середине", "серединой", "середину");
	
	private List<String> forms;
	
	private MonthPart(String... forms) {
		this.forms = Lists.newArrayList(forms);
	}
	
	public String getName() {
		return forms.get(0);
	}
	
	public static MonthPart fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		
		String lowercasedNameForm = nameForm.toLowerCase();
		
		for (MonthPart currentValue : MonthPart.values()) {
			List<String> forms = currentValue.getForms();
			
			for (String currentForm : forms) {
				if (lowercasedNameForm.equals(currentForm)) {
					return currentValue;
				}
			}
		}
		
		return null;
	}
	
	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (MonthPart currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}
	
	public List<String> getForms() {
		return Collections.unmodifiableList(forms);
	}
}
