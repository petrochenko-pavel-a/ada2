package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.FreeFormDate.YearsCount;
import com.onpositive.parsers.dates.FreeFormDateRange;
import com.onpositive.parsers.dates.IFreeFormDate;

/**
 * 192 год до н.э. 
 * 1901-1945 гг
 */
public class DTBCCustomCases extends DateTemplate{
	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
		if (pattern == null) {
			String yearSign = oneOf(Lists.newArrayList("год","года","годы","гг", "г","году","годах"), true);
			String year = "(\\d{1,4})" + optional(some(SPACE_DELIMITTER) + yearSign);
			String epochSign = simpleBrackets(oneOf("до н.э.","до н. э.","до нашей эры"));		
			pattern = Pattern.compile(VALID_START+rangeTemplate.getRangePattern(year)+ some(SPACE_DELIMITTER)+ epochSign+VALID_END);
			//LoggerFactory.getLogger(getClass()).info(VALID_START+rangeTemplate.getRangePattern(year)+ some(SPACE_DELIMITTER)+ epochSign+VALID_END);
		}
		return pattern;
	}
	
	//private boolean yearSign = false;
	protected FreeFormDate getDateByShift(Matcher match,int shift)
	{
		//yearSign = false;
		String year = match.group(0+shift);
		//yearSign = (match.group(1+shift) != null);
		Integer yearInt = null;
		try {
			yearInt = Integer.parseInt(year);
			if (yearInt <= 0 || yearInt > MAX_YEAR_ACCEPT) {
				return null;
			}
		} catch (Throwable th) {
		}
			
		if (yearInt != null ) {
			return new FreeFormDate(yearInt, null, null, null, null, YearsCount.ONE);
		} 
		return null;
	}
	
	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		FreeFormDate date1 = getDateByShift(match, 1);
		FreeFormDate date2 = null;
		IFreeFormDate result = null;
		if (date1 != null) {
			date2 = getDateByShift(match, 3);	
			if (date2 != null)
			{
				convertToBC(date1);
				convertToBC(date2);
				result = new FreeFormDateRange(date1,date2);
			}
		}

		return result;
	}
}
