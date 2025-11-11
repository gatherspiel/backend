package app.result.listing;

import java.util.ArrayList;
import java.util.List;

public class EventSearchResult {

  private List<EventSearchResultItem> eventData = new ArrayList<>();;

  public EventSearchResult(List<EventSearchResultItem> data){
    eventData = data;
  }

  public List<EventSearchResultItem> getEventData(){
    return eventData;
  }

}
