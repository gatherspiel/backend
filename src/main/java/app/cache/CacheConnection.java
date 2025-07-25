package app.cache;

import app.result.GroupSearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.search.GroupSearchParams;
import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.util.Optional;

public class CacheConnection {

  String cacheKey;
  Context context;
  ObjectMapper objectMapper;

  private static final Logger logger = LogUtils.getLogger();

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
    this.context = ctx;
    this.objectMapper = new ObjectMapper();
  }

  public Optional<GroupSearchResult> getCachedSearchResult() throws Exception{

    String data = context.cookieStore().get(cacheKey);
    if(data == null){
      logger.info("Did not find cached search result");

      return Optional.empty();
    }

    logger.info("Found cached search result");

    GroupSearchResult cachedData = objectMapper.readValue(data, GroupSearchResult.class);
    return Optional.of(cachedData);
  }

  public void cacheSearchResult(GroupSearchResult searchResult) throws Exception{
    logger.info("Caching search result");
    String cacheData = objectMapper.writeValueAsString(searchResult);
    context.cookieStore().set(cacheKey, cacheData);
  }

  public static void clearCache(Context ctx){
    logger.info("Clearing result cache");
    ctx.cookieStore().clear();
  }
}
