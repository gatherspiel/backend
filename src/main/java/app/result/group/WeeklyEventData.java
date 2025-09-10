package app.result.group;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;

class WeeklyEventDataComparator implements Comparator<WeeklyEventData> {
  public int compare(WeeklyEventData eventData1, WeeklyEventData eventData2) {
    return eventData1.getStartTime().compareTo(eventData2.getStartTime());
  }
}

public class WeeklyEventData {
  private String name;
  private String description;
  private String location;
  private LocalTime startTime;
  private LocalTime endTime;
  private DayOfWeek dayOfWeek;
  private int id;

  public WeeklyEventData(
      String name,
      String description,
      String location,
      int id,
      LocalTime startTime,
      LocalTime endTime,
      DayOfWeek dayOfWeek)
  {
    this.name = name;
    this.description = description;
    this.location = location;
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.dayOfWeek = dayOfWeek;
  }

  public String getName(){
    return name;
  }

  public String getDescription(){
    return description;
  }

  public String getLocation(){
    return location;
  }

  public LocalTime getStartTime(){
    return startTime;
  }

  public LocalTime getEndTime(){
    return endTime;
  }

  public DayOfWeek getDayOfWeek(){
    return dayOfWeek;
  }

  public Integer getId(){
    return id;
  }
}
