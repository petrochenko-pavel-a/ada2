package com.onpositive.parsers.dates;

import java.time.LocalDate;


public class Period {
	
	private static final String DAY_PERIOD_SUFFIX = "/P1D";
	private String startDate;
	private String endDate;

	public Period() {
		// Default constructor
	}
	
	/**
	 * Construct Period
	 * @param startDate - start date string, ISO-8601
	 * @param endDate - end date string, ISO-8601
	 */
	public Period(String startDate, String endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	/**
	 * Construct one-day period
	 * @param date Date
	 */
	public Period(String date) {
		this.startDate = date;
		this.endDate = date;
	}

	public Period(IDatePeriod date) {
		startDate = date.getStartDate().toString();
		endDate = date.getEndDate().toString();
	}
	
	/**
	 * Construct Period from {@link IFreeFormDate}
	 * @param date {@link IFreeFormDate} to init Period with
	 */
	public Period(IFreeFormDate date) {
		LocalDate start = date.getStartDate();
		LocalDate end = date.getEndDate();
		if (start != null && start.getYear() <=0 )
			start = start.minusYears(1);
		if (end != null && end.getYear() <=0 )
			end = end.minusYears(1);
		if (start != null && end != null) {
//			if (date instanceof FreeFormDate) { //TODO possibly optimize period representation in future
//				Integer year = ((FreeFormDate) date).getYear();
//				Month month = ((FreeFormDate) date).getMonth();
//				MonthPart monthPart = ((FreeFormDate) date).getMonthPart();
//				YearsCount yearCount = ((FreeFormDate) date).getYearCount();
//				if (year != null && month == null && monthPart == null && ((FreeFormDate) date).getDay() == null) {
//					String suffix = "/P1Y";
//					if (yearCount != null) {
//						switch (yearCount) {
//						case ONE:
//							suffix = "/P1Y";
//							break;
//						case TEN:
//							suffix = "/P10Y";
//							break;
//						case HUNDRED:
//							suffix = "/P100Y";
//							break;
//						}
//					}
//					startDate = year + suffix;
//				} else if (((FreeFormDate) date).getDay() == null && month != null && year != null) {
//					startDate = year + "-" + DateUtil.getMonthIdx(month) + "/P1M";  
//				}
//			} 
			startDate = date.toISOCalendarString();
			if (!startDate.endsWith(DAY_PERIOD_SUFFIX)) {
				startDate = start.toString();
				endDate = end.toString();
			}
		} else if (start != null) {
			startDate = endDate = start.toString();
		} else if (end != null) {
			startDate = endDate = end.toString();
		} else {
			throw new IllegalArgumentException(date + " is not a valid date");
		}
	}
	public IFreeFormDate getFreeFormDate() {
		return new FreeFormDateRange(
				new FreeFormDate(LocalDate.parse(startDate)),
				new FreeFormDate(LocalDate.parse(endDate)));
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String toString() {
		if (endDate == null) {
			return startDate;
		}
		if (startDate != null && startDate.equals(endDate)) {
			return startDate + DAY_PERIOD_SUFFIX;
		}
		return startDate + "/" + endDate;
	}

}
