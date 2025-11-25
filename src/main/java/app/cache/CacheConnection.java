package app.cache;

import app.groups.Event;
import app.result.group.GroupPageData;
import app.result.listing.EventSearchResult;
import app.result.listing.HomeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.search.SearchParams;
import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CacheConnection {

  String cacheKey;
  ObjectMapper objectMapper;

  private static final Logger logger = LogUtils.getLogger();

  private static final Map<String, String> groupSearchResultCache = new ConcurrentHashMap<String, String>();
  private static final Map<String, EventSearchResult> eventSearchResultCache = new ConcurrentHashMap<String, EventSearchResult>();
  private static final Map<String, GroupPageData> groupPageCache = new ConcurrentHashMap<>();

  private static LocalDateTime lastSearchTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

  public CacheConnection(Context ctx){
    String day = ctx.queryParam(SearchParams.DAYS_OF_WEEK);
    String location = ctx.queryParam(SearchParams.CITY);
    String area = ctx.queryParam(SearchParams.AREA);
    String name = ctx.queryParam(SearchParams.NAME);
    String distance = ctx.queryParam(SearchParams.DISTANCE);
    String userGroupEvents = ctx.queryParam(SearchParams.USER_GROUP_EVENTS);
    String key = "";
    if(day != null){
      key+= SearchParams.DAYS_OF_WEEK +"_"+day+"_";
    }
    if(location !=null){
      key+= SearchParams.CITY+"_"+location+"_";
    }
    if(area !=null){
      key+= SearchParams.AREA+"_"+area+"_";
    }
    if(name !=null){
      key+= SearchParams.NAME+"_"+name;
    }
    if(distance !=null){
      key+= SearchParams.DISTANCE+"_"+distance;
    }
    if(userGroupEvents !=null){
      key+= SearchParams.USER_GROUP_EVENTS+"_"+userGroupEvents;
    }
    this.cacheKey = key;
    this.objectMapper = new ObjectMapper();
  }


  public Optional<EventSearchResult> getCachedEventSearchResult() throws Exception {

    LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    //Event search cache should only last for one minute.
    if(currentTime.isAfter(lastSearchTime)){
      eventSearchResultCache.clear();
      return Optional.empty();
    }

    if(eventSearchResultCache.containsKey(cacheKey)){
      return Optional.of(eventSearchResultCache.get(cacheKey));
    }
    return Optional.empty();
  }

  public Optional<HomeResult> getCachedGroupSearchResult() throws Exception{

    try {
      String data = groupSearchResultCache.get(cacheKey);
      if(data == null){
        return Optional.empty();
      }

      LogUtils.printDebugLog("Found cached search result");
      CompressedHomepageSearchResult cachedData = objectMapper.readValue(data, CompressedHomepageSearchResult.class);
      return Optional.of(cachedData.getHomepageSearchResult());
    } catch (Exception e) {
      logger.warn("Failed to retrieve cached search result because of error: "+e.getMessage());
      return Optional.empty();
    }
  }

  public void cacheSearchResult(EventSearchResult eventSearchResult){
    try {
      eventSearchResultCache.put(cacheKey, eventSearchResult);
      LogUtils.printDebugLog("Cached search result");
    } catch(Exception e){
      logger.warn("Failed to cache result because of error:"+e.getMessage());
    }
  }

  public void cacheSearchResult(HomeResult searchResult)  {
    try {
      CompressedHomepageSearchResult compressed = new CompressedHomepageSearchResult();
      compressed.addGroups(searchResult);

      String cacheData = objectMapper.writeValueAsString(compressed);
      groupSearchResultCache.put(cacheKey, cacheData);

      LogUtils.printDebugLog("Cached search result");
    } catch (Exception e){
      logger.warn("Failed to cache result because of error:"+e.getMessage());
    }
  }


  public Optional<GroupPageData> getCachedGroupPage() throws Exception {
    if(!groupPageCache.containsKey(cacheKey)){
      return Optional.empty();
    }
    return Optional.of(groupPageCache.get(cacheKey));

  }

  public void cacheGroupPage(GroupPageData data){
    groupPageCache.put(cacheKey, data);
  }

  public static void clearCache(){
    logger.info("Clearing cache due to data update");
    eventSearchResultCache.clear();
    groupSearchResultCache.clear();
    groupPageCache.clear();
  }
}
