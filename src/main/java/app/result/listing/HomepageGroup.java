package app.result.listing;


import app.groups.GameTypeTag;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.TreeSet;

public class HomepageGroup {

  public int id;
  public String url;
  public String[] cities;
  public String name;

  public TreeSet<DayOfWeek> recurringEventDays = new TreeSet<>();

  public GameTypeTag[] gameTypeTags;

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
    if(cities == null){
      return new String[0];
    }
    return cities;
  }

  public void setRecurringEventDays(TreeSet<DayOfWeek> recurringEventDays){
    this.recurringEventDays = recurringEventDays;
  }

  public TreeSet<DayOfWeek>  getRecurringEventDays(){
    return recurringEventDays;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String toString() {
    return (
            "id:" +
            id +
            "\n" +
            "url:" +
            url +
            "\n" +
            "cities:" +
            cities +
            "\n" +
            "name:" +
            name +
            "\n"
    );
  }

  public void addCity(String city) {

    if(city == null){
      return;
    }

    if (cities == null) {
      cities = new String[] { city };
      return;
    }

    //TODO: Consider using TreeSet to improve efficiency.
    for(String existing: cities){
      if(existing.equals(city)){
        return;
      }
    }

    String[] updated = new String[cities.length + 1];
    System.arraycopy(cities, 0, updated, 0, cities.length);
    updated[cities.length] = city;
    cities = updated;

    Arrays.sort(cities);
  }

  public void setGameTypeTags(GameTypeTag[] gameTypeTags){
    this.gameTypeTags = gameTypeTags;
  }

  public GameTypeTag[] getGameTypeTags(){
    return gameTypeTags;
  }

}
