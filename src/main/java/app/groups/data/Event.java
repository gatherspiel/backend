package app.groups.data;

import app.users.data.PermissionName;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class Event {
  private Integer id;
  private String day;
  private String description;
  private String name;
  private String url;
  private Boolean isRecurring = false;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private EventLocation eventLocation;

  private String groupName;
  private Integer groupId;

  private HashMap<PermissionName, Boolean> permissions;

  public Event() {
    permissions = new HashMap<>();
  }

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
    if(day != null){
      this.day = day.toLowerCase();
    }
  }

  public String getLocation() {
    return eventLocation.toString();
  }

  public void setLocation(String location) throws Exception {

    if(location == null || location.isEmpty()){
      this.eventLocation = new EventLocation();
      return;
    }

    EventLocation eventLocation = new EventLocation();
    this.eventLocation = eventLocation;
    String[] locationSplit = location.split(",");

    if(locationSplit.length == 3) {
      String[] locationSplit2 = locationSplit[2].trim().split(" ");
      if(locationSplit2.length == 2) {
        eventLocation.setStreetAddress(locationSplit[0].trim());
        eventLocation.setCity(locationSplit[1].trim());
        eventLocation.setState(locationSplit2[0].trim());
        eventLocation.setZipCode(Integer.parseInt(locationSplit2[1].trim()));
        return;
      }
    }
    eventLocation.setStreetAddress(location);
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
    if(startTime != null){
      this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
    } else {
      this.startTime = startTime;
    }
  }

  public LocalDateTime getStartTime(){
    return startTime;
  }

  public void setEndTime(LocalDateTime endTime){
    if(endTime != null){
      this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
    } else {
      this.endTime = endTime;
    }
  }

  public LocalDateTime getEndTime(){
    return endTime;
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

  @JsonProperty(required = false)
  public EventLocation getEventLocation(){
    return eventLocation;
  }

  public void setEventLocation(EventLocation eventLocation){
    this.eventLocation = eventLocation;
  }

  public boolean getIsRecurring(){
    return isRecurring;
  }

  public void setGroupName(String groupName){
    this.groupName = groupName;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupId(Integer groupId){
    this.groupId = groupId;
  }

  public Integer getGroupId(){
    return groupId;
  }

  public void setIsRecurring(boolean isRecurring){
    this.isRecurring = isRecurring;
  }

  public String toString(){
    return "Event data \n id:"+this.id +"\nday:"+this.day+"\n location:"+ this.eventLocation.toString() +"\n description:"+this.description +
        " \nname:"+this.name+"\n url:"+this.url + "\n startTime:"+this.startTime + "\n endTime:" + this.endTime;
  }

  public void setPermissions(HashMap<PermissionName, Boolean> permissions){
    this.permissions = permissions;
  }

  public HashMap<PermissionName, Boolean> getPermissions(){
    return permissions;
  }

  public void setUserCanEditPermission(Boolean canEdit){
    permissions.put(PermissionName.USER_CAN_EDIT, canEdit);
  }
}
