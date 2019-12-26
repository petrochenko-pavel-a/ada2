package com.onpositive.parsers.dates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.parsers.dates.templates.DTBCCustomCases;
import com.onpositive.parsers.dates.templates.DTContextDateSpecialCases;
import com.onpositive.parsers.dates.templates.DTContextDates;
import com.onpositive.parsers.dates.templates.DTDayMonthYear;
import com.onpositive.parsers.dates.templates.DTDaysInterval;
import com.onpositive.parsers.dates.templates.DTDecadesAndCentures;
import com.onpositive.parsers.dates.templates.DTDigitYear;
import com.onpositive.parsers.dates.templates.DTOnlyDigits;
import com.onpositive.parsers.dates.templates.DTSimpleYearRange;
import com.onpositive.parsers.dates.templates.DateTemplate;
import com.onpositive.parsers.dates.templates.DateTemplate.DatesMap;

public class MultiRegexDateParser {

	public static enum DateParserType {
		SIMPLE_PARSER, 
		CONTEXT_PARSER,
		SIMPLE_YEAR_RANGE_PARSER,
		CONFIDENT_DATES_SOURCE_PARSER,
		CONTEXT_YEAR_RANGE_PARSER,
		CONTEXT_CONFIDENT_DATES_SOURCE_PARSER;
	}
	
	private DateParserType type;
	private MultiRegexDateParser(DateParserType type) {
		this.type = type;
	}
	
	public DateParserType getType() {
		return type;
	}
	
	private static HashMap<DateParserType,MultiRegexDateParser> parsers = new HashMap<>();
	public static MultiRegexDateParser getInstance(DateParserType type)
	{
		if (!parsers.containsKey(type))
		{
			MultiRegexDateParser newParser = new MultiRegexDateParser(type);
			newParser.registerTemplate(new DTOnlyDigits());
			newParser.registerTemplate(new DTDecadesAndCentures());
			newParser.registerTemplate(new DTDaysInterval());
			newParser.registerTemplate(new DTDayMonthYear());
			newParser.registerTemplate(new DTBCCustomCases());
			if (type == DateParserType.CONTEXT_PARSER || 
				type == DateParserType.CONTEXT_YEAR_RANGE_PARSER ||
				type == DateParserType.CONTEXT_CONFIDENT_DATES_SOURCE_PARSER)
			{
				newParser.registerContextParsers();
			} else {
				if (type == DateParserType.SIMPLE_YEAR_RANGE_PARSER || 
					type == DateParserType.CONFIDENT_DATES_SOURCE_PARSER || 
					type == DateParserType.CONTEXT_YEAR_RANGE_PARSER || 
					type == DateParserType.CONTEXT_CONFIDENT_DATES_SOURCE_PARSER)
					newParser.registerTemplate(new DTSimpleYearRange());
					//newParser.registerTemplate(new DTEventNames());
				if (type == DateParserType.CONFIDENT_DATES_SOURCE_PARSER || 
					type == DateParserType.CONTEXT_CONFIDENT_DATES_SOURCE_PARSER)
					newParser.registerTemplate(new DTDigitYear());
			}
			parsers.put(type, newParser);
		}
		
		return parsers.get(type);
	}
	
	public void registerContextParsers()
	{
		registerTemplate(new DTContextDateSpecialCases());
		registerTemplate(new DTContextDates());
	}
	
	private ArrayList<DateTemplate> templates = new ArrayList<>();
	
	public void registerTemplate(DateTemplate t)
	{
		templates.add(t);
	}
	
	public void unregisterLastAdd()
	{
		templates.remove(templates.size() - 1);
	}
	
	
	public List<IFreeFormDate> parse(String text) {
		return parse(text, new ArrayList<Integer>());
	}
	public List<IFreeFormDate> parse(String text, LocalDate baseDate) {
		return parse(text, new ArrayList<Integer>(), baseDate);
	}

	public List<IFreeFormDate> parse(String text, List<Integer> positions) {
		return parse(text, positions,null);
	}
	public List<IFreeFormDate> parse(String text, List<Integer> positions, LocalDate baseDate) {
		if (text == null || text.length() <=3)
			return new ArrayList <IFreeFormDate>();
		DatesMap datesMap = new DatesMap();
		String textToParse = " "+text+" "; 
		for (DateTemplate t : templates) {
			t.clear();
			t.parse(textToParse, datesMap,baseDate);
		}
		positions.addAll(datesMap.getPositions());
		/*for (IFreeFormDate date:datesMap.getDatesArray()) {
			//LoggerFactory.getLogger(getClass()).info("datesToParse.add(new TextDateStructure(\""+text+"\",");
			//LoggerFactory.getLogger(getClass()).info("\t\t\t\""+date+"\",");
			//LoggerFactory.getLogger(getClass()).info("\t\t\t\""+date.toISOString()+"\",");
			//LoggerFactory.getLogger(getClass()).info("\t\t\t\""+date.toISOString()+"\",");
			//LoggerFactory.getLogger(getClass()).info("\t\t\t\""+date.getStartDate()+"\",");
			//LoggerFactory.getLogger(getClass()).info("\t\t\t\""+date.getEndDate()+"\"));");
		}*/
		
		return datesMap.getDatesArray();		
	}
	public DatesMap parseToMap(String text, LocalDate baseDate) {
		if (text == null || text.length() <=3)
			return new DatesMap();
		DatesMap datesMap = new DatesMap();
		String textToParse = " "+text+" "; 
		for (DateTemplate t : templates) {
			t.clear();
			t.parse(textToParse, datesMap,baseDate);
		}
		return datesMap;		
	}

	public static void main(String[] args) {
		/*MultiRegexDateParser.registerTemplate(new DTOnlyDigits());
		MultiRegexDateParser.registerTemplate(new DTDecadesAndCentures());
		MultiRegexDateParser.registerTemplate(new DTDayMonthYear());*/
		MultiRegexDateParser parser = MultiRegexDateParser.getInstance(DateParserType.CONTEXT_PARSER);
		String toParse[] = {
				"в 10-x годах нашей эры",
				"в августе прошлого года",
				"за второй квартал этого года",
				"за это лето",
				"за лето этого года",
				"за первое полугодие следующего года",
				"три года назад",
				"спустя 2 недели",
				"месяц спустя",
				
				"настоящей средой",
				"в четверг состоялась",
				"в минувшую пятницу",
				
				"в первой декаде прошлого месяца",
				"в первой декаде июля",
				"за прошлую декаду",
				
				"в августе прошлого года",
				"в прошлом мае",
				"в следующем январе",
				"по итогам минувшего месяца",
				"за месяц необходимо выполнить",
				"в следующем месяце",
				
				"за прошлую зиму",
				"за прошлую осень",
				"за прошлую весну",
				
				"в начале первого квартала прошлого года",
				"в начале первой декады августа",
				"в конце третьей декады февраля",
				"в начале третьей декады прошлого месяца",
				"в начале первого полугодия текущего года",
				"в начале второго полугодия следующего года",
				
				"в начале зимы этого года",
				
				
				"в прошлый понедельник",
				"в начале следующего дня",
				"на следующий день",
				"в начале дня",
				"настоящей средой",
				"в четверг состоялась",
				"в минувшую пятницу",
				
				"за прошедшие выходные",
				"на выходных",
				"на следующих выходных",

				"на прошлой неделе",
				"в конце недели",
				"в начале следующей недели",
				
				"в августе прошлого года",
				"в прошлом мае",
				"в следующем январе",
				"по итогам минувшего месяца",
				"за месяц необходимо выполнить",
				"в следующем месяце",
				
				"за прошлый год",
				"этот год был богат на",
				"в следующем году",
				
				"за прошлое лето",
				"за прошлую зиму",
				"за прошлую осень",
				"за прошлую весну",
				"следующая зима",
				
				"осенью всё увядает",
				
				"за минувший квартал было",
				"за текущий квартал необходимо",
				"в следующем квартале",
				
				"за прошедшие полгода",
				"в следующем полугодии",
				"к концу полугодия",
				"в начале века",
				"от прошлого века нам досталось",
				"следующий век станет веком",
				
				"за прошлую декаду",
				"на текущей декаде",
				"в течение следующей декады",

				"завтра всё будет лучше чем вчера",
				"сегодня вчерашний завтрашний:)",
				"позавчера",
				"послезавтра",
				
				"от прошлого века нам досталось",
				"к началу следующего полугодия",
				"за следующие полгода",
				"в начале века",
				"в начале полугодия",
				"2 года назад",
				"спустя три недели",
				/*" в начале 988 года ",
				" в августе 988 года ",
				" в августе 1988 года ",
				" в ноябре 988 года ",
				" 15 мая ",
				" 15 мая 988 года ",
				" в 1964 году ",
				" в начале мая ",
				" август ",
				" 15 мая 2011 ",
				" 18 ноября 1980 года ",
				" в августе 1980 года ",
				" в декабре 1942 г.  ",
				" в конце мая 2016 года  ",
				" в 988 году  ",
				" в 102 году  ",
				" в начале апреля 2016  ",
				" в начале весны 1945 года ",
				" весна 1945 года ",
				" в конце 2016 года  ",
				" с 17 июля 1942 ",
				" с мая по сентябрь 1956 ",
				" после 17 июля 1942 ",
				" с конца 1942 ",
				" до 1 января 1942 ",
				" до конца 1942 ",
				" 15 мая - 17 июня 2011 ",
				" с 17 июля 1942 по 2 февраля 1943 года",
				" 17 июля 1942 — 2 февраля 1943 ",
				" 22 июня 1941 — 18 ноября 1942 ",
				" с 7 сентября по 22 октября 1941 ",
				" 16 декабря 1775 — 18 июля 1817 ",

                " 13.12.1985 ",
                " 13.12.1985-14.12.1985 ",
                " с 13.12.1985 по 14.12.1985 ",
                " после 13.12.1985 ",
                " до 13.12.1985 ",
                " по 13.12.1985 ",
                */
                /*" в 1940-х ",
                " в 1940ых ",
                " в 1940ые ",
                " в 1940-е  ",
                " в 1940е  ",
                " в 1940е годы ",*/
				" I век ",
				" девяностые годы II века до н.э. ",
				" начало девяностых годов II века до н.э. ",
				" I век до н.э. ",
				" XI век до н.э. ",
				" I век до н.э. - III век н.э."
				/*" генрих XI  ",
                " по 1940-е  ",
                " после 1940-х  ",
                " в начале 90-х XX века ",
                " в 90-х XX века ",
                " в девяностых годах XX века  ",
                " с XX по XXI век  ",
                " в девяностых годах ",
                " в конце 1990-х ",
                " в начале 1990-х ",
                " в 1990-х ",
                " в XV веке " ,
                " в начале XV века ",
                " в конце XV века ",
                " в двадцатых годах XVII века  ",
                " восьмидесятые годы " ,
                " V в. ",
                " V-VI вв. ",
                " в 1820-х  ",
                " после 1820-х  ",
                " до XI века ",
                " после XIX века ",
                " в 30х годах XIX века  "*/
		                    };
		for (String s : toParse) {
			//LoggerFactory.getLogger(getClass()).info("-------\n"+s);
			ArrayList<Integer> arrayList = new ArrayList<Integer>();
			List<IFreeFormDate> parse = parser.parse(s,arrayList,LocalDate.now());
			System.out.println(parse);
		}
			
	}
}
