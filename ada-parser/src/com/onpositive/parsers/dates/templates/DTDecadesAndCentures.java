package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.FreeFormDateRange;
import com.onpositive.parsers.dates.IFreeFormDate;
import com.onpositive.parsers.dates.RomanToDecimal;
import com.onpositive.parsers.dates.parts.MonthPart;
import com.onpositive.parsers.dates.FreeFormDate.YearsCount;
/**
 * Декады и века 
 * в 80-х
 * в XII веке
 * девяностые годы XX века
 */
public class DTDecadesAndCentures  extends DateTemplate {

	public static enum DecadeName {
		FIRST("нулевые", "нулевых"),
		SECOND("десятые", "десятых"),
		THIRD("двадцатые","двадцатых"),
		FOURTH("тридцатые","тридцатых"),
		FIFTH("сороковые","сороковых"),
		SIXTH("пятидесятые","пятидесятых"),
		SEVENTH("шестидесятые","шестидесятых"),
		EIGHTH("семидесятые","семидесятых"),
		NINTH("восьмидесятые","восьмидесятых"),
		LAST("девяностые","девяностых");

		private List<String> forms;
		
		private DecadeName(String... forms) {
			this.forms = Lists.newArrayList(forms);
		}
		
		public List<String> getForms() {
			return Collections.unmodifiableList(forms);
		}
		
		public static Integer getByForm(String form) {
			for (DecadeName currentValue : values()) {
				if (currentValue.forms.contains(form))
					return currentValue.ordinal()*10;
			}
			return null;
		}
		
		public static List<String> getAllForms() {
			List<String> result = new ArrayList<String>();
			
			for (DecadeName currentValue : values()) {
				result.addAll(currentValue.getForms());
			}
			
			return result;
		}
	}
	private Pattern pattern = null;
	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
			
			String decageSign = "-?"+oneOf(
				"е"+optional(some(SPACE_DELIMITTER)+"годы"),
				"[ы]?х"+optional(some(SPACE_DELIMITTER)+"год(?:ах|ов)"),
				"ые"+optional(some(SPACE_DELIMITTER)+"годы"));
				
			List<String> decade = new ArrayList<String>();
			decade.add( brackets("(\\d{2,4})"+simpleBrackets(decageSign)));
			String forms = oneOf(DecadeName.getAllForms(),true)+optional(some(SPACE_DELIMITTER)+"год(?:ы|ах|ов)");
			decade.add(forms);
			
			String centuryPart = oneOf(MonthPart.getAllForms(),true); 
			
			String centurySign = simpleBrackets(oneOf("веке","век","вв","в\\.","века","веков"));
			String epochSign = optional(some(SPACE_DELIMITTER) + simpleBrackets(oneOf("до н.э.","до н. э.","до н. э.","до нашей эры")));
			String cetnury = "([IXV]+)" + optional(optional(some(SPACE_DELIMITTER)) + centurySign);
			
			List<String> variants = new ArrayList<String>();
			variants.add(oneOf(decade) + optional(some(SPACE_DELIMITTER) + cetnury));
			variants.add(some(SPACE_DELIMITTER) + cetnury);
			String date = optional(centuryPart + some(SPACE_DELIMITTER)) + oneOf(variants) + epochSign;
			pattern = Pattern.compile(VALID_START+rangeTemplate.getRangePattern(date)+VALID_END);
		}
		return pattern;
	}

	protected Integer getTenYears(String years) {
		if (years == null) {
			return null;
		}
		Integer result = DecadeName.getByForm(years);
		if	(result != null)
			return result;
		try {
			result = Integer.parseInt(years);
			return result;
		} catch (Throwable th) {
		}
		
		return null;
	}
	
	boolean isCenturySigned = false;
	boolean isBC = false;
	boolean isYearsSigned = true;
	protected FreeFormDate getDateByShift(Matcher match,int shift, String text)
	{
		String decade = match.group(1+shift);
		Integer tenYears = getTenYears(decade);
		if (tenYears == null) {
			decade = match.group(3+shift);
			tenYears = getTenYears(decade);
			isYearsSigned = true;
		}
		isYearsSigned = match.group(2+shift) != null;
		
		String partStr = match.group(0+shift);
		MonthPart part = MonthPart.fromNameForm(partStr);
		
		String century = match.group(4+shift);
		isCenturySigned = match.group(5+shift) != null;
		if (century == null) {
			century = match.group(6+shift);
			isCenturySigned = match.group(7+shift) != null;
		}
		isBC = match.group(8+shift) != null;
		Integer centuryInt = null;
		if (century != null)
		{
			centuryInt = RomanToDecimal.convert(century)-1;
		}
		int resultYears = 0;
		YearsCount yearsCount = YearsCount.HUNDRED;
		if (centuryInt != null)
			resultYears += centuryInt * 100 + 1;
			//if (isBC && tenYears == null)
			//	resultYears += 99;
		if (tenYears != null)
		{
			resultYears += tenYears;
			if (centuryInt != null)
				resultYears -=1;
			else
				if (tenYears < 30)
					resultYears += 2000;
				else if (tenYears < 100)
					resultYears += 1900;
			yearsCount = YearsCount.TEN;
		}			
		if (centuryInt == null && tenYears != null)
		{
			/*if (match.group(0).endsWith("-е"))
			{
				if (text.substring(match.end()).startsWith(" место"))
					return null;
				
			}*/
		}
		if (centuryInt != null || tenYears != null)
			if (tenYears == null || tenYears%10 == 0)
				return new FreeFormDate(resultYears, null, null, null, part, yearsCount);
		return null;
	}
	

	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		isCenturySigned = false;
		isBC = false;
		FreeFormDate date1 = getDateByShift(match, 1,text);
		boolean isCenturySignedDate1 = isCenturySigned;
		boolean isCenturySignedDate2 = false;
		boolean isDADate1 = isBC;
		//boolean isYearsSigned1 = isYearsSigned;
		FreeFormDate date2 = null;
		boolean isDADate2 = false;
		boolean isYearsSigned2 = false;
		IFreeFormDate result = null;
		boolean onlyBefore = false;
		boolean onlyAfter = false;
		//return date1;
		if (date1 != null) {
			date2 = getDateByShift(match, 10,text);
			isDADate2 = isBC;
			isCenturySignedDate2 = isCenturySigned;
			isYearsSigned2 = isYearsSigned;
		}
		else {
			date1 = getDateByShift(match, 19,text);
			isCenturySignedDate1 = isCenturySigned;
			isDADate1 = isBC;
			//isYearsSigned1 = isYearsSigned;
			if (date1 != null) {
				date2 = getDateByShift(match, 28,text);
				isDADate2 = isBC;
				isCenturySignedDate2 = isCenturySigned;
				isYearsSigned2 = isYearsSigned;
				if (date2 == null)
					onlyAfter = true;
			}
			else {
				date1 = getDateByShift(match, 37,text);
				isCenturySignedDate1 = isCenturySigned;
				isDADate1 = isBC;
				//isYearsSigned1 = isYearsSigned;
				if (date1 != null) {
					onlyBefore = true;
				}
				
			}
		}
		if (date1 != null && date2 != null) {
			if (isDADate1)
			{
				convertToBC(date1);
			}
			if (isDADate2)
			{
				convertToBC(date1);
				convertToBC(date2);
			}
			if (isCenturySignedDate2 || isDADate2 || isYearsSigned2)
			result = new FreeFormDateRange(date1, date2);
		}
		else if (date1 != null) {
			if (date1.getYearCount() == YearsCount.HUNDRED && !isCenturySignedDate1)
				return null;
			if (isDADate1)
			{
				convertToBC(date1);
			}
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
	}

}
