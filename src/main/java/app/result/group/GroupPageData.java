package app.result.group;

import app.groups.data.Event;
import app.groups.data.Group;
import app.users.data.PermissionName;

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
  private HashMap<PermissionName, Boolean> permissions;
  private TreeSet<OneTimeEventData> oneTimeEventData;
  private TreeSet<WeeklyEventData> weeklyEventData;

  private GroupPageData(int id, String name, String url, String description){
    this.id = id;
    this.name = name;
    this.url = url;
    this.description = description;
    this.permissions = new HashMap<>();
    this.oneTimeEventData = new TreeSet<OneTimeEventData>(new OneTimeEventDataComparator());
    this.weeklyEventData = new TreeSet<WeeklyEventData>(new WeeklyEventDataComparator());
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

  public Set<OneTimeEventData> getOneTimeEventData(){
    return this.oneTimeEventData;
  }

  public void addOneTimeEventData(
      String name,
      String description,
      String link, int id,
      LocalDateTime startTime,
      LocalDateTime endTime)
  {
    OneTimeEventData eventData = new OneTimeEventData(name, description, link, id, startTime, endTime);
    oneTimeEventData.add(eventData);
  }

  public Set<WeeklyEventData> getWeeklyEventData(){
    return this.weeklyEventData;
  }

  public void addWeeklyEventData(
      String name,
      String description,
      String link, int id,
      LocalTime startTime,
      LocalTime endTime,
      DayOfWeek dayOfWeek)
  {
    WeeklyEventData eventData = new WeeklyEventData(name, description, link, id, startTime, endTime, dayOfWeek);
    weeklyEventData.add(eventData);
  }

  public static GroupPageData createFromSearchResult(Group group) {
    GroupPageData data = new GroupPageData(group.getId(), group.getName(), group.getUrl(), group.getDescription());

    LocalDateTime currentDate = LocalDateTime.now();

    if(group.getEvents() != null){
      for(Event event: group.getEvents()) {

        //Event is not ready to be published because it does not have a location
        if(event.getLocation() == null){
          continue;
        }

        WeeklyEventData recurringEvent = event.getWeeklyEventData();
        if(recurringEvent != null) { //Event is recurring
            var startTime = event.getStartTime();
            var endTime = event.getEndTime();
            data.addWeeklyEventData(event.getName(), event.getDescription(), event.getLocation(), event.getId(),startTime, endTime);
        }
        else if(event.getStartTime() != null && event.getEndTime() != null) {
          var startTime = event.getStartTime();
          var endTime = event.getEndTime();
          data.addOneTimeEventData(event.getName(), event.getDescription(), event.getLocation(), event.getId(),startTime, endTime);
        }
      }
    }
    return data;
  }


  public HashMap<PermissionName,Boolean> getPermissions(){
    return permissions;
  }

  public void setPermissions(HashMap<PermissionName, Boolean> permissions){
    this.permissions = permissions;
  }

  public void enablePermission(PermissionName permissionName, boolean isEnabled){
    this.permissions.put(permissionName, isEnabled);
  }

  public boolean userCanEdit(){
    if(permissions == null) {
      return false;
    }
    return permissions.getOrDefault(PermissionName.USER_CAN_EDIT, false);
  }
}
