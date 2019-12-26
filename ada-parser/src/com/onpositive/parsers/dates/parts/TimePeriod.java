package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum TimePeriod {
	QUARTER("квартал", "квартала", "кварталу", "квартале", "кварталом"),
	HALF_YEAR("полгода", "полугода", "полугоду", "полугодом", "полугоде",
			"полугодие", "полугодия", "полугодию", "полугодием", "полугодии"),
	DECADE("декада", "декады", "декаде", "декадой", "декаду"),
	WEEK("неделя", "недели", "неделе", "неделей", "неделю"),
	WEEKEND("выходные", "выходных", "выходными", "выходным"),
	YEAR("год", "года", "году", "годом", "годе", "лет"),
	CENTURY("век", "века", "веку", "веком", "веке");
	
	private HashSet<String> forms;
	
	private TimePeriod(String... forms) {
		this.forms = Sets.newHashSet(forms);
	}
	
	public List<String> getForms() {
		return Lists.newArrayList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (TimePeriod currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}

	public static TimePeriod fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		String lowercasedNameForm = nameForm.toLowerCase();
		for (TimePeriod currentValue : TimePeriod.values()) {
			List<String> forms = currentValue.getForms();
			if (forms.contains(lowercasedNameForm))
				return currentValue;
		}
		return null;		
	}
}