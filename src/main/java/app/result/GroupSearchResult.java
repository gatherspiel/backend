package app.result;

import app.data.Event;
import app.data.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GroupSearchResult {
  private HashMap<Integer, Group> groupData;

  @JsonIgnore
  Logger logger;

  public GroupSearchResult() {
    groupData = new HashMap<Integer, Group>();
    logger = LogUtils.getLogger();
  }

  public void addGroup(
    Integer id,
    String name,
    String url,
    String summary,
    String groupCity
  ) {
    if (!groupData.containsKey(id)) {
      Group group = new Group();
      group.setId(id);
      group.setName(name);
      group.setUrl(url);
      group.setSummary(summary);
      group.addCity(groupCity);
      groupData.put(id, group);
    }
  }

  public void addEvent(
    Integer groupId,
    Integer eventId,
    String name,
    String description,
    String dayOfWeek,
    String address,
    String city
  ) {
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
    event.setSummary(description);
    event.setDay(dayOfWeek);
    event.setLocation(address);
    event.setId(eventId);
    group.addEvent(event);
  }

  public Group getFirstGroup() {
    if(groupData.isEmpty()){
      return null;
    }
    return groupData.get(groupData.keySet().toArray()[0]);
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

  public HashMap<Integer, Group> getGroupData() {
    return groupData;
  }


}
