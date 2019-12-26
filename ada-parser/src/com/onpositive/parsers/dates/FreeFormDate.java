package com.onpositive.parsers.dates;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.TimeZone;

import com.onpositive.parsers.dates.parts.Month;
import com.onpositive.parsers.dates.parts.MonthPart;
import com.onpositive.parsers.dates.parts.YearPart;

/**
 * Класс реализует работу с одиночной датой, заданной в свободной форме (не
 * интервал)
 */
public class FreeFormDate extends AbstractFreeFormDate implements Serializable {

	private static final long serialVersionUID = -8087692250710716159L;

	private static TimeZone STANDARD_TIME_ZONE = TimeZone.getTimeZone("Europe/Moscow");

	public static enum YearsCount {
		ONE, TEN, HUNDRED;
	}

	private Integer year;

	private YearsCount yearsCount;

	private Month month;

	private YearPart yearPart;

	private Integer day;

	private MonthPart monthPart;

	private LocalDate startDate = null;
	private LocalDate endDate = null;

	public FreeFormDate() {

	}

	public static FreeFormDate now() {
		FreeFormDate result = new FreeFormDate();

		Calendar calendar = Calendar.getInstance(STANDARD_TIME_ZONE);
		result.year = calendar.get(Calendar.YEAR);
		result.month = Month.fromMonthNumber(calendar.get(Calendar.MONTH));
		result.day = calendar.get(Calendar.DAY_OF_MONTH);

		return result;
	}

	public FreeFormDate(Integer year, Month month, YearPart yearPart, Integer day, MonthPart monthPart,
			YearsCount yearsCount) {
		this.year = year;
		this.month = month;
		this.yearPart = yearPart;
		this.day = day;
		this.monthPart = monthPart;
		this.yearsCount = yearsCount;
	}

	/**
	 * Construct from ISO-8601 str
	 * 
	 * @param dateStr
	 *            ISO-8601 str
	 */
	public FreeFormDate(String isoStr) {
		this(LocalDate.parse(isoStr));
	}

	public boolean isOnlyYear() {
		return year != null && month == null && monthPart == null && yearPart == null && day == null
				&& yearsCount == YearsCount.ONE;
	}

	public FreeFormDate(LocalDate date) {
		this.year = date.getYear();
		this.month = Month.fromMonthNumber(date.getMonthValue() - 1);
		this.yearPart = null;
		this.day = date.getDayOfMonth();
		this.monthPart = null;
		this.yearsCount = YearsCount.ONE;
	}

	public Integer getYear() {
		return year;
	}

	public YearsCount getYearCount() {
		return yearsCount;
	}

	public void setYear(Integer year) {
		this.year = year;
		startDate = null;
		endDate = null;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
		startDate = null;
		endDate = null;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
		startDate = null;
		endDate = null;
	}

	public MonthPart getMonthPart() {
		return monthPart;
	}

	public void setMonthPart(MonthPart monthPart) {
		this.monthPart = monthPart;
		startDate = null;
		endDate = null;
	}

	public YearPart getYearPart() {
		return yearPart;
	}

	public void setYearPart(YearPart yearPart) {
		this.yearPart = yearPart;
		startDate = null;
		endDate = null;
	}

	public boolean hasMonthOrYearPart() {
		return month != null || yearPart != null;
	}

	public boolean hasDayOrMonthPart() {
		return day != null || monthPart != null;
	}

	public void copyMonthOrYearPartTo(FreeFormDate toDate) {
		toDate.setMonth(this.month);
		toDate.setYearPart(this.yearPart);
	}

	public void copyDayOrMonthPartTo(FreeFormDate toDate) {
		toDate.setDay(this.day);
		toDate.setMonthPart(this.monthPart);
	}

	/**
	 * Gets whether this date represent addressable point in time. In example,
	 * "1980" is addressable, "28 May" is not (because, we do not know the year),
	 * "1980 25" is not addressable too (we do not know the month).
	 * 
	 * @return
	 */
	public boolean isComplete() {
		if (year == null)
			return false;
		if (month == null && yearPart == null && yearsCount == null && (day != null || monthPart != null))
			return false;
		return true;
		/*
		 * Integer[] toCheck = new Integer[3]; toCheck[0] = year; toCheck[1] =
		 * (month==null?null:month.getMonthNumber()); if (toCheck[1] == null && yearPart
		 * != null) { toCheck[1] = new Integer(0); } toCheck[2] = day; if (toCheck[2] ==
		 * null && monthPart != null) { toCheck[2] = new Integer(0); }
		 * 
		 * if (toCheck[0] == null) { return false; }
		 * 
		 * if (toCheck[1] == null && toCheck[2] != null) { return false; }
		 * 
		 * return true;
		 */
	}

	/**
	 * Check if day of month is correct for this year and month
	 * 
	 * @return
	 */
	public boolean isCorrect() {
		if (year == null || month == null || day == null)
			return true;
		LocalDate date = LocalDate.of(year, month.getMonthNumber() + 1, 1);
		if (day > date.lengthOfMonth())
			return false;
		return true;
	}

	@Override
	public String toString() {

		String dayName = null;
		if (day != null) {
			dayName = Integer.toString(day);
		} else if (monthPart != null) {
			dayName = monthPart.getName();
		}

		String monthName = null;
		if (month != null) {
			if (dayName != null)
				monthName = month.getDayName();
			else
				monthName = month.getName();
		} else if (yearPart != null) {
			monthName = yearPart.getName();
		}

		String yearName = null;
		if (year != null) {
			if (year > 0)
				yearName = Integer.toString(year);
			else
				yearName = Integer.toString(-year + 1);
		}

		if (yearsCount == YearsCount.TEN) {
			if (year < 0)
				yearName = Integer.toString(-year - 9);
			if (monthPart != null)
				yearName = yearName + "-х";
			else
				yearName += "-e";
		}
		if (yearsCount == YearsCount.HUNDRED)
			if (monthPart != null) {
				if (year > 0)
					yearName = (year / 100 + 1) + " века";
				else
					yearName = (-year / 100 - 1) + " века";
			} else if (year > 0)
				yearName = (year / 100 + 1) + " век";
			else
				yearName = (-year / 100 - 1) + " век";
		else
			yearName += " г.";
		if (year != null && year < 1)
			yearName += " до н.э.";
		return (dayName != null ? (dayName + " ") : "") + (monthName != null ? (monthName + " ") : "")
				+ (yearName != null ? (yearName) : "");
	}

	public LocalDate getStartDate() {

		if (!isComplete()) {
			return null;
		}
		if (startDate != null)
			return startDate;

		// Calendar calendar = new GregorianCalendar();
		// calendar.clear();
		if (year == null) {
			return null;
		}

		int monthNumber = -1;
		if (month != null) {
			monthNumber = month.getMonthNumber();
		} else if (yearPart != null) {
			switch (yearPart) {
			case BEGINNING:
				monthNumber = Month.JANUARY.getMonthNumber();
				break;
			case END:
				monthNumber = Month.NOVEMBER.getMonthNumber();
				break;
			case MIDDLE:
				monthNumber = Month.MAY.getMonthNumber();
				break;
			case SPRING:
				monthNumber = Month.MARCH.getMonthNumber();
				break;
			case SUMMER:
				monthNumber = Month.JUNE.getMonthNumber();
				break;
			case FALL:
				monthNumber = Month.SEPTEMBER.getMonthNumber();
				break;
			case WINTER:
				monthNumber = Month.JANUARY.getMonthNumber();
				break;
			}
		} else {
			monthNumber = 0;
		}

		int dayNumber = -1;
		if (day != null) {
			dayNumber = day;
		} else if (monthPart != null && (yearsCount == null || yearsCount == YearsCount.ONE)) {
			switch (monthPart) {
			case BEGINNING:
				dayNumber = 1;
				break;
			case MIDDLE:
				dayNumber = 10;
				break;
			case END:
				dayNumber = 20;
				break;
			}
		} else {
			dayNumber = 1;
		}

		if (dayNumber == 0)
			dayNumber = 1;

		int yearNumber = year;
		if (yearsCount != null && monthPart != null) {
			switch (yearsCount) {
			case ONE:
				break;
			case TEN:
				switch (monthPart) {
				case BEGINNING:
					break;
				case MIDDLE:
					yearNumber += 3;
					break;
				case END:
					yearNumber += 7;
					break;
				}
				break;
			case HUNDRED:
				switch (monthPart) {
				case BEGINNING:
					break;
				case MIDDLE:
					yearNumber += 30;
					break;
				case END:
					yearNumber += 70;
					break;
				}
				break;
			}
		}
		startDate = LocalDate.of(yearNumber, monthNumber + 1, dayNumber);
		return startDate;
		/*
		 * if (year<0){ //int year=-this.year; //calendar.set(year, monthNumber,
		 * dayNumber, 0, 0, 0); //calendar.set(GregorianCalendar.ERA,
		 * GregorianCalendar.BC); } else{ calendar.set(year, monthNumber, dayNumber, 0,
		 * 0, 0); } return calendar.getTime();
		 */
	}

	public LocalDate getEndDate() {
		if (!isComplete()) {
			return null;
		}

		if (endDate != null)
			return endDate;

		if (year == null) {
			return null;
		}

		int curYear = year;
		if (yearsCount != null) {
			switch (yearsCount) {
			case ONE:
				break;
			case TEN:
				if (monthPart == null)
					curYear += 10;
				else
					switch (monthPart) {

					case BEGINNING:
						curYear += 3;
						break;
					case MIDDLE:
						curYear += 7;
						break;
					case END:
						curYear += 10;
						break;
					}
				break;

			case HUNDRED:
				if (monthPart == null)
					curYear += 99;
				else
					switch (monthPart) {
					case BEGINNING:
						curYear += 30;
						break;
					case MIDDLE:
						curYear += 70;
						break;
					case END:
						curYear += 99;
						break;
					}
				break;

			}
		}

		int monthNumber = -1;
		if (month != null) {
			monthNumber = month.getMonthNumber();
		} else if (yearPart != null) {
			switch (yearPart) {
			case BEGINNING:
				monthNumber = Month.MARCH.getMonthNumber();
				break;
			case END:
				monthNumber = Month.DECEMBER.getMonthNumber();
				break;
			case MIDDLE:
				monthNumber = Month.AUGUST.getMonthNumber();
				break;
			case SPRING:
				monthNumber = Month.MAY.getMonthNumber();
				break;
			case SUMMER:
				monthNumber = Month.AUGUST.getMonthNumber();
				break;
			case FALL:
				monthNumber = Month.NOVEMBER.getMonthNumber();
				break;
			case WINTER:
				monthNumber = Month.FEBRUARY.getMonthNumber();
				break;
			}
		} else {
			monthNumber = 11;
		}

		int dayNumber = -1;
		if (day != null) {
			dayNumber = day;
		} else if (monthPart != null) {
			switch (monthPart) {
			case BEGINNING:
				dayNumber = 9;
				break;
			case MIDDLE:
				dayNumber = 19;
				break;
			case END:
				dayNumber = -1; // last day of the month
				break;
			}
		} else {
			dayNumber = -1; // last day of the month
		}
		if (dayNumber == 0)
			dayNumber = 1;
		if (dayNumber != -1) {
			endDate = LocalDate.of(curYear, monthNumber + 1, dayNumber);
		} else {
			LocalDate currentMonth = LocalDate.of(curYear, monthNumber + 1, 1);
			endDate = LocalDate.of(curYear, monthNumber + 1, currentMonth.lengthOfMonth());
		}
		return endDate;
	}

	/**
	 * Packs this date into a single long value for affective storage.
	 * 
	 * @return
	 * @throws IOException
	 */
	public long pack() throws IOException {
		return packInt();
	}

	/**
	 * Unpacks this date from a long. All internal values are overwritten from the
	 * data inside that long.
	 * 
	 * @param packed
	 */
	public void unpackFrom(long packed) {
		unpackInt((int) packed);
	}

	public int packInt() {
		// packer.packBoolean(yearsCount != null);

		LongPackager packer = new LongPackager(0);
		if (yearsCount != null) {
			packer.packEnumNullSupported(yearsCount, YearsCount.class);
		} else {
			packer.packEnumNullSupported(YearsCount.ONE, YearsCount.class);
		}
		packer.packBoolean(monthPart != null);
		if (monthPart != null) {
			packer.packEnumNullSupported(monthPart, MonthPart.class);
		} else {
			packer.packIntegerByMaxValue(day, 31);
		}

		packer.packBoolean(yearPart != null);
		if (yearPart != null) {
			packer.packEnumNullSupported(yearPart, YearPart.class);
		} else {
			packer.packEnumNullSupportedExcludeCommon(month, Month.class);
		}
		packer.packIntegerByMaxValue(year, (1 << 15) - 1);

		long value = packer.getValue();
		return (int) value;
	}

	void unpackInt(int packed) {
		LongPackager packer = new LongPackager(packed);
		// if (packer.readBoolean()) {
		yearsCount = packer.readEnumNullSupported(YearsCount.class);
		// } else {
		// yearsCount = packer.readEnumNullSupported(YearsCount.class);
		// yearsCount = YearsCount.ONE;
		// }
		if (packer.readBoolean()) {
			monthPart = packer.readEnumNullSupported(MonthPart.class);
		} else {
			day = packer.readIntegerByMaxValue(31);
		}

		if (packer.readBoolean()) {
			yearPart = packer.readEnumNullSupported(YearPart.class);
		} else {
			month = packer.readEnumNullSupportedExcludeCommon(Month.class);
		}

		year = packer.readIntegerByMaxValue((1 << 15) - 1);
		if (((year >> 15) & 1) != 0)
			year += (~0) - ((1 << 16) - 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((monthPart == null) ? 0 : monthPart.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		result = prime * result + ((yearPart == null) ? 0 : yearPart.hashCode());
		result = prime * result + ((yearsCount == null) ? 0 : yearsCount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FreeFormDate other = (FreeFormDate) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (month != other.month)
			return false;
		if (monthPart != other.monthPart)
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		if (yearPart != other.yearPart)
			return false;
		return true;
	}

	@Override
	public long getStartDateInDays() {
		return getStartDate().toEpochDay();
	}

	@Override
	public long getEndDateInDays() {
		return getEndDate().toEpochDay();
	}

}
