package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class DateUtils
{
  public static LocalDate getNextOccurrence(DayOfWeek dayOfWeek, LocalTime localTime){

    LocalDateTime nextEventOccurrence = LocalDateTime.now();
    if(!dayOfWeek.equals(nextEventOccurrence.getDayOfWeek()) ||
      nextEventOccurrence.toLocalTime().isAfter(localTime)){
      nextEventOccurrence = nextEventOccurrence.with(TemporalAdjusters.next(dayOfWeek));
    }
    return nextEventOccurrence.toLocalDate();
  }
}
