package service.read;

import app.groups.Group;
import app.result.listing.*;
import app.users.User;
import database.search.SearchParams;
import database.search.SearchRepository;
import org.apache.logging.log4j.Logger;
import app.result.error.SearchParameterException;
import service.data.SearchParameterValidator;
import utils.LogUtils;

import java.sql.Connection;
import java.util.*;

public class SearchService {

  private final SearchRepository searchRepository;
  private final Logger logger;
  private final User user;
  public SearchService(Connection conn, User user){
    this.searchRepository = new SearchRepository(conn);
    this.logger = LogUtils.getLogger();
    this.user = user;
  }

  public EventSearchResult getEventsForHomepage(LinkedHashMap<String,String> searchParamMap) throws Exception{

    String searchCity = searchParamMap.get(SearchParams.CITY);
    String distance = searchParamMap.get(SearchParams.DISTANCE);

    if(distance != null){
      LinkedHashMap<String, String> updatedParams = new LinkedHashMap<>(searchParamMap);
      updatedParams.remove(SearchParams.CITY);
      SearchParams params = new SearchParams(updatedParams);
      ArrayList<EventSearchResultItem> results = searchRepository.getEventSearchResults(params,user);
      return filterAndSortEventSearchResults(
        results,
        searchCity,
        SearchParameterValidator.validateAndRetrieveDistanceParameter(distance));
    }else {
      SearchParams params = new SearchParams(searchParamMap);
      ArrayList<EventSearchResultItem> results = searchRepository.getEventSearchResults(params,user);

      TreeSet<EventSearchResultItem> sorted = new TreeSet<>(new EventSearchResultComparator());
      sorted.addAll(results);

      return new EventSearchResult(sorted.stream().toList());
    }
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

  public static class EventSearchResultComparator implements Comparator<EventSearchResultItem> {
    public int compare(EventSearchResultItem item1, EventSearchResultItem item2){
      double compare = item1.getDistance().compareTo(item2.getDistance());

      if(compare == 0.0){
        compare = item1.getNextEventDate().compareTo(item2.getNextEventDate());
      }
      if(compare == 0.0){
        compare = item1.getNextEventTime().compareTo(item2.getNextEventTime());
      }
      if(compare == 0.0){
        compare = item1.getEventName().compareTo(item2.getEventName());
      }
      return (int)compare;
    }
  }

  private EventSearchResult filterAndSortEventSearchResults(ArrayList<EventSearchResultItem> data, String searchCity, Double maxDistance){

    TreeSet<EventSearchResultItem> sorted = new TreeSet<>(new EventSearchResultComparator());

    for(EventSearchResultItem item: data){

      String eventCity = item.getEventLocation().getCity();
      Optional<Double> distance = DistanceService.getDistance(searchCity, eventCity);

      if(!distance.isPresent()){
        var cityOutputA = searchCity.replaceAll("[^a-zA-Z0-9\\s]", "");
        var cityOutputB = eventCity.replaceAll("[^a-zA-Z0-9\\s]", "");
        logger.warn("Could not find distance between cities " + cityOutputA + " and " + cityOutputB);
      }
      else if(distance.get()<=maxDistance){
        item.setDistance(distance.get());
        sorted.add(item);
      }
    }

    List<EventSearchResultItem> resultItemList = sorted.stream().toList();
    return new EventSearchResult(resultItemList);
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
