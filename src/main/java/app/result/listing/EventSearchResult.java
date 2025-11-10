package app.result.listing;


import java.util.ArrayList;
import java.util.List;

public class EventSearchResult {

  private List<EventSearchResultItem> eventData = new ArrayList<>();;

  public List<EventSearchResultItem> getEventData(){
    return eventData;
  }

  public void setEventData(List<EventSearchResultItem> eventData){
    this.eventData = eventData;
  }

}
