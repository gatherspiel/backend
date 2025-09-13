package app.result.listing;

import app.groups.data.Event;
import app.groups.data.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


import org.apache.logging.log4j.Logger;
import utils.LogUtils;

class GroupResultComparator implements Comparator<Group> {
  public int compare(Group group1, Group group2){
    return group1.getName().compareTo(group2.getName());
  }
}

public class GroupSearchResult {

  //TODO: Update data structure
  private LinkedHashMap<Integer, Group> groupData;

  @JsonIgnore
  Logger logger;

  public GroupSearchResult() {
    groupData = new LinkedHashMap<Integer, Group>();
    logger = LogUtils.getLogger();
  }

  public void addGroup(
    Integer id,
    String name,
    String url,
    String description,
    String groupCity
  ) {
    if (!groupData.containsKey(id)) {
      Group group = new Group();
      group.setId(id);
      group.setName(name);
      group.setUrl(url);
      group.setDescription(description);
      group.addCity(groupCity);
      groupData.put(id, group);
    }
  }

  public void addWeeklyEvent(
      Integer groupId,
      Integer eventId,
      String name,
      String description,
      String dayOfWeek,
      String address,
      LocalTime startTime,
      LocalTime endTime
  ) throws Exception{
    if (!groupData.containsKey(groupId)) {
      logger.warn(
          "Group with id {} does not exist. Event will not be added to group search result",
          groupId
      );
      return;
    }

    Group group = groupData.get(groupId);

    Event event = new Event();
    event.setName(name);
    event.setDescription(description);
    event.setLocation(address);
    event.setId(eventId);
    event.setStartTime(startTime);
    event.setEndTime(endTime);
    event.setDay(dayOfWeek);
    event.setIsRecurring(true);
    group.addEvent(event);
  }

  public void addOneTimeEvent(
    Integer groupId,
    Integer eventId,
    String name,
    String description,
    String dayOfWeek,
    String address,
    String city,
    LocalDateTime startTime,
    LocalDateTime endTime
  ) throws Exception{
    if (!groupData.containsKey(groupId)) {
      logger.warn(
        "Group with id {} does not exist. Event will not be added to group search result",
        groupId
      );
      return;
    }

    Group group = groupData.get(groupId);
    group.addCity(city);
    Event event = new Event();
    event.setName(name);
    event.setDescription(description);
    event.setDay(dayOfWeek);
    event.setLocation(address);
    event.setId(eventId);
    event.setStartTime(startTime.toLocalTime());
    event.setStartDate(startTime.toLocalDate());
    event.setEndTime(endTime.toLocalTime());
    event.setEndDate(endTime.toLocalDate());
    group.addEvent(event);
  }

  public Optional<Group> getFirstGroup() {
    if(groupData.isEmpty()){
      return Optional.empty();
    }
    return Optional.of(groupData.get(groupData.keySet().toArray()[0]));
  }
  public int countGroups() {
    return groupData.size();
  }

  public int countEvents() {
    int events = 0;
    for (Group group : groupData.values()) {
      events += group.countEvents();
    }
    return events;
  }

  public LinkedHashMap<Integer, Group> getGroupData() {

    /*Results are sorted here instead of in the database query due to the fact that the database isn't configured
    to correctly sort '-' characters*/
    return groupData.entrySet().stream().sorted(Map.Entry.comparingByValue(new GroupResultComparator())).collect(
        Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  public void setCanEdit(Group group, boolean canEdit){
    //TODO: Add logic.
  }

}
