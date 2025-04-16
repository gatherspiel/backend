package app.data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class Convention {
  String[] days;
  Integer id;
  String url;
  String name;

  public Convention() {}

  public String[] getDays() {
    return days;
  }

  public void setDays(String[] days) {
    this.days = days;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String link) {
    this.url = link;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addDays(int days){
    String oldLastDay = this.days[this.days.length - 1];

    LocalDate date = LocalDate.parse(oldLastDay);
    String[] updates = new String[days];
    for(int i = 0; i<days; i++){
      updates[i] = date.plusDays(i+1).toString();
    }

    String[] updated = new String[this.days.length+days];
    int i = 0;
    while(i<this.days.length){
      updated[i] = this.days[i];
      i++;
    }
    for(int j=0;j<updates.length; j++){
      updated[i+j] = updates[j];
    }
    this.days = updated;
  }

}
