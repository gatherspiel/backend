package app.groups.data;

import app.result.group.WeeklyEventData;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {

  public WeeklyEventData[] weeklyEventData;
  public Event[] oneTimeEvents;
  public int id;
  public String url;
  public String[] cities;

  public String description;
  public String name;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setCities(String[] cities) {
    this.cities = cities;
  }

  public String[] getCities() {
    return cities;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription(){
    return description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOneTimeEvents(Event[] oneTimeEvents) {
    this.oneTimeEvents = oneTimeEvents;
  }

  public Event[] getOneTimeEvents() {

    if(oneTimeEvents == null || oneTimeEvents.length == 0){
      return new Event[0];
    }
    return oneTimeEvents;
  }

  public void setWeeklyEventData(WeeklyEventData[] weeklyEventData) {
    this.weeklyEventData = weeklyEventData;
  }

  public WeeklyEventData[] getWeeklyEventData() {

    if(oneTimeEvents == null || oneTimeEvents.length == 0){
      return new WeeklyEventData[0];
    }
    return weeklyEventData;
  }

  public String toString() {
    return (
      "Events:" +
          oneTimeEvents +
      "\n" +
      "id:" +
      id +
      "\n" +
      "url:" +
      url +
      "\n" +
      "cities:" +
      cities +
      "\n" +
      "description:" +
          description +
      "\n" +
      "name:" +
      name +
      "\n"
    );
  }

  public int countEvents() {
    if (oneTimeEvents == null) {
      return 0;
    }
    return oneTimeEvents.length;
  }

  public void addOneTimeEvent(Event event) {
    if (oneTimeEvents == null) {
      oneTimeEvents = new Event[] { event };
      return;
    }
    Event[] updated = new Event[oneTimeEvents.length + 1];
    System.arraycopy(oneTimeEvents, 0, updated, 0, oneTimeEvents.length);
    updated[oneTimeEvents.length] = event;
    oneTimeEvents = updated;
  }

  public void addWeeklyEvent(WeeklyEventData event) {
    if (weeklyEventData == null) {
      weeklyEventData = new WeeklyEventData[] { event };
      return;
    }
    WeeklyEventData[] updated = new WeeklyEventData[weeklyEventData.length + 1];
    System.arraycopy(weeklyEventData, 0, updated, 0, weeklyEventData.length);
    updated[weeklyEventData.length] = event;
    weeklyEventData = updated;
  }

  public void addCity(String city) {

    if(city == null){
      return;
    }

    if (cities == null) {
      cities = new String[] { city };
      return;
    }

    //TODO: Consider using set to improve efficiency.
    for(String existing: cities){
      if(existing.equals(city)){
        return;
      }
    }

    String[] updated = new String[cities.length + 1];
    System.arraycopy(cities, 0, updated, 0, cities.length);
    updated[cities.length] = city;
    cities = updated;
  }
}
