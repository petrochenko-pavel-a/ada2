package com.onpositive.parsers.dates.templates;

/**
 * Конструктор выражений с периодами с-по
 */
public class DTRangeTemplate extends TemplateConstructor {
	public static final String dashe = oneOf("-", "—", "–", "−");
	public static final String delimiter = oneOf("\\s","_"," ");

	public static int PREFIX_SIZE = 15;
	public String getRangePattern(String date) {
		String rangeStart = oneOf("с", "после", "со", "начиная");
		String rangeEnd = oneOf("по", "до", "к", "заканчивая", "и заканчивая");
		
		//22 июня 1941 — 18 ноября 1942 ИЛИ 15 мая 2011
		String dashRange = 
				date + 
				optional(some(delimiter) + dashe + some(delimiter) + date);
		
		//с 7 сентября по 22 октября 1941	
		String textualRange =
				oneOf(
				rangeStart + some(delimiter) + date + optional(some(delimiter) + rangeEnd + some(delimiter) + date),
				some(delimiter) + rangeEnd + some(delimiter) + date);
				/*rangeStart + some(delimiter) + date + some(delimiter) + rangeEnd + some(delimiter) + date,
				some(delimiter) + rangeEnd + some(delimiter) + date,
				rangeStart + some(delimiter) + date);*/
		//LoggerFactory.getLogger(getClass()).info(oneOf(dashRange,textualRange));
		return oneOf(dashRange,textualRange);
	}
}
