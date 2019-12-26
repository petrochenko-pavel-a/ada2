package com.onpositive.parsers.dates.templates;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.FreeFormDate.YearsCount;
import com.onpositive.parsers.dates.IFreeFormDate;

public abstract class DateTemplate extends TemplateConstructor {
	public static final String YEAR_SIGN = "(г(?:г|од(?:а|ы|у|ах|ов)?)?)";

	public static final class DatesMap {
		protected ArrayList <IFreeFormDate> dates = new ArrayList<>();
		/**
		 * keys - char ids, values - dates ids
		 */
		protected LinkedHashMap <Integer, Integer> datesByStart = new LinkedHashMap<>();
		protected HashMap <Integer, Integer> datesByEnd = new LinkedHashMap<>();
		protected ArrayList <Integer> startsByDateId = new ArrayList<>();
		protected ArrayList <Integer> endsByDateId = new ArrayList<>();
		/**
		 * keys - dates ids, values - length
		 */
		protected ArrayList <Integer> datesLength = new ArrayList<>();
		public boolean addDate(IFreeFormDate date, int start, int end)
		{
			try {
				date.getEndDate();
				date.getStartDate();
			}
			catch (DateTimeException e) {
				return false;
			}
			if (date.getEndDate()!=null && date.getStartDate()!= null && date.getStartDate().isAfter(date.getEndDate()))
				return false;
			if (datesByStart.containsKey(start))
			{
				Integer concurrentDateId = datesByStart.get(start);
				Integer concurrentLength = datesLength.get(concurrentDateId);
				if (concurrentLength < end-start)
				{
					dates.set(concurrentDateId, date);
					Integer concurrentEnd = endsByDateId.get(concurrentDateId);
					datesByEnd.remove(concurrentEnd);
					datesByEnd.put(end, concurrentDateId);
					endsByDateId.set(concurrentDateId, end);
					datesLength.set(concurrentDateId,end-start);
					return true;
				}
				return false;
			}
			if (datesByEnd.containsKey(end))
			{
				Integer concurrentDateId = datesByEnd.get(end);
				Integer concurrentLength = datesLength.get(concurrentDateId);
				if (concurrentLength < end-start)
				{
					dates.set(concurrentDateId, date);
					Integer concurrentStart = startsByDateId.get(concurrentDateId);
					datesByStart.remove(concurrentStart);
					datesByStart.put(start, concurrentDateId);
					startsByDateId.set(concurrentDateId, start);
					datesLength.set(concurrentDateId,end-start);
					return true;
				}
				return false;
			}
			int currDateId = dates.size();
			dates.add(date);
			startsByDateId.add(start);
			datesByStart.put(start, currDateId);
			datesByEnd.put(end, currDateId);
			datesLength.add(end-start);
			//startsByDateId.add(start);
			endsByDateId.add(end);
			return true;
		}
		public void clearUnconsistentDates() {
			for (int i=0;i<dates.size();i++)
			{
				if (dates.get(i).getStartDate().isAfter(dates.get(i).getEndDate()))
				{
					dates.remove(i);
					i--;
				}
			}
		}
		
		public static class DateAndPosition implements Comparable<DateAndPosition>{
			
			public final  int start;
			public final  int length;
			public final IFreeFormDate date;
			public DateAndPosition(int start, int length, IFreeFormDate date) {
				super();
				this.start = start;
				this.length = length;
				this.date = date;
			}
			@Override
			public int compareTo(DateAndPosition o) {
				return this.start-o.start;
			}
		}
		
		public ArrayList <IFreeFormDate> getDatesArray() {
			
			Integer[] starts = datesByStart.keySet().toArray(new Integer[0]);
			Arrays.sort(starts);
			ArrayList <IFreeFormDate> newDates = new ArrayList<>(); 
			for (int s:starts)
			{
				newDates.add(dates.get(datesByStart.get(s)));
			}
			return newDates;
		}
		
		public ArrayList <DateAndPosition> getDatesWithPositions() {
			ArrayList<DateAndPosition>positions=new ArrayList<>();
			for (int i=0;i<this.dates.size();i++) {
				int start=startsByDateId.get(i);
				int end=endsByDateId.get(i);
				IFreeFormDate d=this.dates.get(i);	
				positions.add(new DateAndPosition(start, end-start, d));
			}
			Collections.sort(positions);
			return positions;
		}
		public ArrayList <Integer> getPositions() {
			return startsByDateId;
		}
		public ArrayList <Integer> getDatesLength() {
			return startsByDateId;
		}
	}

	int MAX_YEAR_ACCEPT = 5000;
	public DateTemplate(){}
	String SPACE_DELIMITTER = oneOf("\\s","_"," ");
	protected DTRangeTemplate rangeTemplate = new DTRangeTemplate();
	protected static final String WHITESPACE_CHARS =  "["
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL) 
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD 
            + "\\u2001" // EM QUAD 
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            + "]";
	protected static final String VALID_START = "(?<=" + WHITESPACE_CHARS +"|\\p{Punct})";
	protected static final String VALID_END = "(?=" + WHITESPACE_CHARS +"|\\p{Punct})";
	protected abstract Pattern getRegex();
	protected abstract IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate);
	
	protected void printMatch(Matcher match)
	{
//		for (int i = 0; i<=match.groupCount(); i++)
//		if (match.group(i) != null)
//			LoggerFactory.getLogger(getClass()).info(i+" "+match.group(i));
	}
	protected Integer getYear(String year) {
		if (year == null) {
			return null;
		}
		
		try {
			Integer result = Integer.parseInt(year);
			if (result <= 0 || result > MAX_YEAR_ACCEPT) {
				return null;
			}
			return result;
		} catch (Throwable th) {
		}
		return null;
	}
	
	protected Integer getDay(String day) {
		if (day == null) {
			return null;
		}
		
		try {
			Integer dayInt = Integer.parseInt(day);
			if (dayInt>31)
				return null;
			return dayInt;
		} catch (Throwable th) {
		}
		
		return null;
	}
	
	public void clear() {}
	
	public void parse(String text, DatesMap datesMap, LocalDate baseDate){
		Matcher matcher = getRegex().matcher(text);
		while (matcher.find()) {
			processMatch(text,matcher, datesMap,baseDate);
		}
	}
	
	protected void processMatch(String text,Matcher matcher,DatesMap datesMap, LocalDate baseDate) {
		int start = matcher.start();
		int end = matcher.end();
		try {
			IFreeFormDate date = getDateFromMatch(matcher, text, baseDate);
			if (date != null)
				datesMap.addDate(date, start, end);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	protected void convertToBC (FreeFormDate date)
	{
		Integer year = date.getYear();
		if (date.getYearCount() == YearsCount.ONE)
			date.setYear(-year+1);
		if (date.getYearCount() == YearsCount.TEN)
			date.setYear(-year-9);
		if (date.getYearCount() == YearsCount.HUNDRED)
			date.setYear(-year-98);
	}
	
}
