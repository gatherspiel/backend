package app.result.group;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;

class WeeklyEventDataComparator implements Comparator<WeeklyEventData> {
  public int compare(WeeklyEventData eventData1, WeeklyEventData eventData2) {

    int compare = eventData1.getDay().getValue() - eventData2.getDay().getValue();
    
    if(compare != 0){
      return compare;
    }

    compare = eventData1.getStartTime().compareTo(eventData2.getStartTime());
    if(compare != 0){
      return compare;
    }

    return  eventData1.getName().compareTo(eventData2.getName());
  }
}

public class WeeklyEventData {
  private String name;
  private String description;
  private String location;
  private LocalTime startTime;
  private LocalTime endTime;
  private DayOfWeek day;
  private int id;


  public void setName(String name){
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
    if(startTime == null){
      return LocalTime.MIN;
    }
    return startTime;
  }

  public void setEndTime(LocalTime endTime){
    this.endTime = endTime;
  }

  public LocalTime getEndTime(){
    if(endTime == null){
      return LocalTime.MAX;
    }
    return endTime;
  }

  public void setDay(String day){
    this.day = DayOfWeek.valueOf(day.toUpperCase());
  }

  public DayOfWeek getDay(){
    return day;
  }

  public void setId(Integer id){
    this.id = id;
  }

  public Integer getId(){
    return id;
  }
}
