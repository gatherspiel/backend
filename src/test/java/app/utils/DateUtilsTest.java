package app.utils;

import org.junit.jupiter.api.Test;
import utils.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateUtilsTest {

  @Test
  public void testOneHourAfterMidnightOnSundayMorning_OccurrencesInPrevious24HoursAreInFuture(){

    LocalDateTime current = LocalDateTime.now();

    LocalDateTime fromTime = current.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));

    for(int i=1;i<=24;i++){
      LocalDateTime localDateTime = fromTime.minusHours(i);

      DayOfWeek occurrenceDay = localDateTime.getDayOfWeek();
      LocalTime occurrenceTime = localDateTime.toLocalTime();

      LocalDate nextOccurrenceDate = DateUtils.getNextOccurrenceFromTime(occurrenceDay, occurrenceTime,fromTime);

      assertTrue(
          nextOccurrenceDate.isAfter(fromTime.toLocalDate()),
          fromTime.toLocalDate().toString() +" "+nextOccurrenceDate.toString());
    }
  }
}
