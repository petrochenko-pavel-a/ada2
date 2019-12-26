package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum Month {
	JANUARY("январь", "января", "январе", "январю", "январем"),
	FEBRUARY("февраль", "февраля", "феврале", "февралю", "февралем"),
	MARCH("март", "марта", "марте", "марту", "мартом"),
	APRIL("апрель", "апреля", "апреле", "апрелю", "апрелем"),
	MAY("май", "мая", "мае", "маю", "маем"),
	JUNE("июнь", "июня", "июне", "июню", "июнем"),
	JULY("июль", "июля", "июле", "июлю", "июлем"),
	AUGUST("август", "августа", "августе", "августу", "августом"),
	SEPTEMBER("сентябрь", "сентября", "сентябре", "сентябрю", "сентябрем"),
	OCTOBER("октябрь", "октября", "октябре", "октябрю", "октябрем"),
	NOVEMBER("ноябрь", "ноября", "ноябре", "ноябрю", "ноябрем"),
	DECEMBER("декабрь", "декабря", "декабре", "декабрю", "декабрем"),
	MONTH("месяц", "месяца", "месяцу", "месяцем", "месяце");
	
	private HashSet<String> forms;
	private String name2;
	private String name1;
	
	private Month(String... forms) {
		name1 = forms[0];
		name2 = forms[1];
		this.forms = Sets.newHashSet(forms);
	}
	public String getName() {
		return name1;
	}
	public String getDayName() {
		return name2;
	}
	
	public int getMonthNumber() {
		return this.ordinal();
	}
	
	public static Month fromMonthNumber(int monthNumber) {
		int ordinal = monthNumber;
		Month[] values = Month.values();
		
		if (ordinal < 0 || ordinal > values.length - 1) {
			return null;
		}
		
		return values[ordinal];
	}
	
	public List<String> getForms() {
		return Lists.newArrayList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (Month currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}

	public static List<String> getMonthNamesForms() {
		List<String> result = new ArrayList<String>();
		
		for (Month currentValue : values()) if (currentValue != MONTH) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}		
	public static Month fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		String lowercasedNameForm = nameForm.toLowerCase();
		for (Month currentValue : Month.values()) {
			if (currentValue.forms.contains(lowercasedNameForm))
				return currentValue;
		}
		return null;		
	}
}
