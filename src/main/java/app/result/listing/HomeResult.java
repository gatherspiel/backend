package app.result.listing;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

  //TODO: Update data structure
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
      String groupCity
  ) {
    if (!groupData.containsKey(id)) {
      HomepageGroup group = new HomepageGroup();
      group.setId(id);
      group.setName(name);
      group.setUrl(url);
      group.addCity(groupCity);
      groupData.put(id, group);
    } else {
      groupData.get(id).addCity(groupCity);
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


  public LinkedHashMap<Integer, HomepageGroup> getGroupData() {

    /*Results are sorted here instead of in the database query due to the fact that the database isn't configured
    to correctly sort '-' characters*/
    return groupData.entrySet().stream().sorted(Map.Entry.comparingByValue(new HomepageResultComparator())).collect(
        Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  public String toString(){
    String data = "";

    for(HomepageGroup group: groupData.values()){
      data += group.toString() +"\n";
    }
    return data;
  }

}
