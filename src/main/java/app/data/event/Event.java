package app.data.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Event {
  private Integer id;
  private String day;
  private String location;
  private String description;
  private String name;
  private String url;

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private EventLocation eventLocation;

  public Event() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDay() {
    if(startTime == null){
      return day;
    }
    return (startTime).getDayOfWeek().toString();
  }

  public void setDay(String day) {
    this.day = day.toLowerCase();
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDescription() {
    return this.description;
  }

  public void getDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl(){
    return url;
  }

  public void setUrl(String url){
    this.url = url;
  }

  public void setStartTime(LocalDateTime startTime){
    this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
  }

  public LocalDateTime getStartTime(){
    return startTime;
  }

  public void setEndTime(LocalDateTime endTime){
    this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  public LocalDateTime getEndTime(){
    return endTime;
  }

  @JsonProperty(required = false)
  public EventLocation getEventLocation(){
    return eventLocation;
  }

  public void setEventLocation(EventLocation eventLocation){
    this.eventLocation = eventLocation;
  }

  public String toString(){
    return "Event data \n id:"+this.id +"\nday:"+this.day+"\n location:"+ this.location +"\n description:"+this.description +
        " \nname:"+this.name+"\n url:"+this.url;
  }
}
