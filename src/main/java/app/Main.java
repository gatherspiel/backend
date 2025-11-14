package app;

import app.cache.CacheConnection;
import app.feedback.Feedback;
import app.result.listing.EventSearchResult;
import app.result.listing.HomeResult;
import app.users.SessionContext;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import database.search.SearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import org.apache.logging.log4j.Logger;
import service.EmailService;
import service.auth.AuthService;
import service.read.DistanceService;
import service.update.GameLocationsService;
import service.read.TestService;
import utils.LogUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

  public static String[] agents = {
      "https://openai.com/",
      "google.com",
      "bing.com",
      "baidu.com",
      "facebook.com",
      "linkedin.com",
      "anthropic.com",
      "perplexity.ai"
  };

  public static final Set<String> blockedUserStrings = new HashSet<>(Arrays.asList(agents));
  public static ConcurrentHashMap<String, Map.Entry<Integer,Long>> ipAddressRequests = new ConcurrentHashMap<>();
  public static final int RATE_LIMIT = 6;
  public static Logger logger = LogUtils.getLogger();
  public static void main(String[] args) {
    var app = Javalin
      .create(
        config -> {
          config.bundledPlugins.enableCors(
            cors -> {
              cors.addRule(
                it -> {
                  it.anyHost();
                }
              );
            }
          );
          config.http.maxRequestSize = 10000000;
          config.jsonMapper(new JavalinJackson().updateMapper(mapper->{
            mapper.registerModule(new JavaTimeModule());
          }));
        }
      )
      .get("/", ctx -> ctx.result("Hello World"))
      .start(7070);

    DistanceService.loadData();
    UserApi.userEndpoints(app);
    GroupsApi.groupEndpoints(app);
    EventsApi.eventEndpoints(app);

    app.before(ctx->{
      final var userAgent = ctx.header("User-Agent");

      for(String blockedString: blockedUserStrings){
        if(userAgent.contains(blockedString)){
          ctx.status(401);
          throw new Exception("When playing Monopoly, getting 3 or 4 railroads early in the game is powerful");
        }
      }

      final var ip = ctx.ip();
      if(!ipAddressRequests.containsKey(ip)){
        ipAddressRequests.put(ip, new AbstractMap.SimpleEntry<Integer,Long>(1,System.currentTimeMillis()));
      } else {

        var data = ipAddressRequests.get(ip);
        Long currentTime = System.currentTimeMillis();
        //Reset rate limit counter
        if(currentTime - data.getValue() > 1000){
          ipAddressRequests.put(ip,new AbstractMap.SimpleEntry<Integer,Long>(1, currentTime));
        }
        else if(data.getKey() > RATE_LIMIT){
          final String rateLimitError = "Exceeded rate limit";
          logger.info("Time:"+(System.currentTimeMillis()-data.getValue()));
          logger.error(rateLimitError + "user agent:{}", userAgent);
          ctx.status(401);
          throw new Exception(rateLimitError);
        }
        else {
          logger.info("Requests:"+(data.getKey()+1));
          ipAddressRequests.put(ip, new AbstractMap.SimpleEntry<Integer,Long>(data.getKey()+1,data.getValue()));
        }
      }
    });

    app.get(
      "/countLocations",
      ctx -> {
        var testService = new TestService();
        ctx.result("Number of locations:" + testService.countLocations());
      }
    );

    app.get(
      "/searchGroups",
      ctx -> {

        long start = System.currentTimeMillis();
        try {

          var sessionContext = SessionContext.createContextWithoutUser(new ConnectionProvider());
          var searchParams = SearchParams.generateParameterMapFromQueryString(
            ctx
          );
          var cacheConnection = new CacheConnection(ctx);
          Optional<HomeResult> cachedData = cacheConnection.getCachedGroupSearchResult();
          if(cachedData.isPresent()){
            ctx.json(cachedData.get());
            ctx.status(HttpStatus.OK);
          } else {
            var searchService = sessionContext.createSearchService();
            var groupSearchResult = searchService.getGroupsForHomepage(
                searchParams
            );

            cacheConnection.cacheSearchResult(groupSearchResult);
            long end = System.currentTimeMillis();

            logger.info("Search time for groups:" + ((end - start) / 1000));
            ctx.json(groupSearchResult);
            ctx.status(HttpStatus.OK);
          }

        } catch (Exception e) {
          e.printStackTrace();
          ctx.result("Invalid search parameter");
          ctx.status(HttpStatus.BAD_REQUEST);
        }
      }
    );

    app.get(
      "/searchEvents",
      ctx -> {

        long start = System.currentTimeMillis();
        try {

          var sessionContext = SessionContext.createContextWithoutUser(new ConnectionProvider());
          var searchParams = SearchParams.generateParameterMapFromQueryString(
            ctx
          );
          var cacheConnection = new CacheConnection(ctx);
          Optional<EventSearchResult> cachedData = cacheConnection.getCachedEventSearchResult();
          if(cachedData.isPresent()){
            ctx.json(cachedData.get());
            ctx.status(HttpStatus.OK);
          } else {
            var searchService = sessionContext.createSearchService();
            var eventSearchResult = searchService.getEventsForHomepage(
              searchParams
            );

            cacheConnection.cacheSearchResult(eventSearchResult);
            long end = System.currentTimeMillis();

            logger.info("Search time for groups:" + ((end - start) / 1000));
            ctx.json(eventSearchResult);
            ctx.status(HttpStatus.OK);
          }

        } catch (Exception e) {
          e.printStackTrace();
          ctx.result("Invalid search parameter");
          ctx.status(HttpStatus.BAD_REQUEST);
        }
      }
    );

    app.get(
      "/searchLocations",
      ctx->{

        var connectionProvider = new ConnectionProvider();
        var conn = connectionProvider.getDatabaseConnection();

        GameLocationsService gameLocationsService = new GameLocationsService(conn);

        String locationType = ctx.queryParam("locationType");

        if(locationType == null){
          ctx.result("Missing locationType parameter");
          ctx.status(HttpStatus.BAD_REQUEST);
        }
        else if(locationType.equals("conventions")){
          ctx.json(gameLocationsService.getConventions(LocalDate.now()));
          ctx.status(HttpStatus.OK);

        }
        else if(locationType.equals("gameRestaurants")){
          ctx.json(gameLocationsService.getGameRestaurants());
          ctx.status(HttpStatus.OK);
        }
        else if(locationType.equals("gameStores")){
          ctx.json(gameLocationsService.getGameStores());
          ctx.status(HttpStatus.OK);
        } else {
          ctx.json("Invalid locationType parameter.");
          ctx.status(HttpStatus.BAD_REQUEST);
        }

      });

    app.get(
      "/listCities",
      ctx->{
        var connectionProvider = new ConnectionProvider();
        var conn = connectionProvider.getDatabaseConnection();

        GameLocationsService gameLocationsService = new GameLocationsService(conn);

        String areaFilter = ctx.queryParam("area");

        var cities = gameLocationsService.getAllEventLocations(areaFilter);
        ctx.json(cities);
        ctx.status(HttpStatus.OK);
      });

    app.post(
      "/feedback",
      ctx->{

        try {

          new EmailService(AuthService.getReadOnlyUser()).sendFeedbackNotification(ctx.bodyAsClass(Feedback.class));
          ctx.result("Sucessfully submitted feedback");
          ctx.status(200);
        } catch(Exception e){
          e.printStackTrace();
          logger.error(e.getMessage());
          ctx.status(500);
        }

      }
    );

    app.after(ctx->{
      if(!ctx.method().equals(HandlerType.GET) && !ctx.method().equals(HandlerType.OPTIONS)){
        CacheConnection.clearCache();
      }
    });
  }
}
