package com.onpositive.parsers.dates.templates;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.onpositive.parsers.dates.FreeFormDate;
import com.onpositive.parsers.dates.IFreeFormDate;

/**
 * Парсит даты из названий событий
 * Евровидение-2018
 * 
 * Использует список из конфига years_prefixes.conf
 * TODO Возможно, надо отрефакторить функцию загрузки фалйа
 */
public class DTEventNames extends DateTemplate {
	private Pattern pattern = null;

	@Override
	protected Pattern getRegex() {
//		if (pattern == null) synchronized (this) {
//			List<String> patterns = new ArrayList<String>();
//			patterns.add("([^ \\d]{2}[^ ]+),");
//			Path rootIndexPath = ConfigurationUtil.getConfigPath();
//			File f = new File(rootIndexPath.resolve("years_prefixes.conf").toString());
//			if(f.exists())
//			{
//				BufferedReader reader;
//				try {
//					reader = new BufferedReader(new FileReader(f));
//					String s = reader.readLine();
//					while(s != null)
//					{
//						if(s.length() > 0)
//							patterns.add(s.replace("(", "\\(").replace(")", "\\)").replace(".", "\\."));
//						s = reader.readLine();
//					}
//					reader.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			else { // For testing
//				String[] pr = new String[] {"кубок","Экзомарс","фильм,", "«Евровидение"};
//				for(String s:pr)
//				{
//					patterns.add(s.replace("(", "\\(").replace(")", "\\)").replace(".", "\\."));
//				}
//			}
//			String years = " ([12]\\d{3})";
//			//LoggerFactory.getLogger(getClass()).info(oneOf(patterns, false) + years+oneOf("»",VALID_END));
//			pattern = Pattern.compile(oneOf(patterns, false) + years+oneOf("»",VALID_END));
//		}
		return pattern;
	}

	@Override
	protected IFreeFormDate getDateFromMatch(Matcher match, String text, LocalDate baseDate) {
		//printMatch(match);
		String year = match.group(2);
		Integer yearInt = getYear(year);
		if (yearInt != null) {
			return new FreeFormDate(yearInt, null, null, null, null, null);
		} 
		return null;
	}
}
