package app.data.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
  private Integer id;
  private String day;
  private String location;
  private String summary;
  private String name;
  private String url;

  private String startTime;
  private String endTime;

  private EventLocation eventLocation;

  public Event() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDay() {
    return day.toLowerCase();
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

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
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

  public void setStartTime(String startTime){
    this.startTime = startTime;
  }

  public String getStartTime(){
    return startTime;
  }

  public void setEndTime(String endTime){
    this.endTime = endTime;
  }

  public String getEndTime(){
    return endTime;
  }

  @JsonProperty(required = false)
  public EventLocation getEventLocation(){
    return eventLocation;
  }

  public void setEventLocation(EventLocation eventLocation){
    this.eventLocation = eventLocation;
  }
}
