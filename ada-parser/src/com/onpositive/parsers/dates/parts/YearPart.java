package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum YearPart {
	BEGINNING("начало", "начала", "начале", "началу", "началом"),
	END("конец", "конца", "конце", "концу", "концом"),
	MIDDLE("середина", "середины", "середине", "серединой", "середину"),
	SPRING("весна", "весны", "весной", "весне"),
	SUMMER("лето", "лета", "летом", "лету"),
	FALL("осень", "осени", "осенью", "осени"),
	WINTER("зима", "зимы", "зимой", "зиме");

	private List<String> forms;

	private YearPart(String... forms) {
		this.forms = Lists.newArrayList(forms);
	}

	public String getName() {
		return forms.get(0);
	}

	public static YearPart fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}

		String lowercasedNameForm = nameForm.toLowerCase();

		for (YearPart currentValue : YearPart.values()) {
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

		for (YearPart currentValue : values()) {
			result.addAll(currentValue.getForms());
		}

		return result;
	}

	public List<String> getForms() {
		return Collections.unmodifiableList(forms);
	}
}