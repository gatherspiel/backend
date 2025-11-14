package app.result.listing;

import app.groups.EventLocation;
import app.groups.GameTypeTag;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class EventSearchResultItem {

  private EventLocation eventLocation;
  private LocalTime nextEventTime;
  private LocalDate nextEventDate;

  private DayOfWeek dayOfWeek;

  private String groupName;
  private String eventName;

  private Integer eventId;
  private Integer groupId;

  //Use the group tags for now.
  private GameTypeTag[] gameTypeTags;

  private Double distance = 0.0;

  public void setEventLocation(EventLocation eventLocation){
    this.eventLocation = eventLocation;
  }

  public EventLocation getEventLocation(){
    return eventLocation;
  }

  public LocalTime getNextEventTime(){
    return nextEventTime;
  }

  public void setNextEventTime(LocalTime nextEventTime){
    this.nextEventTime = nextEventTime;
  }

  public LocalDate getNextEventDate(){
    return nextEventDate;
  }

  public void setNextEventDate(LocalDate nextEventDate){
    this.nextEventDate = nextEventDate;
  }

  @JsonGetter("dayOfWeek")
  public String getSerializedDayOfWeek(){
    if(dayOfWeek == null){
      return "";
    }
    return dayOfWeek.getDisplayName(TextStyle.FULL,
      Locale.getDefault());
  }

  public DayOfWeek getDayOfWeek(){
    return dayOfWeek;
  }

  public void setDayOfWeek(DayOfWeek dayOfWeek){
    this.dayOfWeek = dayOfWeek;
  }

  public String getEventName(){
    return this.eventName;
  }

  public void setEventName(String eventName){
    this.eventName = eventName;
  }

  public String getGroupName(){
    return groupName;
  }

  public void setGroupName(String groupName){
    this.groupName = groupName;
  }

  public GameTypeTag[] getGameTypeTags(){
    return gameTypeTags;
  }

  public void setGameTypeTags(GameTypeTag[] gameTypeTags){
    this.gameTypeTags = gameTypeTags;
  }

  @JsonIgnore
  public void setDistance(Double distance){
    this.distance = distance;
  }

  @JsonIgnore
  public Double getDistance(){
    return distance;
  }

  public int getGroupId(){
    return groupId;
  }

  public void setGroupId(int groupId){
    this.groupId = groupId;
  }

  public int getEventId(){
    return eventId;
  }

  public void setEventId(int eventId){
    this.eventId = eventId;
  }

}
