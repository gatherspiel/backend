package app.result.listing;

import app.groups.GameTypeTag;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;


import org.apache.logging.log4j.Logger;
import utils.LogUtils;

class HomepageResultComparator implements Comparator<HomepageGroup> {
  public int compare(HomepageGroup group1, HomepageGroup group2){
    return group1.getName().compareTo(group2.getName());
  }
}

public class HomeResult {

  @JsonIgnore
  private LinkedHashMap<Integer, HomepageGroup> groupData;

  @JsonIgnore
  Logger logger;

  public HomeResult() {
    groupData = new LinkedHashMap<Integer, HomepageGroup>();
    logger = LogUtils.getLogger();
  }

  public void addGroup(
      Integer id,
      String name,
      String url,
      String[] cities,
      TreeSet<DayOfWeek> days,
      GameTypeTag[] gameTypeTags
  ){
    HomepageGroup group = new HomepageGroup();
    group.setId(id);
    group.setName(name);
    group.setUrl(url);
    group.setCities(cities);
    group.setRecurringEventDays(days);
    group.setGameTypeTags(gameTypeTags);
    groupData.put(id, group);
  }

  public void addGroup(
      Integer id,
      String name,
      String url,
      String groupCity,
      DayOfWeek eventDay,
      GameTypeTag[] gameTypeTags
  ) {

    if (!groupData.containsKey(id)) {
      HomepageGroup group = new HomepageGroup();
      group.setId(id);
      group.setName(name);
      group.setUrl(url);
      group.addCity(groupCity);
      group.setGameTypeTags(gameTypeTags);
      if(eventDay !=null){
        TreeSet<DayOfWeek> days = new TreeSet<DayOfWeek>();
        days.add(eventDay);
        group.setRecurringEventDays(days);
      }

      groupData.put(id, group);
    } else {
      groupData.get(id).addCity(groupCity);
      if(eventDay != null){
        groupData.get(id).getRecurringEventDays().add(eventDay);
      }
    }
  }

  public void addGroupData(HomepageGroup group){
    groupData.put(group.getId(), group);
  }

  public HomepageGroup getFirstGroup() {
    if(groupData.isEmpty()){
      return null;
    }
    return groupData.get(groupData.keySet().toArray()[0]);
  }

  public int countGroups() {
    return groupData.size();
  }


  @JsonIgnore
  public LinkedHashMap<Integer, HomepageGroup> getGroupDataMap() {

    /*Results are sorted here instead of in the database query due to the fact that the database isn't configured
    to correctly sort '-' characters*/
    return groupData.entrySet().stream().sorted(Map.Entry.comparingByValue(new HomepageResultComparator())).collect(
        Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  @JsonGetter
  public TreeSet<HomepageGroup> getGroupData(){
    TreeSet<HomepageGroup> data = new TreeSet<HomepageGroup>(new HomepageResultComparator());
    data.addAll(groupData.values());
    return data;
  }

  public String toString(){
    String data = "";

    for(HomepageGroup group: groupData.values()){
      data += group.toString() +"\n";
    }
    return data;
  }

}
