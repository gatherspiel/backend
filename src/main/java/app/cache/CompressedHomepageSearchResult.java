package app.cache;

import app.result.listing.HomepageGroup;
import app.result.listing.HomeResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.util.LinkedHashMap;

public class CompressedHomepageSearchResult {

  //TODO: Update data structure
  private LinkedHashMap<Integer, CompressedGroup> groupData;

  @JsonIgnore
  Logger logger;

  public CompressedHomepageSearchResult() {
    groupData = new LinkedHashMap<Integer, CompressedGroup>();
    logger = LogUtils.getLogger();
  }

  public void addGroups(
      HomeResult result
  ) {

    for(HomepageGroup group: result.getGroupDataMap().values()) {

      CompressedGroup compressedGroup = new CompressedGroup();
      compressedGroup.setA(group.getId());
      compressedGroup.setB(group.getName());
      compressedGroup.setC(group.getCities());
      compressedGroup.setD(group.getUrl());
      groupData.put(group.getId(), compressedGroup);
    }
  }

  public LinkedHashMap<Integer, CompressedGroup> getGroupData(){
    return groupData;
  }

  @JsonIgnore
  public HomeResult getHomepageSearchResult(){
    HomeResult homepageSearchResult = new HomeResult();
    for(CompressedGroup compressedGroup: groupData.values()){

      var locationName = "";
      if(compressedGroup.getC().length != 0){
        locationName = compressedGroup.getC()[0];
      }
      homepageSearchResult.addGroup(compressedGroup.getA(), compressedGroup.getB(), compressedGroup.getD(), locationName);
    }
    return homepageSearchResult;
  }
}