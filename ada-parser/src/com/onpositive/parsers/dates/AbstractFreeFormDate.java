package com.onpositive.parsers.dates;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Класс реализует базовый функционал работы с датами, заданными в произвольной форме
 */
public abstract class AbstractFreeFormDate implements IFreeFormDate, Serializable {

	private static final long serialVersionUID = 8453634631052685498L;
	public boolean intersects(IFreeFormDate date) {
		LocalDate currentStartDate = this.getStartDate();
		LocalDate currentEndDate = this.getEndDate();
		
		LocalDate compareStartDate = date.getStartDate();
		LocalDate compareEndDate = date.getEndDate();
		
		return intersects(currentStartDate, currentEndDate, compareStartDate, compareEndDate);
	}
	public long intersectLength(IFreeFormDate date)
	{
		if(intersects(date))
		{
			LocalDate currentStartDate = this.getStartDate();
			LocalDate currentEndDate = this.getEndDate();
			
			LocalDate compareStartDate = date.getStartDate();
			LocalDate compareEndDate = date.getEndDate();
			if (currentStartDate == null && compareStartDate == null ||
				currentEndDate == null && compareEndDate == null )
				return Long.MAX_VALUE;
			LocalDate startDate = currentStartDate != null && 
							(compareStartDate == null || currentStartDate.isAfter(compareStartDate)) ? 
									currentStartDate : 
									compareStartDate;
			LocalDate endDate = currentEndDate != null && 
							(compareEndDate == null || currentEndDate.isBefore(compareEndDate)) ? 
									currentEndDate : 
									compareEndDate;
			
			return ChronoUnit.DAYS.between(startDate, endDate);
		}
		return 0;
	}
	
	@Override
	public String toISOString() {
		if(lengthInDays() == Long.MAX_VALUE)
			if(getStartDate() != null)
				return getStartDate().toString();
			else
				return "";
		return getStartDate().toString()+"/P"+lengthInDays()+"D";
	}
	
	@Override
	public String toISOCalendarString() {
		LocalDate startDateToStr = getStartDate();
		if (startDateToStr != null && startDateToStr.getYear() <=0)
			startDateToStr = startDateToStr.minusYears(1);
		if(lengthInDays() == Long.MAX_VALUE)
		{
			if(startDateToStr != null)
			{
				return getStartDate().toString();
			}
			else
				return "";
		}
		return getStartDate().toString()+"/P"+lengthInDays()+"D";
	}
	
	public long intersectLengthInDays(IFreeFormDate date)
	{
		return intersectLength(date);///(86400000L);	
	}
	
	protected Long getDateAsUnsigned(Date date)
	{
		if(date == null)
			return null;
		long time = date.getTime() >> 2;
		return time;
	}
	public static boolean intersects(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
		return RangeUtils.intersects(
				startDate1!=null?startDate1.toEpochDay():null,
				endDate1!=null?endDate1.toEpochDay():null,
				startDate2!=null?startDate2.toEpochDay():null,
				endDate2!=null?endDate2.toEpochDay():null);
	}
	
	public long length() {
		LocalDate startDate = this.getStartDate();
		LocalDate endDate = this.getEndDate();
		
		if (startDate == null || endDate == null) {
			return Long.MAX_VALUE;
		}
		
		long result = ChronoUnit.DAYS.between(startDate, endDate) + 1;
		return result>0?result:1;
	}

	public long lengthInDays() {
		return length();
	}
	public float lengthInYears() {
		LocalDate startDate = this.getStartDate();
		LocalDate endDate = this.getEndDate();
		
		if (startDate == null || endDate == null) {
			return Long.MAX_VALUE;
		}
		
		return ChronoUnit.YEARS.between(startDate, endDate);

	}
	
}
