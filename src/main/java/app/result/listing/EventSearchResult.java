package app.result.listing;


import java.util.ArrayList;

public class EventSearchResult {

  private ArrayList<EventSearchResultItem> eventData = new ArrayList<>();;

  public ArrayList<EventSearchResultItem> getEventData(){
    return eventData;
  }

  public void setEventData(ArrayList<EventSearchResultItem> data){
    this.eventData = eventData;
  }

}
