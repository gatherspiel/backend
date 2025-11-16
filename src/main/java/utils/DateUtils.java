package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class DateUtils
{

  public static LocalDate getNextOccurrenceFromTime(DayOfWeek occurrenceDay, LocalTime occurrenceTime, LocalDateTime fromTime){

    if(!occurrenceDay.equals(fromTime.getDayOfWeek()) ||
        fromTime.toLocalTime().isAfter(occurrenceTime)){

      fromTime = fromTime.with(TemporalAdjusters.next(occurrenceDay));
    }
    return fromTime.toLocalDate();
  }

  public static LocalDate getNextOccurrence(DayOfWeek dayOfWeek, LocalTime localTime){
    return getNextOccurrenceFromTime(dayOfWeek, localTime, LocalDateTime.now());
  }
}
