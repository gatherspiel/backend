package app.result.group;

import app.groups.data.Event;
import com.fasterxml.jackson.annotation.JsonGetter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

class OneTimeEventDataComparator implements Comparator<OneTimeEventData> {
  public int compare(OneTimeEventData eventData1, OneTimeEventData eventData2) {
    return eventData1.getStartTime().compareTo(eventData2.getStartTime());
  }
}

// Event that will be shown on a group page.
public class OneTimeEventData {

  private LocalDate eventDate;

  private String name;
  private String description;
  private String location;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private int id;

  public OneTimeEventData(
      String name,
      String description,
      String location,
      int id,
      LocalDateTime startTime,
      LocalDateTime endTime)
  {
    this.name = name;
    this.description = description;
    this.location = location;
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public LocalDate getEventDate(){
    return eventDate;
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getDescription(){
    return description;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public String getLocation(){
    return location;
  }

  public void setLocation(String location){
    this.location = location;
  }

  public void setId(int id){
    this.id = id;
  }

  public int getId(){
    return id;
  }

  /*
  The start and end time are represented as strings as a workaround for a serialization limitation with the
  LocalDateTime object
   */
  @JsonGetter("startTime")
  public String getSerializedStartTime(){
    return startTime.toString();
  }

  @JsonGetter("endTime")
  public String getSerializedEndTime(){
    return endTime.toString();
  }

  public LocalDateTime getStartTime(){
    return startTime;
  }

  public LocalDateTime getEndTime(){
    return endTime;
  }

  @Override
  public boolean equals(Object event){
    var eventB = (Event)event;

    return eventB.getId() == this.id;
  }

  @Override
  public int hashCode(){
    return Objects.hash(
        name,
        description,
        location,
        startTime,
        endTime,
        id);
  }
}
