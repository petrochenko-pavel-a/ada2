package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.onpositive.parsers.dates.ContextDate;
import com.onpositive.parsers.dates.IFreeFormDate;
import com.onpositive.parsers.dates.parts.Counting;
import com.onpositive.parsers.dates.parts.Day;
import com.onpositive.parsers.dates.parts.PartOfSmth;
import com.onpositive.parsers.dates.parts.Season;
import com.onpositive.parsers.dates.parts.TimeAdj;
import com.onpositive.parsers.dates.parts.TimePeriod;
import com.onpositive.parsers.dates.parts.Month;
import com.onpositive.parsers.dates.templates.DTDecadesAndCentures.DecadeName;

/**
 * за прошедшую неделю
 * в следующем месяце
 * во второй декаде января
 * в первом квартале 1989 года
 *  
 */
public class DTContextDates extends DateTemplate{
	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
		if (pattern == null) synchronized (this) {
			
			List<String> dayPatterns = new ArrayList<String>();
			dayPatterns.addAll(Day.getAllForms());
			String day = oneOf(dayPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> timePeriodPatterns = new ArrayList<String>();
			timePeriodPatterns.addAll(TimePeriod.getAllForms());
			String timePeriod = oneOf(timePeriodPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> monthPatterns = new ArrayList<String>();
			monthPatterns.addAll(Month.getAllForms());
			String month = oneOf(monthPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> seasonPatterns = new ArrayList<String>();
			seasonPatterns.addAll(Season.getAllForms());
			String season = oneOf(seasonPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> YearPatterns = new ArrayList<String>();
			YearPatterns.addAll(TimePeriod.YEAR.getForms());
			String year = oneOf(YearPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> CenturyPatterns = new ArrayList<String>();
			CenturyPatterns.addAll(TimePeriod.CENTURY.getForms());
			String century = oneOf(CenturyPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> DecadePatterns = new ArrayList<String>();
			DecadePatterns.addAll(TimePeriod.DECADE.getForms());
			String decade = oneOf(DecadePatterns, true) + some(SPACE_DELIMITTER);

			List<String> QuarterAndHaldYearPatterns = new ArrayList<String>();
			QuarterAndHaldYearPatterns.addAll(TimePeriod.QUARTER.getForms());
			QuarterAndHaldYearPatterns.addAll(TimePeriod.HALF_YEAR.getForms());
			String quarterAndHalfYear = oneOf(QuarterAndHaldYearPatterns, true) + some(SPACE_DELIMITTER);

			List<String> refinementPatterns = new ArrayList<String>();
			refinementPatterns.addAll(TimeAdj.getAllForms());
			String refinement = oneOf(refinementPatterns, true) + some(SPACE_DELIMITTER);
			
			List<String> partPatterns = new ArrayList<String>();
			partPatterns.addAll(PartOfSmth.getAllForms());
			String partOfSmth = oneOf(partPatterns, true) + some(SPACE_DELIMITTER);

			List<String> countingPatterns = new ArrayList<String>();
			countingPatterns.addAll(Counting.getAllForms());
			String counting = oneOf(countingPatterns, true) + some(SPACE_DELIMITTER);
			

			String decageSign = "-?"+oneOf(
				"е"+optional(some(SPACE_DELIMITTER)+"годы"),
				"х"+optional(some(SPACE_DELIMITTER)+"годах"),
				"х"+optional(some(SPACE_DELIMITTER)+"годов"),
				"ых"+optional(some(SPACE_DELIMITTER)+"годах"),
				"ые"+optional(some(SPACE_DELIMITTER)+"годы"));
			
			List<String> decadeOfYears = new ArrayList<String>();
			decadeOfYears.add( brackets("(\\d{2,4})"+decageSign));
			String forms = oneOf(DecadeName.getAllForms(),true)+optional(some(SPACE_DELIMITTER)+oneOf("годы","годах","годов"));
			decadeOfYears.add(forms);
			
			String date = 
					optional(partOfSmth) + 
					oneOf(
							oneOf(counting, refinement) + decade + optional(optional(refinement) + month),
							oneOf(month, 
								season, 
								counting + quarterAndHalfYear) + refinement + year,
							oneOf(decadeOfYears) + some(SPACE_DELIMITTER) + refinement + century,
							refinement + oneOf(day, month, season, timePeriod)
					);
			pattern = Pattern.compile(VALID_START+date+VALID_END);
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
		
	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		ContextDate date = new ContextDate(baseDate);
		String partOfSmth = null, refinement = null, counting = null;
		partOfSmth = match.group(1);
		refinement = match.group(17);
		Integer dec = null;
		//Обработка декады
		if (match.group(4) != null) {
			counting = match.group(2);
			refinement = match.group(3);
			date.setDecade();
			if (counting != null)
				date.setCounting(Counting.fromNameForm(counting));
			if (match.group(6) != null){
				refinement = match.group(5);
				date.setMonth(Month.fromNameForm(match.group(6)));
			}
			else {
				date.setMonth(Month.MONTH);
			}
		}
		//Обработка выражения с годом
		else if (match.group(12) != null) {
			refinement = match.group(11);
			date.setYear();
			date.setCounting(Counting.fromNameForm(match.group(9)));
			if (match.group(7) != null) {
				date.setMonth(Month.fromNameForm(match.group(7)));
			} else if (match.group(8) != null) {
				date.setSeason(Season.fromNameForm(match.group(8)));
			} else if (match.group(10) != null) {
				date.setTimePeriod(TimePeriod.fromNameForm(match.group(10)));
			} 
		}
		//Обработка века
		else if (match.group(16) != null) {
			date.setCentury();
			refinement = match.group(15);
			dec = getTenYears(match.group(13));
		}
		//Обработка дня недели 
		else if (match.group(18) != null) {
			date.setDay(Day.fromNameForm(match.group(18)));
		} 
		//Обработка месяца
		else if (match.group(19) != null) {
			date.setMonth(Month.fromNameForm(match.group(19)));
		} 
		//Обработка времени года
		else if (match.group(20) != null) {
			date.setSeason(Season.fromNameForm(match.group(20)));
		} 
		else if (match.group(21) != null) {
			date.setTimePeriod(TimePeriod.fromNameForm(match.group(21)));
		} 
		if (refinement != null)
			date.setRefinementTime(TimeAdj.fromNameForm(refinement));
		if (dec != null)
			date.setDecadeOfCentury(dec);
		if (partOfSmth != null)
			date.setPartOfSmth(PartOfSmth.fromNameForm(partOfSmth));
		
		return date.getIFreeFormDate();
		
		
		//date.getIFreeFormDate();
		//return null;
	}
}
