package app.result.listing;

import app.groups.EventLocation;
import app.groups.GameTypeTag;

import java.time.LocalDateTime;

public class EventSearchResultItem {

  private EventLocation eventLocation;
  private LocalDateTime nextEventTime;

  private String eventName;

  //Use the group tags for now.
  private GameTypeTag[] gameTypeTags;

  public void setEventLocation(EventLocation eventLocation){
    this.eventLocation = eventLocation;
  }

  public EventLocation getEventLocation(){
    return eventLocation;
  }

  public void setNextEventTime(LocalDateTime nextEventTime){
    this.nextEventTime = nextEventTime;
  }

  public void setEventName(String eventName){
    this.eventName = eventName;
  }

  public String getEventName(){
    return this.eventName;
  }

}
