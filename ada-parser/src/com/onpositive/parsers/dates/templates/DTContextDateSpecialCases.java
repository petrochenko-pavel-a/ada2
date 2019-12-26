package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.onpositive.parsers.dates.parts.Day;
import com.onpositive.parsers.dates.parts.TimePeriod;
import com.onpositive.parsers.dates.ContextDateSpecialCase;
import com.onpositive.parsers.dates.ContextDateSpecialCase.Byttp;
import com.onpositive.parsers.dates.ContextDateSpecialCase.SmthCounting;
import com.onpositive.parsers.dates.parts.Month;
import com.onpositive.parsers.dates.IFreeFormDate;

/**
 * Даты со специальными названиями
 * завтра
 * неделю спустя
 * полгода назад
 */
public class DTContextDateSpecialCases extends DateTemplate{
	private Pattern pattern = null;
	
	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
						
			List<String> dayPatterns = new ArrayList<String>();
			dayPatterns.addAll(Day.getRegexps());
			String day = oneOf(dayPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> timePeriodPatterns = new ArrayList<String>();
			timePeriodPatterns.addAll(TimePeriod.WEEK.getForms());
			timePeriodPatterns.addAll(TimePeriod.HALF_YEAR.getForms());
			timePeriodPatterns.addAll(TimePeriod.YEAR.getForms());
			String timePeriod = oneOf(timePeriodPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> monthPatterns = new ArrayList<String>();
			monthPatterns.addAll(Month.getAllForms());
			String month = oneOf(monthPatterns, true) + some(SPACE_DELIMITTER);
						
			List<String> byttpPatterns = new ArrayList<String>();
			byttpPatterns.addAll(Byttp.getAllForms());
			String byttp = oneOf(byttpPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> smthCountPatterns = new ArrayList<String>();
			smthCountPatterns.addAll(SmthCounting.getAllForms());
			String smthCount = oneOf(smthCountPatterns, true) + some(SPACE_DELIMITTER);
			
			String backward = "назад" + some(SPACE_DELIMITTER);
			String after = "спустя" + some(SPACE_DELIMITTER);
			
			
			String date = oneOf(byttp,
					simpleBrackets(after) + optional(smthCount) + oneOf(Lists.newArrayList(day, month, timePeriod)),
					optional(smthCount) + oneOf(Lists.newArrayList(day, month, timePeriod)) + simpleBrackets(oneOf(Lists.newArrayList(after,backward)))		
					);
			pattern = Pattern.compile(VALID_START+date+VALID_END);
		}
		return pattern;
	}
	
	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		ContextDateSpecialCase date = new ContextDateSpecialCase(baseDate);
		// Обработка сегодня, завтра и пр.
		if (match.group(1) != null) {
			date.setByttp(Byttp.fromNameForm(match.group(1)));
		}
		// Обработка "спустя ..."
		else if (match.group(2) != null) {
			date.setBack(true);
			if (match.group(3) != null)
				date.setCount(SmthCounting.fromNameForm(match.group(3)));
			if (match.group(4) != null)
				date.setDay(Day.fromNameForm(match.group(4)));
			else if (match.group(5) != null)
				date.setMonth(Month.fromNameForm(match.group(5)));
			else if (match.group(6) != null)
				date.setTimePeriod(TimePeriod.fromNameForm(match.group(6)));
		}
		// Обработка "... спустя"
		else if (match.group(11) != null) {
			if (match.group(11).equals("спустя")) 
				date.setBack(true);
			if (match.group(11).equals("назад"))
				date.setBack(true);
			if (match.group(7) != null)
				date.setCount(SmthCounting.fromNameForm(match.group(7)));
			else if (match.group(11).equals("спустя"))
				date.setCount(SmthCounting.ONE);
			if (match.group(10) != null)
				date.setTimePeriod(TimePeriod.fromNameForm(match.group(10)));
			if (match.group(9) != null)
				date.setMonth(Month.fromNameForm(match.group(9)));
			if (match.group(8) != null)
				date.setDay(Day.fromNameForm(match.group(8)));
		}
		// Обработка "... назад"
		/*else if (match.group(22) != null) {
			date.setBack(true);
			if (match.group(16) != null)
				date.setCount(SmthCounting.fromNameForm(match.group(16)));
			if (match.group(17) != null)
				date.setDay(Day.fromNameForm(match.group(17)));
			else if (match.group(18) != null)
				date.setWeek(Week.WEEK);
			else if (match.group(19) != null) {
				date.setMonth(Month.fromNameForm(match.group(19)));
			}
			else if (match.group(20) != null)
				date.setHalfYear(HalfYear.HALF_YEAR);
			else if (match.group(21) != null)
				date.setYear(Year.YEAR);
		}*/
		
		
		//date.getIFreeFormDate();
		return date.getIFreeFormDate();
		//return null;
	}

}
