package com.onpositive.parsers.dates.templates;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.parts.Month;
import com.onpositive.parsers.dates.parts.MonthPart;
import com.onpositive.parsers.dates.parts.YearPart;
import com.onpositive.parsers.dates.FreeFormDateRange;
import com.onpositive.parsers.dates.IFreeFormDate;

/** Даты с днем, месяцем и годом
 * в начале января 2011
 * 15 августа 1756 года
 * с 21 июня 1941 по 9 мая 1945г.
 */
public class DTDayMonthYear extends DateTemplate {

	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
					
			List<String> dayPatterns = new ArrayList<String>();
			dayPatterns.add("[1-2]?\\d");
			dayPatterns.add("30");
			dayPatterns.add("31");
			String day = oneOf(dayPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> partOfMonthPatterns = new ArrayList<String>();
			partOfMonthPatterns.addAll(MonthPart.getAllForms());
			String partOfMonth = oneOf(partOfMonthPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> monthPatterns = new ArrayList<String>();
			monthPatterns.addAll(Month.getMonthNamesForms());
			String month = oneOf(monthPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> partOfYearPatterns = new ArrayList<String>();
			partOfYearPatterns.addAll(YearPart.getAllForms());
			String partOfYear = oneOf(partOfYearPatterns, true) + some(SPACE_DELIMITTER);

			String year = oneOf(
					Lists.newArrayList("(\\d{4})" + optional(some(SPACE_DELIMITTER) + YEAR_SIGN),
					"(\\d{1,3})" + some(SPACE_DELIMITTER) + YEAR_SIGN), false
					);
			String epochSign = optional(some(SPACE_DELIMITTER) + simpleBrackets(oneOf("до н.э.","до н. э.","до нашей эры")));		
			String date = oneOf(
					day + month + optional(year),
					optional(partOfMonth) + oneOf(month + optional(year),partOfYear + year),
					year
					);
			pattern = Pattern.compile(VALID_START+rangeTemplate.getRangePattern(date)+ optional(epochSign)+VALID_END);
			//System.out.println(YEAR_SIGN);
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
	private String yearSignStr = "";
	protected FreeFormDate getDateByShift(Matcher match,int shift)
	{
		yearSign = false;
		String day = match.group(0+shift);
		
		String partOfMonth = match.group(6+shift);

		String month = match.group(1+shift);
		if (month == null)
			month = match.group(7+shift);
		
		String partOfYear = match.group(12+shift);

		String year = match.group(2+shift);
		yearSign = (match.group(3+shift) != null);
		yearSignStr = match.group(3+shift);
		if (year == null)
		{
			year = match.group(4+shift);
			yearSign = (match.group(5+shift) != null);
			yearSignStr = match.group(5+shift);
		}
		if (year == null)
		{
			year = match.group(8+shift);
			yearSign = (match.group(9+shift) != null);
			yearSignStr = match.group(9+shift);
		}
		if (year == null)
		{
			year = match.group(10+shift);
			yearSign = (match.group(11+shift) != null);
			yearSignStr = match.group(11+shift);
		}
		if (year == null)
		{
			year = match.group(13+shift);
			yearSign = (match.group(14+shift) != null);
			yearSignStr = match.group(14+shift);
		}
		if (year == null)
		{
			year = match.group(15+shift);
			yearSign = (match.group(16+shift) != null);
			yearSignStr = match.group(16+shift);
		}
		if (year == null)
		{
			year = match.group(17+shift);
			yearSign = (match.group(18+shift) != null);
			yearSignStr = match.group(18+shift);
		}
		if (year == null)
		{
			year = match.group(19+shift);
			yearSign = (match.group(20+shift) != null);
			yearSignStr = match.group(20+shift);
		}
		
		Integer dayInt = getDay(day);

		MonthPart monthPart = null;
		if (dayInt == null) {
			monthPart = MonthPart.fromNameForm(partOfMonth);
		}
		
		Month monthObj = null;
		try {
			monthObj = Month.fromNameForm(month);
		} catch (Throwable th){}
		
		YearPart yearPart = null;
		if (dayInt == null) {
			yearPart = YearPart.fromNameForm(partOfYear);
		}
		Integer yearInt;
		if ((year != null&&yearSign) || monthObj != null || dayInt != null)
			yearInt = getYearIncludePrev(year);
		else 
			yearInt = getYear(year);
			
		
		if (yearInt != null || monthObj != null || dayInt != null) {
			return new FreeFormDate(yearInt, monthObj, yearPart, dayInt, monthPart, null);
		} 
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
		FreeFormDate date1 = getDateByShift(match, 1);
		boolean date1YearSighed = yearSign;
		String date1YearSign = yearSignStr;
		boolean isPrevYearSetDate1 = isPrevYearSet;
		boolean isPrevYearSetDate2 = false;
		FreeFormDate date2 = null;
		IFreeFormDate result = null;
		boolean date2YearSighed=false;
		boolean onlyBefore = false;
		boolean onlyAfter = false;
		if (date1 != null) {
			date2 = getDateByShift(match, 22);				
			date2YearSighed = yearSign;
			isPrevYearSetDate2 = isPrevYearSet;
		}
		else {
			date1 = getDateByShift(match, 43);
			date1YearSighed = yearSign;
			date1YearSign = yearSignStr;
			isPrevYearSetDate1 = isPrevYearSet;
			if (date1 != null) {
				date2 = getDateByShift(match, 64);
				date2YearSighed = yearSign;
				isPrevYearSetDate2 = isPrevYearSet;
				if (date2 == null)
					onlyAfter = true;
			}
			else {
				date1 = getDateByShift(match, 85);
				if (date1 != null) {
					onlyBefore = true;
				}
				date1YearSighed = yearSign;
				date1YearSign = yearSignStr;
				isPrevYearSetDate1 = isPrevYearSet;
			}
		}
		if (date1 != null && date2 != null) {
			
			if (match.group(106)!=null)
			{
				convertToBC(date1);
				convertToBC(date2);
				if (prevYear != null && prevYear>0) prevYear = -prevYear;
			}
			if (date2.getMonth() != null || date2YearSighed) {
				if (date1.getYear() == null && date2.getYear() == null)
				{
					if (baseDate == null)
						return null;
					else 
					{
						date1.setYear(baseDate.getYear());
						if (baseDate.getDayOfYear() < date1.getStartDate().getDayOfYear())
							date1.setYear(baseDate.getYear()-1);
						date2.setYear(baseDate.getYear());
					}
				}
				if (date1.getYear() == null || isPrevYearSetDate1)
					date1.setYear(date2.getYear());
	
				try {
					date1.getStartDate();
					date2.getEndDate();
				}
				catch (DateTimeException e) {
					return null;
				}
				if (date1.getYear() != null && date2.getYear() != null && date1.getYear() > date2.getYear())
				{
					if (date2.getYear()/100 == 0)
						date2.setYear((date1.getYear()/100)*100+date2.getYear());
				}
				if (isPrevYearSetDate1 && isPrevYearSetDate2 && 
					date1.getStartDate() != null && date2.getEndDate() != null &&	
					date1.getStartDate().isAfter(date2.getEndDate()) )
					date2.setYear(date2.getYear()+1);
				result = new FreeFormDateRange(date1, date2);
			}
		}
		else if (date1 != null)
		{
			if ("году".equals(date1YearSign) && 
				 text.substring(0, match.start()).endsWith("на "))
				return null;
			if (match.group(106)!=null)
			{
				convertToBC(date1);
				if (prevYear != null && prevYear>0) 
					prevYear = -prevYear;

			}
			if (date1.getYear() == null) 
			{
				if (baseDate == null)
					return null;
				else 
				{
					date1.setYear(baseDate.getYear());
					try {
						date1.getStartDate();
					}
					catch (DateTimeException e) {
						return null;
					}
					/*if (baseDate.getDayOfYear() < date1.getStartDate().getDayOfYear())
						date1.setYear(baseDate.getYear()-1);*/

				}
			}
			try {
				date1.getStartDate();
			}
			catch (DateTimeException e) {
				return null;
			}
			if (onlyBefore) {
				if (date1.getYearPart() != null || date1.getMonth() != null || date1YearSighed)
					result = new FreeFormDateRange(null, date1);
			}
			else if (onlyAfter) {
				if (date1.getYearPart() != null || date1.getMonth() != null || date1YearSighed)
					result = new FreeFormDateRange(date1, null);
			}
			else
			{
				if (date1.getMonth() != null || date1YearSighed)
					result = date1;
			}
		}

		return result;
	}

}
