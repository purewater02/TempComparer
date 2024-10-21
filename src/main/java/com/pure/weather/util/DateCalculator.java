package com.pure.weather.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateCalculator {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  public static String[] getLastYearWeekRange(LocalDate today) {
    LocalDate lastYearSameDate = today.minusYears(1);
    LocalDate startOfWeek =
        lastYearSameDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate lastOfWeek = lastYearSameDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

    return new String[] {startOfWeek.format(formatter), lastOfWeek.format(formatter)};
  }

  public static String[] getLastYearMonthRange(LocalDate today) {
    LocalDate lastYearSameDate = today.minusYears(1);
    LocalDate startOfMonth = lastYearSameDate.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate lastOfMonth = lastYearSameDate.with(TemporalAdjusters.lastDayOfMonth());

    return new String[] {startOfMonth.format(formatter), lastOfMonth.format(formatter)};
  }

  public static LocalDate convertToLocalDate(String date) {
    return LocalDate.parse(date, formatter);
  }

  public static String convertToString(LocalDate date) {
    return date.format(formatter);
  }
}
