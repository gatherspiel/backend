package service.read;

import app.groups.Group;
import app.result.listing.*;
import database.search.SearchParams;
import database.search.SearchRepository;
import org.apache.logging.log4j.Logger;
import app.result.error.SearchParameterException;
import utils.LogUtils;

import java.sql.Connection;
import java.util.*;

public class SearchService {

  private final SearchRepository searchRepository;
  private final Logger logger;
  public SearchService(Connection conn){
    this.searchRepository = new SearchRepository(conn);
    this.logger = LogUtils.getLogger();
  }

  public EventSearchResult getEventsFromHomePage(LinkedHashMap<String,String> searchParams) throws Exception{

    String searchCity = searchParams.get(SearchParams.CITY);
    String distance = searchParams.get(SearchParams.DISTANCE);

    SearchParams params = new SearchParams(searchParams);
    ArrayList<EventSearchResultItem> results = searchRepository.getEventSearchResult(params);

    return filterAndSortEventSearchResultsByDistance(results, searchCity, Integer.parseInt(distance));
  }
  public HomeResult getGroupsForHomepage(
    LinkedHashMap<String, String> searchParams
  ) throws Exception
  {
    String searchCity = searchParams.get(SearchParams.CITY);
    String distance = searchParams.get(SearchParams.DISTANCE);

    if(distance != null){

      var updatedParams = new LinkedHashMap<>(searchParams);
      updatedParams.remove(SearchParams.CITY);

      SearchParams params = new SearchParams(updatedParams);
      HomeResult groups = searchRepository.getGroupsForHomepage(params);

      if(searchCity == null){
        throw new SearchParameterException("City not specified for distance filter");
      }
      return filterGroupSearchResultsByDistance(
        groups,
        searchParams.get(SearchParams.CITY),
        Integer.parseInt(searchParams.get(SearchParams.DISTANCE)));

    } else {
      SearchParams params = new SearchParams(searchParams);
      return searchRepository.getGroupsForHomepage(params);
    }
  }

  public Optional<Group> getSingleGroup(
      LinkedHashMap<String, String> searchParams) throws Exception
  {
    SearchParams params = new SearchParams(searchParams);

    GroupSearchResult groups = searchRepository.getGroupsWithDetails(params);
    if(groups.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }
    return groups.getFirstGroup();
  }

  private EventSearchResult filterAndSortEventSearchResultsByDistance(ArrayList<EventSearchResultItem> data, String searchCity, Integer maxDistance){

    EventSearchResult eventSearchResult = new EventSearchResult();
    TreeMap<Double, EventSearchResultItem> sorted = new TreeMap<>();

    for(EventSearchResultItem item: data){

      String eventCity = item.getEventLocation().getCity();
      Optional<Double> distance = DistanceService.getDistance(searchCity, eventCity);

      if(!distance.isPresent()){
        var cityOutputA = searchCity.replaceAll("[^a-zA-Z0-9\\s]", "");
        var cityOutputB = eventCity.replaceAll("[^a-zA-Z0-9\\s]", "");
        logger.warn("Could not find distance between cities " + cityOutputA + " and " + cityOutputB);
      }
      else if(distance.get()<=maxDistance){
        sorted.put(distance.get(), item);
      }
    }

    List<EventSearchResultItem> resultItemList = sorted.values().stream().toList();
    eventSearchResult.setEventData(resultItemList);
    return eventSearchResult;
  }

  private HomeResult filterGroupSearchResultsByDistance(HomeResult groups, String searchCity, Integer maxDistance){

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
