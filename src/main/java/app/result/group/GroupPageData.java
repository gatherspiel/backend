package app.result.group;

import app.groups.data.Event;
import app.groups.data.GameTypeTag;
import app.groups.data.Group;
import app.users.data.PermissionName;
import service.update.EventService;

import java.time.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class GroupPageData {

  private int id;
  private String name;
  private String url;
  private String description;
  private String imagePath;

  private HashMap<PermissionName, Boolean> permissions;
  private TreeSet<Event> oneTimeEventData;
  private TreeSet<Event> weeklyEventData;
  public GameTypeTag[] gameTypeTags;

  class WeeklyEventDataComparator implements Comparator<Event> {
    public int compare(Event eventData1, Event eventData2) {

      int compare = eventData1.getDay().getValue() - eventData2.getDay().getValue();

      if(compare != 0){
        return compare;
      }

      compare = eventData1.getStartTime().compareTo(eventData2.getStartTime());
      if(compare != 0){
        return compare;
      }

      return  eventData1.getName().compareTo(eventData2.getName());
    }
  }

  class OneTimeEventDataComparator implements Comparator<Event> {
    public int compare(Event eventData1, Event eventData2) {
      int compare = eventData1.getStartTime().compareTo(eventData2.getStartTime());
      if(compare == 0){
        compare = eventData1.getStartDate().compareTo(eventData2.getStartDate());
      }
      return compare;
    }
  }


  private GroupPageData(
      int id,
      String name,
      String url,
      String description,
      GameTypeTag[] gameTypeTags,
      String imagePath){
    this.id = id;
    this.name = name;
    this.url = url;
    this.description = description;
    this.permissions = new HashMap<>();
    this.oneTimeEventData = new TreeSet<>(new OneTimeEventDataComparator());
    this.weeklyEventData = new TreeSet<>(new WeeklyEventDataComparator());
    this.gameTypeTags = gameTypeTags;
    this.imagePath = imagePath;
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

  public TreeSet<Event> getOneTimeEventData(){
    return this.oneTimeEventData;
  }

  public void addEvent(Event event)
  {
    if(event.getIsRecurring()){
      weeklyEventData.add(event);
    }else {
      oneTimeEventData.add(event);
    }
  }

  public TreeSet<Event> getWeeklyEventData(){
    return this.weeklyEventData;
  }

  public static GroupPageData createFromSearchResult(Group group) {
    GroupPageData data = new GroupPageData(
        group.getId(),
        group.getName(),
        group.getUrl(),
        group.getDescription(),
        group.getGameTypeTags(),
        group.getImageFilePath());

    if(group.getEvents() != null){
      for(Event event: group.getEvents()) {

        //Event is not ready to be published because it does not have a location.
        if (event.getLocation() == null) {
          continue;
        }
        data.addEvent(event);
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

  public void setGameTypeTags(GameTypeTag[] gameTypeTags){
    this.gameTypeTags = gameTypeTags;
  }

  public GameTypeTag[] getGameTypeTags(){
    return gameTypeTags;
  }

  public String getImagePath(){
    return imagePath;
  }

  public void setImagePath(String imagePath){
    this.imagePath = imagePath;
  }
}
