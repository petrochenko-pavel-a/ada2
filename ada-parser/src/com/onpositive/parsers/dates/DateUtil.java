package com.onpositive.parsers.dates;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.onpositive.parsers.dates.parts.Month;

public class DateUtil {
	
	private static final Pattern ISO_PATTERN = Pattern.compile("(-?)(\\d{4})(-(\\d{2}))?(-(\\d{2}))?/P(\\d+)([DMY])");
	
	public static IFreeFormDate parseDate(String isoDate) {
		java.util.regex.Matcher matcher = ISO_PATTERN.matcher(isoDate);
        if (matcher.matches())
        {
        	
        	FreeFormDate start = null;
        	FreeFormDate finish = null;
    		int year = Integer.parseInt(matcher.group(2));
    		if (!matcher.group(1).isEmpty())
    			year = -year;
        	if(matcher.group(3) == null) // Only year
        	{
        		start = new FreeFormDate(year, null, null, null, null, null);
        	}
        	else // With month
        	{
        		int month = Integer.parseInt(matcher.group(4));
        		if (matcher.group(5) == null)	// Only with month
        			start = new FreeFormDate(year, Month.fromMonthNumber(month-1), null, null, null, null);
        		else	// With day
        		{
        			int day = Integer.parseInt(matcher.group(6));
        			start = new FreeFormDate(year, Month.fromMonthNumber(month-1), null, day, null, null);
        		}
        	}
    		int length = Integer.parseInt(matcher.group(7));
    		//Calendar c = Calendar.getInstance(); 
    		//c.setTime(start.getStartDate()); 
    		LocalDate finishDate = start.getStartDate();
    		switch (matcher.group(8))
    		{
    			case "D": finishDate = start.getStartDate().plusDays(length-1);break;//c.add(Calendar.DATE, length); break;
    			case "M": finishDate = start.getStartDate().plusMonths(length).minusDays(1);break;//c.add(Calendar.MONTH, length); break;
    			case "Y": finishDate = start.getStartDate().plusYears(length).minusDays(1);break;//c.add(Calendar.YEAR, length); break;
    		}
    		 
    		/*int finishYear = c.get(Calendar.YEAR);
    		if (c.get(Calendar.ERA) ==  GregorianCalendar.BC)
    			finishYear = -finishYear;*/
    		finish = new FreeFormDate(
    				finishDate.getYear(), 
    				Month.fromMonthNumber(finishDate.getMonthValue()-1), 
    				null, 
    				finishDate.getDayOfMonth(), null, null);
        	return new FreeFormDateRange(start, finish);
        }
        try {
        	LocalDate parsedDate = LocalDate.parse(isoDate, DateTimeFormatter.ISO_DATE);
        	return new FreeFormDate(parsedDate);
        } catch (DateTimeParseException e) {
        	// Ignore
        }
		return null;
	}
	
	/*public static IFreeFormDate getFreeFormDate(Period period) {
		return getFreeFormDate();
	}
	
	public static IFreeFormDate getFreeFormDate(String startDate, String endDate) {
		startDate = StringUtils.stripToEmpty(startDate);
		endDate = StringUtils.stripToEmpty(endDate);
		if (!startDate.isEmpty() && !endDate.isEmpty()) {
			if (startDate.equals(endDate)) {
				return new FreeFormDate(startDate);
			}
			return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
		} else if (!startDate.isEmpty()) {
			return new FreeFormDate(startDate);
		} else if (!endDate.isEmpty()) {
			return new FreeFormDate(endDate);
		}
		return null;
	}*/
}
