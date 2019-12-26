package com.onpositive.parsers.dates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.onpositive.parsers.dates.parts.Counting;
import com.onpositive.parsers.dates.parts.TimeAdj;
import com.onpositive.parsers.dates.parts.TimePeriod;

public class ContextDateSpecialCase extends ContextDate {
	
	private Byttp byttp;
	private SmthCounting count;
	private boolean back; 
	
	public static enum Byttp {
		BEFOREYESTERDAY("позавчера"),
		YESTERDAY("вчера"),
		TODAY("сегодня"),
		TOMORROW("завтра"),
		PASTTOMORROW("послезавтра");

		private List<String> forms;
		
		private Byttp(String... forms) {
			this.forms = Lists.newArrayList(forms);
		}
		
		public List<String> getForms() {
			return Collections.unmodifiableList(forms);
		}

		public static List<String> getAllForms() {
			List<String> result = new ArrayList<String>();
			
			for (Byttp currentValue : values()) {
				result.addAll(currentValue.getForms());
			}
			
			return result;
		}
		
		public static Byttp fromNameForm(String nameForm) {
			if (nameForm == null) {
				return null;
			}
			
			String lowercasedNameForm = nameForm.toLowerCase();
			
			for (Byttp currentValue : Byttp.values()) {
				List<String> forms = currentValue.getForms();
				
				for (String currentForm : forms) {
					if (lowercasedNameForm.equals(currentForm)) {
						return currentValue;
					}
				}
			}
			
			return null;
		}
	}
	
	public static enum SmthCounting {
		ONE("1", "один"),
		TWO("2", "два", "две"),
		THREE("3", "три"),
		FOUR("4", "четыре"),
		FIVE("5", "пять"),
		SIX("6", "шесть"),
		SEVEN("7", "семь"),
		EIGTH("8", "восемь"),
		NINE("9", "девять"),
		TEN("10", "десять");

		private List<String> forms;
		
		private SmthCounting(String... forms) {
			this.forms = Lists.newArrayList(forms);
		}
		
		public List<String> getForms() {
			return Collections.unmodifiableList(forms);
		}

		public static List<String> getAllForms() {
			List<String> result = new ArrayList<String>();
			
			for (SmthCounting currentValue : values()) {
				result.addAll(currentValue.getForms());
			}
			
			return result;
		}
		
		public static SmthCounting fromNameForm(String nameForm) {
			if (nameForm == null) {
				return null;
			}
			
			String lowercasedNameForm = nameForm.toLowerCase();
			
			for (SmthCounting currentValue : SmthCounting.values()) {
				List<String> forms = currentValue.getForms();
				
				for (String currentForm : forms) {
					if (lowercasedNameForm.equals(currentForm)) {
						return currentValue;
					}
				}
			}
			
			return null;
		}
	}
	
	public ContextDateSpecialCase(LocalDate base) {
		super(base);
		this.count = null;
		this.byttp = null;
		this.back = false;
	}

	@Override
	public IFreeFormDate getIFreeFormDate() {
		if (count == null) 
			count = SmthCounting.ONE;
		if (refinementTime == null)
			refinementTime = TimeAdj.PRESENTTIME;
		if (byttp != null) {
			return getDateFromByttp();
		} else if (back != false) {
			if (day != null) {
				return getBackDateFromDay();
			} else if (period.is(TimePeriod.WEEK)) {
				return getBackDateFromWeek();
			} else if (month != null) {
				return getBackDateFromMonth();
			} else if (period.is(TimePeriod.HALF_YEAR)) {
				return getBackDateFromHalfYear();
			} else if (period.is(TimePeriod.YEAR)) {
				return getBackDateFromYear();
			}
		}
		return null;
	}
	
	private IFreeFormDate getDateFromByttp() {
		//Возвращает дату описанного дня.
		LocalDate newDate = null;
		switch (byttp){
			case BEFOREYESTERDAY:
				newDate = baseDate.minusDays(2);
				break;
			case YESTERDAY:
				newDate = baseDate.minusDays(1);
				break;
			case TODAY:
				newDate = baseDate;
				break;
			case TOMORROW:
				newDate = baseDate.plusDays(1);
				break;
			case PASTTOMORROW:
				newDate = baseDate.plusDays(2);
				break; 
		}
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}

	private IFreeFormDate getBackDateFromDay() {
		//Возвращает дату описанного дня.
		LocalDate newDate = baseDate.minusDays(count.ordinal() + 1);
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}
	
	private IFreeFormDate getBackDateFromWeek() {
		//Возвращает дату описанного дня.
		LocalDate newDate = baseDate.minusWeeks(count.ordinal() + 1);
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}
	
	private IFreeFormDate getBackDateFromMonth() {
		//Возвращает дату описанного дня.
		LocalDate newDate = baseDate.minusMonths(count.ordinal() + 1);
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}
	
	private IFreeFormDate getBackDateFromHalfYear() {
		//Возвращает дату описанного дня.
		LocalDate newDate = baseDate.minusMonths(6);
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}
	
	private IFreeFormDate getBackDateFromYear() {
		//Возвращает дату описанного дня.
		LocalDate newDate = baseDate.minusYears(count.ordinal() + 1);
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}
	
	public Counting getCounting() {
		return counting;
	}

	public void setCounting(Counting counting) {
		this.counting = counting;
	}

	public Byttp getByttp() {
		return byttp;
	}

	public void setByttp(Byttp byttp) {
		this.byttp = byttp;
	}

	public SmthCounting getCount() {
		return count;
	}

	public void setCount(SmthCounting count) {
		this.count = count;
	}

	public boolean isBack() {
		return back;
	}

	public void setBack(boolean back) {
		this.back = back;
	}
}
