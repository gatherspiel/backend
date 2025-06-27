package app.groups.data;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.time.LocalDate;
import java.util.Comparator;

class GroupPageEventDataComparator implements Comparator<GroupPageEventData> {
  public int compare(GroupPageEventData eventData1, GroupPageEventData eventData2) {
    return eventData1.getEventDate().compareTo(eventData2.getEventDate());
  }
}

// Event that will be shown on a group page.
public class GroupPageEventData {


  private LocalDate eventDate;

  private String name;
  private String description;
  private String location;
  private int id;

  public GroupPageEventData(LocalDate date, String name, String description, String location, int id) {
    eventDate = date;
    this.name = name;
    this.description = description;
    this.location = location;
    this.id = id;
  }

  @JsonGetter("eventDate")
  public String getSerializedDate(){
    return eventDate.toString();
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
}
