package service.read;

import app.groups.Group;
import app.result.listing.HomepageGroup;
import app.result.listing.GroupSearchResult;
import app.result.listing.HomeResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import org.apache.logging.log4j.Logger;
import app.result.error.SearchParameterException;
import utils.LogUtils;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Optional;

public class SearchService {

  private final SearchRepository searchRepository;
  private final Logger logger;
  public SearchService(Connection conn){
    this.searchRepository = new SearchRepository(conn);
    this.logger = LogUtils.getLogger();
  }

  public HomeResult getGroupsForHomepage(
    LinkedHashMap<String, String> searchParams
  ) throws Exception
  {
    String searchCity = searchParams.get(GroupSearchParams.CITY);
    String distance = searchParams.get(GroupSearchParams.DISTANCE);

    if(distance != null){

      var updatedParams = new LinkedHashMap<>(searchParams);
      updatedParams.remove(GroupSearchParams.CITY);

      GroupSearchParams params = new GroupSearchParams(updatedParams);
      HomeResult groups = searchRepository.getGroupsForHomepage(params);

      if(searchCity == null){
        throw new SearchParameterException("City not specified for distance filter");
      }
      return filterResultsByDistance(
        groups,
        searchParams.get(GroupSearchParams.CITY),
        Integer.parseInt(searchParams.get(GroupSearchParams.DISTANCE)));

    } else {
      GroupSearchParams params = new GroupSearchParams(searchParams);
      return searchRepository.getGroupsForHomepage(params);
    }
  }

  public Optional<Group> getSingleGroup(
      LinkedHashMap<String, String> searchParams) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);

    GroupSearchResult groups = searchRepository.getGroupsWithDetails(params);
    if(groups.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }
    return groups.getFirstGroup();
  }

  private HomeResult filterResultsByDistance(HomeResult groups, String searchCity, Integer maxDistance){

    HomeResult result = new HomeResult();

    for(HomepageGroup group: groups.getGroupDataMap().values()){
      for(String groupCity: group.getCities()){
        Optional<Double> distance = DistanceService.getDistance(searchCity, groupCity);

        if(!distance.isPresent()){
          var cityOutputA = searchCity.replaceAll("[^a-zA-Z0-9\\s]", "");
          var cityOutputB = groupCity.replaceAll("[^a-zA-Z0-9\\s]", "");
          logger.warn("Could not find distance between cities " + cityOutputA + " and " + cityOutputB);
        }
        else if(distance.get()<=maxDistance){
          result.addGroupData(group);
          break;
        }
      }
    }
    return result;
  }
}
