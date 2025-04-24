package app;

import app.data.InputRequest;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.*;
import utils.LogUtils;

import java.time.LocalDate;

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
        }
      )
      .get("/", ctx -> ctx.result("Hello World"))
      .start(7070);

    app.get(
      "/countLocations",
      ctx -> {
        var testService = new TestService();
        ctx.result("" + testService.countLocations());
      }
    );

    app.get(
      "/searchEvents",
      ctx -> {

        long start = System.currentTimeMillis();
        try {
          var connectionProvider = new ConnectionProvider();
          var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
            ctx
          );
          var searchService = new SearchService();

          var groupSearchResult = searchService.getGroups(
            searchParams,
            connectionProvider
          );

          long end = System.currentTimeMillis();

          logger.info("Search time:"+((end-start)/100));
          ctx.json(groupSearchResult);
          ctx.status(200);

          logger.info("Finished search");

        } catch (Exception e) {
          ctx.result("Invalid search parameter");
          ctx.status(400);
        }
      }
    );

    app.get(
        "/searchLocations",
        ctx->{

          //TODO: Add optional location tag parameter.
          var connectionProvider = new ConnectionProvider();
          GameLocationsService gameLocationsService = new GameLocationsService();

          var gameLocationData = gameLocationsService.getGameLocations(connectionProvider, LocalDate.now());
          logger.info("Retrieved game location data");

          ctx.json(gameLocationData);
          ctx.status(200);

        });

    app.get(
        "/listCities",
        ctx->{
          var connectionProvider = new ConnectionProvider();
          GameLocationsService gameLocationsService = new GameLocationsService();

          var cities = gameLocationsService.getAllEventLocations(connectionProvider);
          logger.info("Retrieved event cities");
          ctx.json(cities);
          ctx.status(200);
        });

    app.post(
      "/admin/saveData",
      ctx -> {
        var authService = new AuthService();

        var data = ctx.bodyAsClass(InputRequest.class);
        authService.validate(data);
        ctx.result("Test");

        var connectionProvider = new ConnectionProvider();
        var bulkUpdateService = new BulkUpdateService();
        bulkUpdateService.bulkUpdate(data.getData(), connectionProvider);
      }
    );
  }
}
