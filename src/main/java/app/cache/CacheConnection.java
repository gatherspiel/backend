package app.cache;

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

  private static Map<String, String> cache = new ConcurrentHashMap<String, String>();

  public CacheConnection(Context ctx){
    String day = ctx.queryParam(GroupSearchParams.DAY_OF_WEEK);
    String location = ctx.queryParam(GroupSearchParams.CITY);
    String area = ctx.queryParam(GroupSearchParams.AREA);

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
    this.cacheKey = key;
    this.objectMapper = new ObjectMapper();
  }

  public Optional<HomeResult> getCachedSearchResult() throws Exception{

    try {
      String data = cache.get(cacheKey);
      if(data == null){
        return Optional.empty();
      }

      LogUtils.printDebugLog("Found cached result");
      logger.debug("Found cached result");
      CompressedHomepageSearchResult cachedData = objectMapper.readValue(data, CompressedHomepageSearchResult.class);
      return Optional.of(cachedData.getHomepageSearchResult());
    } catch (Exception e) {

      logger.warn("Failed to retrieve cache result because of error: "+e.getMessage());
      return Optional.empty();
    }

  }

  public void cacheSearchResult(HomeResult searchResult)  {
    try {
      CompressedHomepageSearchResult compressed = new CompressedHomepageSearchResult();
      compressed.addGroups(searchResult);

      String cacheData = objectMapper.writeValueAsString(compressed);

      cache.put(cacheKey, cacheData);

      logger.info("Cached result with key "+cacheKey);
    } catch (Exception e){
      logger.warn("Failed to cache result because of error:"+e.getMessage());
    }

  }



  public static void clearCache(){
    logger.info("Clearing search result cache");
    cache.clear();
  }
}
