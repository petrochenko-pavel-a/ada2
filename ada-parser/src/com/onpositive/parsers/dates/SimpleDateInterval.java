package com.onpositive.parsers.dates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import com.google.common.primitives.Ints;

public class SimpleDateInterval implements IDatePeriod {

	private int startDay;
	private int endDay;
	
	public int hashCode() {
		return (int)((((long) startDay * (1<<20)) + endDay) % (1<<30 +1));
	}
	public boolean equals(Object other) {
		if (other instanceof IDatePeriod) {
			IDatePeriod period = (IDatePeriod) other;
			long periodStartDay = period.getStartDate() == null ? Integer.MIN_VALUE : period.getStartDate().toEpochDay();
			long periodEndDay = period.getEndDate() == null ? Integer.MAX_VALUE : period.getEndDate().toEpochDay();
			return (startDay == periodStartDay && endDay == periodEndDay);
		}
		return false;
	}
	
	public SimpleDateInterval() {
	}

	public SimpleDateInterval (LocalDate startDay, LocalDate endDay) {
		this((int) startDay.toEpochDay(),(int) endDay.toEpochDay());
	}	
	public SimpleDateInterval (int startDay, int endDay) {
		if (startDay > endDay) {
			int k = endDay;
			endDay = startDay;
			startDay = k;
		}
		this.startDay = startDay;
		this.endDay = endDay;
	}
	public SimpleDateInterval(IDatePeriod date)
	{
		this((int)date.getStartDate().toEpochDay(),(int)date.getEndDate().toEpochDay());
	}
	public long length()
	{
		return endDay - startDay + 1;
	}
	public int startDay()
	{
		return startDay;
	}
	public int endDay()
	{
		return endDay;
	}

	public long intersect(IDatePeriod date)
	{
		long end = date.getEndDateInDays() > endDay ? endDay : date.getEndDateInDays();
		long start = date.getStartDateInDays() < startDay ? startDay : date.getStartDateInDays();
		return end < start ? 0 : end-start+1;
	}
	
	public long union(IDatePeriod date)
	{
		return endDay - startDay + 1 + date.getEndDateInDays() - date.getStartDateInDays() + 1 - intersect(date);
	}
	
	public void write(ByteArrayOutputStream bytes) throws IOException
	{
		bytes.write(Ints.toByteArray(startDay));
		bytes.write(Ints.toByteArray(endDay));
	}
	public SimpleDateInterval(byte[] bytes, int start)
	{
		startDay = Ints.fromBytes(bytes[start], bytes[start+1], bytes[start+2], bytes[start+3]);
		start += 4;
		endDay = Ints.fromBytes(bytes[start], bytes[start+1], bytes[start+2], bytes[start+3]);
	}
	public IFreeFormDate toFreeFormDate()
	{
		return new FreeFormDateRange(
					new FreeFormDate(getStartDate()),
					new FreeFormDate(getEndDate())
				);
	}

	@Override
	public LocalDate getStartDate() {
		return LocalDate.ofEpochDay(startDay);
	}

	@Override
	public LocalDate getEndDate() {
		return LocalDate.ofEpochDay(endDay);
	}
	
	public String toString() {
		return getStartDate().toString()+"/P"+(endDay-startDay+1)+"D";
	}
	@Override
	public long getStartDateInDays() {
		return startDay;
	}
	@Override
	public long getEndDateInDays() {
		return endDay;
	}
}
