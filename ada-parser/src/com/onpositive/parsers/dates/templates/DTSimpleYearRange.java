package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.FreeFormDateRange;
import com.onpositive.parsers.dates.IFreeFormDate;

public class DTSimpleYearRange  extends DateTemplate {

	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
			String dashRange = 
					"(\\d{4})" + 
					optional(some(DTRangeTemplate.delimiter)) + 
					DTRangeTemplate.dashe + 
					optional(some(DTRangeTemplate.delimiter)) + 
					"(\\d{4})";
			String yearInBrackets = some(DTRangeTemplate.delimiter)+"\\((\\d{4})\\)"; 
			pattern = Pattern.compile(VALID_START+oneOf(dashRange,yearInBrackets)+optional(VALID_END));
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
		if (text.substring(0, match.start()).trim().length() == 0)
			return null;
		//printMatch(match);
		FreeFormDate date1 = getDate(match, 1);
		FreeFormDate date2 = getDate(match, 2);
		IFreeFormDate result = null;
		if (date1 != null) {
			if (date2 != null)
				result = new FreeFormDateRange(date1, date2);
			else
			{
				result = date1;
			}
		}
		else {
			result = getDate(match, 3);
		}
		return result;
	}

}
