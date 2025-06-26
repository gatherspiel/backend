package app.data;

public class Event {
  private Integer id;
  private String day;
  private String location;
  private String summary;
  private String name;
  private String url;

  private String eventDate;
  private String eventTime;

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


  public void setEventDate(String eventDate){
    this.eventDate = eventDate;
  }

  public String getEventDate(){
    return eventDate;
  }

  public String getEventTime(){
    return eventDate;
  }
}
