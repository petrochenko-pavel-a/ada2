package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum Day {
	MONDAY("понедельник(?:а|е|у|ом)?","понедельник", "понедельника", "понедельнику", "понедельнике", "понедельником"),
	TUESDAY("вторник(?:а|е|у|ом)?","вторник", "вторника", "вторнику", "вторнике", "вторником"),
	WEDNESDAY("сред(?:а|е|ы|у|ой)","среда", "среды", "среде", "среду", "средой"),
	THURSDAY("четверг(?:а|е|у|ом)?","четверг", "четверга", "четвергу", "четверге", "четвергом"),
	FRIDAY("пятниц(?:а|е|ы|у|ой)","пятница", "пятницы", "пятнице", "пятницой", "пятницу"),
	SATURDAY("суббот(?:а|е|ы|у|ой)","суббота", "субботы", "субботе", "субботой", "субботу"),
	SUNDAY("воскресень(?:я|е|ю|ем)","воскресенье", "воскресенья", "воскресенью", "воскресеньем"),
	DAY("(?:день|дн(?:я|е|ю|ем|ём)|сут(?:ок|к(?:и|ам|ами)))","день", "днём", "дня", "дню", "дне", "сутки", "суток", "суткам", "сутками"),
	DAYS("дн(?:и|ями|ям|ей|ях)","дни", "днями", "дням", "дней", "днях");
	
	private HashSet<String> forms;
	private String regexp;
	private Day(String regexp, String... forms) {
		this.regexp = regexp;
		this.forms = Sets.newHashSet(forms);
	}
		
	public List<String> getForms() {
		return Lists.newArrayList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (Day currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}

	public static Day fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		String lowercasedNameForm = nameForm.toLowerCase();
		for (Day currentValue : values()) {
			List<String> forms = currentValue.getForms();
			if (forms.contains(lowercasedNameForm))
				return currentValue;
		}
		return null;		
	}

	public static Collection<? extends String> getRegexps() {
		List<String> result = new ArrayList<String>();
		for (Day currentValue : values()) {
			result.add(currentValue.regexp);
		}
		return result;
	}
}