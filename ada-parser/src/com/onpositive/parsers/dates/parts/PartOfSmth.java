package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum PartOfSmth {
	BEGINNING("начало", "начала", "начале", "началу", "началом"),
	END("конец", "конца", "конце", "концу", "концом"),
	MIDDLE("середина", "середины", "середине", "серединой", "середину");
	
	private List<String> forms;
	
	private PartOfSmth(String... forms) {
		this.forms = Lists.newArrayList(forms);
	}
	
	public List<String> getForms() {
		return Collections.unmodifiableList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (PartOfSmth currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}
	
	public static PartOfSmth fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		
		String lowercasedNameForm = nameForm.toLowerCase();
		
		for (PartOfSmth currentValue : PartOfSmth.values()) {
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