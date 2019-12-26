package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.FreeFormDateRange;
import com.onpositive.parsers.dates.IFreeFormDate;
import com.onpositive.parsers.dates.parts.Month;
/*
 * 1-15 июля 2010
 */
public class DTDaysInterval extends DateTemplate  {
	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
					
			List<String> dayPatterns = new ArrayList<String>();
			dayPatterns.add("[1-2]?\\d");
			dayPatterns.add("30");
			dayPatterns.add("31");
			String day = oneOf(dayPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> monthPatterns = new ArrayList<String>();
			monthPatterns.addAll(Month.getMonthNamesForms());
			String month = oneOf(monthPatterns, true) + some(SPACE_DELIMITTER);
			
			String year = oneOf(
					Lists.newArrayList("(\\d{4})" + optional(some(SPACE_DELIMITTER) + YEAR_SIGN),
					"(\\d{1,3})" + some(SPACE_DELIMITTER) + YEAR_SIGN), false
					);
			String date = rangeTemplate.getRangePattern(day) + month + optional(year);
			pattern = Pattern.compile(VALID_START+date+VALID_END);
		}
		return pattern;
	}
	
	protected Integer prevYear = null;
	protected boolean isPrevYearSet = false;
	
	protected Integer getYearIncludePrev(String year) {
		if (year == null) {
			isPrevYearSet = true;
			return prevYear;
		}
		Integer resYear = super.getYear(year); 
		prevYear = resYear;
		return resYear;
	}
	public void clear() {
		prevYear = null;
	}
	
	private boolean yearSign = false;
	protected Integer getDayFromMatch(Matcher match)
	{
		String day = match.group(1);
		if (day == null)
			day = match.group(3);
		return getDay(day);
	}

	protected FreeFormDate getDate(Matcher match, LocalDate baseDate)
	{
		yearSign = false;
		String day = match.group(2);
		if (day == null)
			day = match.group(4);
		if (day == null)
			return null;
		
		String month = match.group(6);		
		String year = match.group(7);
		yearSign = (match.group(8) != null);
		
		Integer dayInt = getDay(day);

		Month monthObj = null;
		try {
			monthObj = Month.fromNameForm(month);
		} catch (Throwable th){}
		
		Integer yearInt;
		if ((year != null&&yearSign) || monthObj != null || dayInt != null)
			yearInt = getYearIncludePrev(year);
		else 
			yearInt = getYear(year);
			
		
		if (yearInt != null && monthObj != null && dayInt != null) {
			return new FreeFormDate(yearInt, monthObj, null, dayInt, null, null);
		} 
		if (baseDate != null && monthObj != null && dayInt != null)
			return new FreeFormDate(baseDate.getYear(), monthObj, null, dayInt, null, null);
		return null;
	}

	protected void convertToBC (FreeFormDate date)
	{
		Integer year = date.getYear();
		if (year != null)
			date.setYear(-year + 1);
		else
			System.out.println(date);
	}
	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		isPrevYearSet = false;
		Integer day = getDayFromMatch(match);
		if (day == null)
			return null;
		FreeFormDate date2 = getDate(match,baseDate);
		if (date2 == null)
			return null;
		FreeFormDate date1 = new FreeFormDate(date2.getYear(), date2.getMonth(), null, day, null, null);
		IFreeFormDate result = null;
		result = new FreeFormDateRange(date1, date2);
		return result;
	}

}
