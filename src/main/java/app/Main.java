package app;

import app.admin.request.BulkUpdateInputRequest;
import app.cache.CacheConnection;
import app.result.HomeResult;
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
import java.util.Optional;

public class Main {

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

    app.get(
      "/countLocations",
      ctx -> {
        var testService = new TestService();
        ctx.result("Number of locations:" + testService.countLocations());
      }
    );

    app.get(
      "/searchEvents",
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

          var gameLocationData = gameLocationsService.getGameLocations(LocalDate.now());

          ctx.json(gameLocationData);
          ctx.status(HttpStatus.OK);

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
