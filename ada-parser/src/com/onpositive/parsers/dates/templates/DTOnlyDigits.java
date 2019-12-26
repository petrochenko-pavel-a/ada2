package com.onpositive.parsers.dates.templates;

/**
 * 13.12.1985
 * 13/12/1985
 * 01/09/2002-01/08/2007
 */
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.FreeFormDateRange;
import com.onpositive.parsers.dates.IFreeFormDate;
import com.onpositive.parsers.dates.parts.Month;

public class DTOnlyDigits extends DateTemplate {

	private Pattern pattern = null;
	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
			String delimiter = oneOf("\\.","/","\\-");
			String date = "(\\d{1,2})" + delimiter + "(\\d{1,2})" + delimiter + "(\\d{4})";
			pattern = Pattern.compile(VALID_START+rangeTemplate.getRangePattern(date)+VALID_END);
		}
		return pattern;
	}
	
	/*protected int calcNextPosition (String text, int prevPosition) {
		int pos1 = text.indexOf('1',prevPosition);
		int pos2 = text.indexOf('2',prevPosition);
		int pos3 = text.indexOf('3',prevPosition);
		int pos = -1;
		if (pos1 != -1)
			pos = pos1;
		if (pos2 != -1 && pos2 < pos)
			pos = pos2;
		if (pos3 != -1 && pos3 < pos)
			pos = pos3;
		if (pos == -1)
			return pos;
		if (pos > prevPosition + DTRangeTemplate.PREFIX_SIZE)
			pos -= DTRangeTemplate.PREFIX_SIZE;
		else
			pos = prevPosition;
		return pos; 
	}
	@Override
	public void parse(String text, DatesMap datesMap, LocalDate baseDate){
		int prevPos = 0;
		int pos = 0;
		Matcher matcher = getRegex().matcher(text);
		do {
			pos = calcNextPosition(text,prevPos);
			if (pos == -1)
				return;
			matcher.reset(text.substring(pos));
			if (!matcher.find())
				return;
			processMatch(text,matcher, datesMap,baseDate);
			prevPos = matcher.end();
		} while (pos != -1);
	}
	*/
	protected FreeFormDate getDateByShift(Matcher match,int shift)
	{
		String day = match.group(0+shift);  
		String month = match.group(1+shift);  
		String year = match.group(2+shift);

		Integer dayInt = getDay(day);
		Month monthObj = null;
		try {
			int monthNumber = Integer.parseInt(month);
			monthObj = Month.fromMonthNumber(monthNumber - 1);
		} catch (Throwable th){}
		Integer yearInt = getYear(year);
		if (yearInt != null && monthObj != null && dayInt != null) {
			return new FreeFormDate(yearInt, monthObj, null, dayInt, null, null);
		} 
		return null;
	}
	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		FreeFormDate date1 = getDateByShift(match, 1);
		FreeFormDate date2 = null;
		IFreeFormDate result = null;
		boolean onlyBefore = false;
		boolean onlyAfter = false;
		if (date1 != null) {
			date2 = getDateByShift(match, 4);
		}
		else {
			date1 = getDateByShift(match, 7);
			if (date1 != null) {
				date2 = getDateByShift(match, 10);
			}
			else {
				date1 = getDateByShift(match, 13);
				if (date1 != null) {
					onlyBefore = true;
				}
				
			}
		}
		if (date1 != null && date2 != null) {
			result = new FreeFormDateRange(date1, date2);
		}
		else if (date1 != null) {
			if (onlyBefore) {
				result = new FreeFormDateRange(null, date1);
			}
			else if (onlyAfter) {
				result = new FreeFormDateRange(date1, null);
			}
			else
				result = date1;
		}
			
		return result;
		/*for (int i = 0; i<=match.groupCount(); i++)
			LoggerFactory.getLogger(getClass()).info(i+" "+match.group(i));
		return null;*/
	}
}
