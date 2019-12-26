package com.onpositive.parsers.dates.parts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum TimeAdj {
	PASTTIME("прошлый", "прошлого", "прошлому", "прошлым", "прошлом",
			"прошлая", "прошлой", "прошлую",
			"прошлое",
			"прошлые", "прошлых", "прошлым", "прошлыми",
			"предыдущий", "предыдущего", "предыдущему", "предыдущим", "предыдущем",
			"предыдущая", "предыдущей", "предыдущую",
			"предыдущее",
			"предыдущие", "предыдущих", "предыдущим", "предыдущими",
			"прошедший", "прошедшего", "прошедшему", "прошедшим", "прошедшем",
			"прошедшая", "прошедшей", "прошедшую",
			"прошедшее", 
			"прошедшие", "прошедших", "прошедшим", "прошедшими",
			"минувший", "минувшего", "минувшему", "минувшим", "минувшем",
			"минувшая", "минувшей", "минувшую",
			"минувшее",
			"минувшие", "минувших", "минувшим", "минувшими",
			"истёкший", "истёкшего", "истёкшему", "истёкшим", "истёкшем",
			"истёкшая", "истёкшей", "истёкшую",
			"истёкшее",
			"истёкшие", "истёкших", "истёкшим", "истёкшими",
			"истекший", "истекшего", "истекшему", "истекшим", "истекшем",
			"истекшая", "истекшей", "истекшую",
			"истекшее",
			"истекшие", "истекших", "истекшим", "истекшими",
			
			"предшествующий", "предшествующего", "предшествующему", "предшествующим", "предшествующем",
			"предшествующая", "предшествующей", "предшествующую",
			"предшествующее",
			"предшествующие", "предшествующих", "предшествующим", "предшествующими"
			/*,
			"позапрошлый", "позапрошлого", "позапрошлому", "позапрошлым", "позапрошлом",
			"позапрошлая", "позапрошлой", "позапрошлую",
			"позапрошлое",
			"позапрошлые", "позапрошлых", "позапрошлым", "позапрошлыми"*/),
	PRESENTTIME("настоящий", "настоящего", "настоящему", "настоящим", "настоящем",
			"настоящая", "настоящей", "настоящую",
			"настоящее",
			"настоящие", "настоящих", "настоящим", "настоящими",
			"текущий", "текущего", "текущему", "текущим", "текущем",
			"текущая", "текущей", "текущую",
			"текущее",
			"текущие", "текущих", "текущим", "текущими",
			"нынешний", "нынешнего", "нынешнему", "нынешним", "нынешнем",
			"нынешная", "нынешней", "нынешную",
			"нынешнее",
			"нынешние", "нынешних", "нынешним", "нынешними",
			"этот", "этого", "этому", "этим", "этом",
			"эта", "этой", "эту",
			"это",
			"эти", "этих", "этим", "этими"),
	FUTURETIME("следующий", "следующего", "следующему", "следующим", "следующем",
			"следующая", "следующей", "следующую",
			"следующее",
			"следующие", "следующих", "следующим", "следующими",
			"грядущий", "грядущего", "грядущему", "грядущим", "грядущем",
			"грядущая", "грядущей", "грядущую",
			"грядущее",
			"грядущие", "грядущих", "грядущим", "грядущими",
			"ближайший", "ближайшего", "ближайшему", "ближайшим", "ближайшем",
			"ближайшая", "ближайшей", "ближайшую",
			"ближайшее",
			"ближайшие", "ближайших", "ближайшим", "ближайшими",
			"предстоящий", "предстоящего", "предстоящему", "предстоящим", "предстоящем",
			"предстоящая", "предстоящей", "предстоящую",
			"предстоящее",
			"предстоящие", "предстоящих", "предстоящим", "предстоящими",
			"будущий", "будущего", "будущему", "будущим", "будущем",
			"будущая", "будущей", "будущую",
			"будущее",
			"будущие", "будущих", "будущим", "будущими");
	
	private HashSet<String> forms;
	
	private TimeAdj(String... forms) {
		this.forms = Sets.newHashSet(forms);
	}
	
	public List<String> getForms() {
		return Lists.newArrayList(forms);
	}

	public static List<String> getAllForms() {
		List<String> result = new ArrayList<String>();
		
		for (TimeAdj currentValue : values()) {
			result.addAll(currentValue.getForms());
		}
		
		return result;
	}

	public static TimeAdj fromNameForm(String nameForm) {
		if (nameForm == null) {
			return null;
		}
		String lowercasedNameForm = nameForm.toLowerCase();
		for (TimeAdj currentValue : TimeAdj.values()) {
			List<String> forms = currentValue.getForms();
			if (forms.contains(lowercasedNameForm))
				return currentValue;
		}
		return null;		
	}
}	