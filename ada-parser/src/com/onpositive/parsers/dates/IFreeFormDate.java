package com.onpositive.parsers.dates;

import java.io.IOException;


/**
 * Интерфейс для дат, заданных в свободной форме, наприемер, "середина сентября".
 */
public interface IFreeFormDate extends IDatePeriod{
	/**
	 * Checks if specified date is inside the rage set by the current date.
	 * In example, returns true for current date being "1980" and specified date being "18 may 1980".
	 * @param date
	 * @return
	 */
	public boolean intersects(IFreeFormDate date);
	
	public long intersectLength(IFreeFormDate date);

	public long intersectLengthInDays(IFreeFormDate date);
	
	public boolean isComplete();

	public boolean isCorrect();
	
	public long pack() throws IOException;

	public long length();
	
	public long lengthInDays();
	
	public float lengthInYears();

	public String toISOString();

	public String toISOCalendarString();
	
}
