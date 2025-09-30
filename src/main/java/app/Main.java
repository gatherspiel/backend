package app;

import app.admin.request.BulkUpdateInputRequest;
import app.cache.CacheConnection;
import app.result.listing.HomeResult;
import app.users.data.SessionContext;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import service.auth.supabase.SupabaseAuthProvider;
import service.read.DistanceService;
import service.read.GameLocationsService;
import service.read.TestService;
import service.update.BulkUpdateService;
import service.user.UserService;
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
  public static final int RATE_LIMIT = 5;
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

          config.jsonMapper(new JavalinJackson().updateMapper(mapper->{
            mapper.registerModule(new JavaTimeModule());
          }));
        }
      )
      .get("/", ctx -> ctx.result("Hello World"))
      .start(7070);

    DistanceService.loadData();
    UsersApi.userEndpoints(app);
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
        ipAddressRequests.put(ip, new AbstractMap.SimpleEntry<>(1,System.currentTimeMillis()));
      } else {
        var data = ipAddressRequests.get(ip);
        if(data.getKey() > RATE_LIMIT){
          final String rateLimitError = "Exceeded rate limit";
          logger.error(rateLimitError + "user agent:{}", userAgent);
          ctx.status(401);
          throw new Exception(rateLimitError);
        }

        Long currentTime = System.currentTimeMillis();
        //Reset rate limit counter
        if(currentTime - data.getValue() > 1000){
          ipAddressRequests.put(ip,new AbstractMap.SimpleEntry<>(1, currentTime));
        }
        else {
          ipAddressRequests.put(ip, new AbstractMap.SimpleEntry<>(data.getKey()+1,currentTime));
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
          var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
            ctx
          );

          var cacheConnection = new CacheConnection(ctx);
          Optional<HomeResult> cachedData = cacheConnection.getCachedSearchResult();
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

            logger.info("Search time:" + ((end - start) / 1000));
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

    //TODO: Consider deleting this endpoint.
    app.post(
      "/admin/saveData",
      ctx -> {

        var connectionProvider = new ConnectionProvider();
        UserService userService = new UserService(UserService.DataProvider.createDataProvider(connectionProvider.getDatabaseConnection()));
        SupabaseAuthProvider supabaseAuthProvider = new SupabaseAuthProvider();

        AuthService authService = new AuthService(supabaseAuthProvider, userService);
        var data = ctx.bodyAsClass(BulkUpdateInputRequest.class);
        authService.validateBulkUpdateInputRequest(data);
        ctx.result("Saved data");

        var bulkUpdateService = new BulkUpdateService();
        bulkUpdateService.bulkUpdate(data.getData(), connectionProvider);
      }
    );

    app.after(ctx->{
      if(!ctx.method().equals(HandlerType.GET) && !ctx.method().equals(HandlerType.OPTIONS)){
        CacheConnection.clearCache();
      }
    });
  }
}
