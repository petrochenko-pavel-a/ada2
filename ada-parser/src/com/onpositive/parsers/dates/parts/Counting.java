package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum Counting {
	FIRST("первый", "первого", "первому", "первым", "первом",
			"первая", "первой", "первую",
			"первое",
			"первые", "первых", "первым", "первыми"),
	SECOND("второй", "второго", "второму", "вторым", "втором",
			"вторая", "второй", "вторую",
			"второе",
			"вторые", "вторых", "вторым", "вторыми"),
	THIRD("третий", "третьего", "третьему", "третьим", "третьем",
			"третья", "третьей", "третью",
			"третье",
			"третьи", "третьих", "третьим", "третьими"),
	FOURTH("четвёртый", "четвёртого", "четвёртому", "четвёртым", "четвёртом",
			"четвёртая", "четвёртой", "четвёртую",
			"четвёртое",
			"четвертые", "четвертых", "четвертым", "четвертыми",
			"четвертый", "четвертого", "четвертому", "четвертым", "четвертом",
			"четвертая", "четвертой", "четвертую",
			"четвертое",
			"четвёртые", "четвёртых", "четвёртым", "четвёртыми");

	private HashSet<String> forms;
	
	private Counting(String... forms) {
		this.forms = Sets.newHashSet(forms);
	}
	
	public List<String> getForms() {
		return Lists.newArrayList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (Counting currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}

	public static Counting fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		String lowercasedNameForm = nameForm.toLowerCase();
		for (Counting currentValue : Counting.values()) {
			List<String> forms = currentValue.getForms();
			if (forms.contains(lowercasedNameForm))
				return currentValue;
		}
		return null;		
	}
}
