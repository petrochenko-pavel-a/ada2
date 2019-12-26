package com.onpositive.parsers.dates;

import java.time.LocalDate;

import com.onpositive.parsers.dates.parts.Counting;
import com.onpositive.parsers.dates.parts.Day;
import com.onpositive.parsers.dates.parts.Month;
import com.onpositive.parsers.dates.parts.PartOfSmth;
import com.onpositive.parsers.dates.parts.Season;
import com.onpositive.parsers.dates.parts.TimeAdj;
import com.onpositive.parsers.dates.parts.TimePeriod;

public class ContextDate implements IDatePeriod{
	
	protected Day day;
	protected TimePeriodMap period;
	protected Month month;
	protected Season season;
	protected Integer tenYears;
	
	protected TimeAdj refinementTime;
	protected PartOfSmth partOfSmth;
	protected Counting counting;
	
	
	protected LocalDate baseDate;
	
	protected static class TimePeriodMap {
		private int map = 0;
		public void setWeekend() {
			this.map |= 1<<TimePeriod.WEEKEND.ordinal();
		}
	 
		public void setWeek() {
			this.map |= 1<<TimePeriod.WEEK.ordinal();
		}

		public void setDecade() {
			this.map |= 1<<TimePeriod.DECADE.ordinal();
		}

		public void setQuarter() {
			this.map |= 1<<TimePeriod.QUARTER.ordinal();
		}

		public void setHalfYear() {
			this.map |= 1<<TimePeriod.HALF_YEAR.ordinal();
		}

		public void setYear() {
			this.map |= 1<<TimePeriod.YEAR.ordinal();
		}

		public void setCentury() {
			this.map |= 1<<TimePeriod.CENTURY.ordinal();
		}
		boolean is(TimePeriod period) {
			return (this.map & (1<<period.ordinal())) != 0;
		}

		public void setTimePeriod(TimePeriod period) {
			this.map |= 1<<period.ordinal();
		}
	}
	
	public ContextDate(LocalDate base) {
		this.day = null;
		this.period = new TimePeriodMap();
		this.month = null;
		this.season = null;
		this.tenYears = null;
		this.refinementTime = null;
		this.partOfSmth = null;
		this.baseDate = base;
	}

	public IFreeFormDate getIFreeFormDate() {
		if (refinementTime == null)
			refinementTime = TimeAdj.PRESENTTIME;
		IFreeFormDate iffd = null;
		if (day != null) {
			iffd = getDateFromDay();
		}  else if (period.is(TimePeriod.WEEKEND)) {
			iffd = getDateFromWeekend();
		}  else if (period.is(TimePeriod.WEEK)) {
			iffd = getDateFromWeek();
		} else if (period.is(TimePeriod.DECADE)) {
			iffd = getDateFromDecade();
		} else if (period.is(TimePeriod.YEAR)) {
			iffd = getDateFromYear();
		} else if (month != null) {
			iffd = getDateFromMonth();
		}  else if (season != null) {
			iffd = getDateFromSeason();
		}  else if (period.is(TimePeriod.QUARTER)) {
			iffd = getDateFromQuarter();
		}  else if (period.is(TimePeriod.HALF_YEAR)) {
			iffd = getDateFromHalfYear();
		}  else if (period.is(TimePeriod.CENTURY)) {
			iffd = getDateFromCentury();
		}
		if (partOfSmth != null)
			iffd = getPartOfDate(iffd);
		//LoggerFactory.getLogger(getClass()).info(iffd.getStartDate().toString() + "\n" + iffd.getEndDate().toString() + "\n----------");
		return iffd;
	}

	private IFreeFormDate getDateFromDay() {
		//Возвращает дату описанного дня. Если время не уточнено, то возвращает требуемый день ближайший к базовой дате (только для дней недели).
		LocalDate newDate = null;
		switch (day){
			case DAY:
				switch (refinementTime){
					case PASTTIME:
						newDate = baseDate.minusDays(1);
						break;
					case PRESENTTIME: 
						newDate = baseDate;
						break;
					case FUTURETIME:
						newDate = baseDate.plusDays(1);
						break;
				}
				break;
			case DAYS:
				return null;
			default:
				switch (refinementTime){
					case PASTTIME:
						int dayDiff = baseDate.getDayOfWeek().ordinal() - day.ordinal();
						if (dayDiff <= 0)
							dayDiff +=7;
						newDate = baseDate.minusDays(dayDiff);
						break;
					case PRESENTTIME: 
						if (baseDate.getDayOfWeek().ordinal() - day.ordinal() < 0)
							newDate = baseDate.minusDays(baseDate.getDayOfWeek().ordinal() - day.ordinal());
						else
							newDate = baseDate.minusDays(-baseDate.getDayOfWeek().ordinal() + day.ordinal());
						break;
					case FUTURETIME:
						int dayDiff1 = day.ordinal() - baseDate.getDayOfWeek().ordinal();
						if (dayDiff1 <= 0)
							dayDiff1 +=7;
						newDate = baseDate.plusDays(dayDiff1);
						break;
				}
				break;
		}
		//LoggerFactory.getLogger(getClass()).info(newDate.toString() + "\n----------");
		return new FreeFormDate(newDate);
	}
	
	private IFreeFormDate getDateFromWeekend() {
		// Возвращает промежуток выходных дней. Если время не указано, то вернет промежуток выходных дней текущей недели.
		LocalDate startDate = null, endDate = null;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(2 + baseDate.getDayOfWeek().ordinal());
				endDate = startDate.plusDays(1);
				break;
			case PRESENTTIME: 
				startDate = baseDate.plusDays(5 - baseDate.getDayOfWeek().ordinal());
				endDate = startDate.plusDays(1);
				break;
			case FUTURETIME:
				startDate = baseDate.plusDays(12 - baseDate.getDayOfWeek().ordinal());
				endDate = startDate.plusDays(1);
				break;
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromWeek() {
		// Возвращает проемежуток недели. Если время не указано, то вернет промежуток текущей недели.
		LocalDate startDate = null, endDate = null;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfWeek().ordinal() + 7);
				break;
			case PRESENTTIME: 
				startDate = baseDate.minusDays(baseDate.getDayOfWeek().ordinal());
				break;
			case FUTURETIME:
				startDate = baseDate.plusDays(7 - baseDate.getDayOfWeek().ordinal());
				break;
		}
		endDate = startDate.plusDays(6);
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}

	private IFreeFormDate getDateFromDecade() {
		// Возвращает промежуток декады. Если время не указано, то вернёт промежуток текущей декады.
		LocalDate startDate = null, endDate = null;
		if (counting != null) { 
			switch (month) {
				case MONTH:
					switch (refinementTime){
						case PASTTIME:
							startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1).minusMonths(1);
							break;
						case PRESENTTIME: 
							startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1);
							break;
						case FUTURETIME:
							startDate = baseDate.plusMonths(1).minusDays(baseDate.getDayOfMonth() - 1);
							break;
					}
					break;
				default:
					switch (refinementTime){
						case PASTTIME:
							startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(month.ordinal());
							break;
						case PRESENTTIME: 
							startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(month.ordinal());
							break;
						case FUTURETIME:
							startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(month.ordinal());
							break;
					}
					endDate = startDate.plusMonths(1).minusDays(1);
					break;
			}
			switch (counting) {
				case FIRST: 
					endDate = startDate.plusDays(9);
					break;
				case SECOND:
					startDate = startDate.plusDays(10);
					endDate = startDate.plusDays(9);
					break;
				case THIRD:
					startDate = startDate.plusDays(20);
					endDate = startDate.minusDays(startDate.getDayOfMonth()).plusMonths(1);
					break;
				default:
					break;
			}
		}
		else {
		int numberDecade = (baseDate.getDayOfMonth() - 1) / 10;
			switch (refinementTime){
				case PASTTIME:
					if (numberDecade == 0) 
						startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1).minusMonths(1).plusDays(20);
					else 
						startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1).plusDays((numberDecade - 1) * 10);
					break;
				case PRESENTTIME: 
					startDate = baseDate.plusDays( - baseDate.getDayOfMonth() + 10 * numberDecade);
					break;
				case FUTURETIME:
					if (numberDecade == 2) 
						startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1).plusMonths(1);
					else 
						startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1).plusDays((numberDecade + 1) * 10);
					break;
			}
			if (Math.abs(startDate.lengthOfMonth() - (startDate.getDayOfMonth() + 10)) < 5) {
				endDate = startDate.plusMonths(1).minusDays(startDate.getDayOfMonth());
			}
			else {
				endDate = startDate.plusDays(10);
			}
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromMonth() {
		// Возвращает промежуток месяца. Если время не указано, то вернет промежуток текущего месяца.
		LocalDate startDate = null, endDate = null;
		switch (month) {
			case MONTH:
				switch (refinementTime){
					case PASTTIME:
						startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1).minusMonths(1);
						break;
					case PRESENTTIME: 
						startDate = baseDate.minusDays(baseDate.getDayOfMonth() - 1);
						break;
					case FUTURETIME:
						startDate = baseDate.plusMonths(1).minusDays(baseDate.getDayOfMonth() - 1);
						break;
				}
				endDate = startDate.plusMonths(1).minusDays(1);
				break;
			default:
				switch (refinementTime){
					case PASTTIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(month.ordinal());
						break;
					case PRESENTTIME: 
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(month.ordinal());
						break;
					case FUTURETIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(month.ordinal());
						break;
				}
				endDate = startDate.plusMonths(1).minusDays(1);
				break;
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromSeason() {
		// Возвращает промежуток времени года. Если время не указано, то вернет промежуток текущего времени года.
		LocalDate startDate = null, endDate = null;
		switch (season){
			case SPRING:
				switch (refinementTime){
					case PASTTIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(2);
						break;
					case PRESENTTIME: 
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(2);
						break;
					case FUTURETIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(2);
						break;
				}
				endDate = startDate.plusMonths(3).minusDays(1);
				break;
			case SUMMER:
				switch (refinementTime){
					case PASTTIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(5);
						break;
					case PRESENTTIME: 
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(5);
						break;
					case FUTURETIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(5);
						break;
				}
				endDate = startDate.plusMonths(3).minusDays(1);
				break;
			case FALL:
				switch (refinementTime){
					case PASTTIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(8);
						break;
					case PRESENTTIME: 
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(8);
						break;
					case FUTURETIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(8);
						break;
				}
				endDate = startDate.plusMonths(3).minusDays(1);
				break;
			case WINTER:
				switch (refinementTime){
					case PASTTIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(11);
						break;
					case PRESENTTIME: 
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(11);
						break;
					case FUTURETIME:
						startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(11);
						break;
				}
				endDate = startDate.plusMonths(3).minusDays(1);
				break;
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromQuarter() {
		// Возвращает промежуток квартала. Если время не указано, то вернет промежуток текущего квартала.
		LocalDate startDate = null, endDate = null;
		int currentQuarter = (baseDate.getMonthValue() - 1) / 3;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(3 * (currentQuarter - 1) % 12);
				break;
			case PRESENTTIME: 
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(3 * currentQuarter % 12);
				break;
			case FUTURETIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(3 * (currentQuarter + 1) % 12).plusYears((currentQuarter + 1) / 4);
				break;
		}
		endDate = startDate.plusMonths(3).minusDays(1);
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromHalfYear() {
		// Возвращает промежуток полугодия. Если время не указано, то вернёт промежуток текущего полугодия.
		LocalDate startDate = null, endDate = null;
		int numberHalfYear = (baseDate.getMonthValue() - 1) / 6;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(6 * (numberHalfYear - 1) % 12);
				break;
			case PRESENTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(6 * numberHalfYear % 12);
				break;
			case FUTURETIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(6 * (numberHalfYear + 1) % 12).plusYears(numberHalfYear);
				break;
		}
		endDate = startDate.plusMonths(6).minusDays(1);
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromYear() {
		// Возвращает промежуток года. Если время не указано, то вернёт промежуток текущего года.
		// Если указан месяц, то вернёт промежуток месяца указанного года.
		LocalDate startDate = null, endDate = null;
		if (month != null) { 
			switch (refinementTime){
				case PASTTIME:
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(month.ordinal());
					break;
				case PRESENTTIME: 
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(month.ordinal());
					break;
				case FUTURETIME:
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusYears(1).plusMonths(month.ordinal());
					break;	
			}	
			endDate = startDate.plusMonths(1).minusDays(1);
		} else if (season != null) { 
			return getDateFromSeasonWithYear();
		} else if (period.is(TimePeriod.QUARTER)) { 
			return getDateFromQuarterWithYear();
		} else if (period.is(TimePeriod.HALF_YEAR)) { 
			return getDateFromHalfYearWithYear();
		} else {
			switch (refinementTime){
				case PASTTIME:
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1);
					break;
				case PRESENTTIME: 
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1);
					break;
				case FUTURETIME:
					startDate = baseDate.plusYears(1).minusDays(baseDate.getDayOfYear() - 1);
					break;
			}
			endDate = startDate.plusYears(1).minusDays(1);
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromQuarterWithYear() {
		// Возвращает промежуток квартала. Если время не указано, то вернёт промежуток текущего квартала.
		// Если указан месяц, то вернёт промежуток месяца указанного года.
		LocalDate startDate = null, endDate = null;
		if (period.is(TimePeriod.QUARTER)) { 
			switch (refinementTime){
				case PASTTIME:
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1).plusMonths(3 * counting.ordinal());
					break;
				case PRESENTTIME: 
					startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).plusMonths(3 * counting.ordinal());
					break;
				case FUTURETIME:
					startDate = baseDate.plusYears(1).minusDays(baseDate.getDayOfYear() - 1).plusMonths(3 * counting.ordinal());
					break;
			}
			endDate = startDate.plusMonths(3).minusDays(1);
		} else {
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromSeasonWithYear() {
		// Возвращает время года. Если время не указано, то вернёт время текущего года.
		LocalDate startDate = null, endDate = null;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1);
				break;
			case PRESENTTIME: 
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1);
				break;
			case FUTURETIME:
				startDate = baseDate.plusYears(1).minusDays(baseDate.getDayOfYear() - 1);
				break;
		}
		switch (season){
		case SPRING:
			startDate = startDate.plusMonths(2);
			break;
		case SUMMER:
			startDate = startDate.plusMonths(5);
			break;
		case FALL:
			startDate = startDate.plusMonths(8);
			break;
		case WINTER:
			startDate = startDate.plusMonths(-1);
			break;
		}
		endDate = startDate.plusMonths(3).minusDays(1);
	
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromHalfYearWithYear() {
		// Возвращает время года. Если время не указано, то вернёт время текущего года.
		LocalDate startDate = null, endDate = null;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1).minusYears(1);
				break;
			case PRESENTTIME: 
				startDate = baseDate.minusDays(baseDate.getDayOfYear() - 1);
				break;
			case FUTURETIME:
				startDate = baseDate.plusYears(1).minusDays(baseDate.getDayOfYear() - 1);
				break;
		}
		switch (counting) {
		case FIRST: 
			endDate = startDate.plusMonths(6).minusDays(1);
			break;
		case SECOND:
			startDate = startDate.plusMonths(6);
			endDate = startDate.plusMonths(6).minusDays(1);
			break;
		default:
			break;
		}
	
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	private IFreeFormDate getDateFromCentury() {
		// Возвращает промежуток века.
		LocalDate startDate = null, endDate = null;
		switch (refinementTime){
			case PASTTIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear());
				while (startDate.getYear() % 100 != 0)
					startDate = startDate.minusYears(1);
				endDate = startDate;
				startDate = endDate.minusYears(100).plusDays(1);
				break;
			case PRESENTTIME: 
				startDate = baseDate.minusDays(baseDate.getDayOfYear());
				while (startDate.getYear() % 100 != 0)
					startDate = startDate.minusYears(1);
				startDate = startDate.plusDays(1);
				endDate = startDate.plusYears(100).minusDays(1);
				break;
			case FUTURETIME:
				startDate = baseDate.minusDays(baseDate.getDayOfYear());
				while (startDate.getYear() % 100 != 0)
					startDate = startDate.plusYears(1);
				startDate = startDate.plusDays(1);
				endDate = startDate.plusYears(100).minusDays(1);
				break;
		}
		if (this.tenYears != null) {
			startDate = startDate.plusYears(this.tenYears - 1);
			endDate = startDate.plusYears(10).minusDays(1);
		}
		//LoggerFactory.getLogger(getClass()).info(startDate.toString() + "\n" + endDate.toString() + "\n----------");
		return new FreeFormDateRange(new FreeFormDate(startDate), new FreeFormDate(endDate));
	}
	
	protected IFreeFormDate getPartOfDate(IFreeFormDate date) {
		if (date == null)
			return null;
		long lenOfPeriod = date.lengthInDays();
		if (lenOfPeriod != 1) {
			switch (partOfSmth){
			case BEGINNING:
				return new FreeFormDateRange(new FreeFormDate(date.getStartDate()), new FreeFormDate(date.getStartDate().plusDays(lenOfPeriod/3)));
			case MIDDLE:
				return new FreeFormDateRange(new FreeFormDate(date.getStartDate().plusDays(lenOfPeriod/3)), new FreeFormDate(date.getStartDate().plusDays(lenOfPeriod/3*2)));
			case END:
				return new FreeFormDateRange(new FreeFormDate(date.getStartDate().plusDays(lenOfPeriod/3*2)), new FreeFormDate(date.getEndDate()));
			}
		}
		return date;
	}
	
	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public void setWeekend() {
		this.period.setWeekend();
	}
 
	public void setWeek() {
		this.period.setWeek();
	}

	public void setDecade() {
		this.period.setDecade();
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public void setTimePeriod(TimePeriod period) {
		this.period.setTimePeriod(period);
	}
	public void setQuarter() {
		this.period.setQuarter();
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public void setHalfYear() {
		this.period.setHalfYear();
	}

	public void setYear() {
		this.period.setYear();
	}

	public void setCentury() {
		this.period.setCentury();
	}

	public TimeAdj getRefinementTime() {
		return refinementTime;
	}

	public void setRefinementTime(TimeAdj refinementTime) {
		this.refinementTime = refinementTime;
	}
	
	public void setDecadeOfCentury(Integer decade) {
		this.tenYears = decade;
	}

	public PartOfSmth getPartOfSmth() {
		return partOfSmth;
	}

	public void setPartOfSmth(PartOfSmth partOfSmth) {
		this.partOfSmth = partOfSmth;
	}

	public LocalDate getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(LocalDate baseDate) {
		this.baseDate = baseDate;
	}

	public Counting getCounting() {
		return counting;
	}

	public void setCounting(Counting counting) {
		this.counting = counting;
	}

	@Override
	public LocalDate getStartDate() {
		return getIFreeFormDate().getStartDate();
	}

	@Override
	public LocalDate getEndDate() {
		// TODO Auto-generated method stub
		return getIFreeFormDate().getEndDate();
	}

	public long length() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getStartDateInDays() {
		return getIFreeFormDate().getStartDateInDays();
	}

	public long getEndDateInDays() {
		return getIFreeFormDate().getEndDateInDays();
	}


}
