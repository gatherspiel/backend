package app.cache;

import app.groups.data.GroupPageData;
import app.result.HomeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.search.GroupSearchParams;
import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CacheConnection {

  String cacheKey;
  ObjectMapper objectMapper;

  private static final Logger logger = LogUtils.getLogger();

  private static final Map<String, String> searchResultCache = new ConcurrentHashMap<String, String>();
  private static final Map<String, GroupPageData> groupPageCache = new ConcurrentHashMap<>();

  public CacheConnection(Context ctx){
    String day = ctx.queryParam(GroupSearchParams.DAY_OF_WEEK);
    String location = ctx.queryParam(GroupSearchParams.CITY);
    String area = ctx.queryParam(GroupSearchParams.AREA);
    String name = ctx.queryParam(GroupSearchParams.NAME);
    String distance = ctx.queryParam(GroupSearchParams.DISTANCE);

    String key = "";
    if(day != null){
      key+= GroupSearchParams.DAY_OF_WEEK+"_"+day+"_";
    }
    if(location !=null){
      key+= GroupSearchParams.CITY+"_"+location+"_";
    }
    if(area !=null){
      key+= GroupSearchParams.AREA+"_"+area+"_";
    }
    if(name !=null){
      key+= GroupSearchParams.NAME+"_"+name;
    }
    if(distance !=null){
      key+= GroupSearchParams.DISTANCE+"_"+distance;
    }
    this.cacheKey = key;
    this.objectMapper = new ObjectMapper();
  }

  public Optional<HomeResult> getCachedSearchResult() throws Exception{

    try {
      String data = searchResultCache.get(cacheKey);
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

  public void cacheSearchResult(HomeResult searchResult)  {
    try {
      CompressedHomepageSearchResult compressed = new CompressedHomepageSearchResult();
      compressed.addGroups(searchResult);

      String cacheData = objectMapper.writeValueAsString(compressed);
      searchResultCache.put(cacheKey, cacheData);

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
    searchResultCache.clear();
    groupPageCache.clear();
  }
}
