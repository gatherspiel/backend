package app.result.group;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
      String day)
  {
    this.name = name;
    this.description = description;
    this.location = location;
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.dayOfWeek = DayOfWeek.valueOf(day);
  }

  public void setName(){
    this.name = name;
  }

  public String getName(){
    return name;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public String getDescription(){
    return description;
  }

  public void setLocation(String location){
    this.location = location;
  }

  public String getLocation(){
    return location;
  }

  public void setStartTime(LocalTime startTime){
    this.startTime = startTime;
  }

  public LocalTime getStartTime(){
    return startTime;
  }

  public void setEndTime(LocalTime endTime){
    this.endTime = endTime;
  }

  public LocalTime getEndTime(){
    return endTime;
  }

  public void setDayOfWeek(String dayOfWeek){
    this.dayOfWeek = DayOfWeek.valueOf(dayOfWeek);
  }

  public DayOfWeek getDayOfWeek(){
    return dayOfWeek;
  }

  public void setId(String id){
    this.id = id;
  }
  public Integer getId(){
    return id;
  }
}
