package com.onpositive.parsers.dates;

import java.time.LocalDate;

public interface IDatePeriod {

	LocalDate getStartDate();

	LocalDate getEndDate();
	long getStartDateInDays();
	long getEndDateInDays();
}
