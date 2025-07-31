package service.read;

import app.groups.data.Group;
import app.groups.data.HomepageGroup;
import app.result.GroupSearchResult;
import app.result.HomeResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import org.apache.logging.log4j.Logger;
import service.data.SearchParameterException;
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
    GroupSearchParams params = new GroupSearchParams(searchParams);

    String searchCity = searchParams.get(GroupSearchParams.CITY);
    String distance = searchParams.get(GroupSearchParams.DISTANCE);

    if(distance != null){

      var updatedParams = new LinkedHashMap<>(searchParams);
      updatedParams.remove(GroupSearchParams.CITY);

      HomeResult groups = searchRepository.getGroupsForHomepage(params);

      if(searchCity == null){
        throw new SearchParameterException("City not specified for distance filter");
      }
      return filterResultsByDistance(
        groups,
        searchParams.get(GroupSearchParams.CITY),
        Integer.parseInt(searchParams.get(GroupSearchParams.DISTANCE)));

    } else {
      return searchRepository.getGroupsForHomepage(params);
    }

  }


  public Group getSingleGroup(
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

    for(HomepageGroup group: groups.getGroupData().values()){
      for(String groupCity: group.getCities()){

        Optional<Double> distance = DistanceService.getDistance(searchCity, groupCity);

        if(!distance.isPresent()){
          // TODO: Sanitize city name parameter and group city before adding them to log file

          var cityOutputA = searchCity.replaceAll("[^a-zA-Z0-9\\s]", "");
          var cityOutputB = groupCity.replaceAll("[^a-zA-Z0-9\\s]", "");

          logger.warn("Could not find distance between cities " + cityOutputA + " and " + cityOutputB);
        }
        if(distance.get()<=maxDistance){
          result.addGroupData(group);
          break;
        }
      }
    }
    return result;
  }
}
