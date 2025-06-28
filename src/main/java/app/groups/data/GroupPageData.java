package app.groups.data;

import app.users.data.PermissionName;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;


public class GroupPageData {

  private static final int TIME_RANGE_DAYS = 30;
  private int id;
  private String name;
  private String url;
  private String description;
  private HashMap<String, Boolean> permissions;
  private TreeSet<GroupPageEventData> groupPageEventData;

  private GroupPageData(int id, String name, String url, String description){
    this.id = id;
    this.name = name;
    this.url = url;
    this.description = description;
    this.permissions = new HashMap<>();
    this.groupPageEventData = new TreeSet<GroupPageEventData>(new GroupPageEventDataComparator());
  }

  public int getId(){
    return id;
  }

  public void setId(int id){
    this.id = id;
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getUrl(){
    return url;
  }

  public void setUrl(String url){
    this.url = url;
  }

  public String getDescription(){
    return description;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public Set<GroupPageEventData> getEventData(){
    return this.groupPageEventData;
  }

  public void addEventData(
      String name,
      String description,
      String link, int id,
      LocalDateTime startTime,
      LocalDateTime endTime)
  {
    GroupPageEventData eventData = new GroupPageEventData(name, description, link, id, startTime, endTime);

    groupPageEventData.add(eventData);
  }

  public static GroupPageData createFromSearchResult(Group group) {
    GroupPageData data = new GroupPageData(group.getId(), group.getName(), group.getUrl(), group.getDescription());

    LocalDateTime currentDate = LocalDateTime.now();

    if(group.getEvents() != null){
      for(Event event: group.getEvents()) {

        if(event.getIsRecurring()) { //Event is recurring

          //TODO: Update logic for recurring events
          String day = event.getDay();
          LocalDateTime nextEventDate = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(day.toUpperCase())));

          while (nextEventDate.minusDays(TIME_RANGE_DAYS + 1).isBefore(currentDate)) {
            data.addEventData(event.getName(), event.getDescription(), event.getLocation(), event.getId(), nextEventDate, nextEventDate);
            nextEventDate = nextEventDate.plusDays(7);
          }
        }
        else if(event.getStartTime() != null && event.getEndTime() != null) {
          var startTime = event.getStartTime();
          var endTime = event.getEndTime();
          data.addEventData(event.getName(), event.getDescription(), event.getLocation(), event.getId(),startTime, endTime);
          System.out.println("Adding");
        }
      }
    }

    return data;
  }


  public HashMap<String,Boolean> getPermissions(){
    return permissions;
  }

  public void setPermissions(HashMap<String, Boolean> permissions){
    this.permissions = permissions;
  }

  public void enablePermission(String permissionName, boolean isEnabled){
    this.permissions.put(permissionName, isEnabled);
  }

  public boolean userCanEdit(){
    if(permissions == null) {
      return false;
    }
    return permissions.getOrDefault(PermissionName.USER_CAN_EDIT.toString(), false);
  }
}
