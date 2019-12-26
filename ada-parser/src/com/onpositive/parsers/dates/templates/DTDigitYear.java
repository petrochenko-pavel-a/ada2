package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.IFreeFormDate;

/**
 * Годы без подписи. Это имеет смысл использовать для заголовков,
 * но в произвольных текстах может давать ошибки
 * 1950-1954
 */
public class DTDigitYear extends DateTemplate {

	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
			pattern = Pattern.compile("(\\d{4})");
		}
		return pattern;
	}

	private FreeFormDate getDate (Matcher match, int position)
	{
		String year = match.group(position);
		Integer yearInt = getYear(year);
		if (yearInt != null) {
			return new FreeFormDate(yearInt, null, null, null, null, null);
		} 
		return null;
	}
	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		FreeFormDate date1 = getDate(match, 1);
		IFreeFormDate result = null;
		if (date1 != null) 
			return date1;
		return result;
	}

}
