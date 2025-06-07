package app;

import app.data.Group;
import app.request.BulkUpdateInputRequest;
import app.result.groupPage.GroupPageData;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.*;
import service.auth.AuthService;
import service.data.SearchParameterException;
import service.provider.ReadGroupDataProvider;
import service.read.GameLocationsService;
import service.read.ReadGroupService;
import service.read.SearchService;
import service.update.GroupEditService;
import utils.LogUtils;

import java.time.LocalDate;

public class Main {

  public static Logger logger = LogUtils.getLogger();
  public static AuthService authService = new AuthService();
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
        ctx.result("Number of locations:" + testService.countLocations());
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

          var searchService = new SearchService(authService.getReadOnlyUser());

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
          e.printStackTrace();
          ctx.result("Invalid search parameter");
          ctx.status(400);
        }
      }
    );

    app.get(
        "/searchLocations",
        ctx->{

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

          String areaFilter = ctx.queryParam("area");

          var cities = gameLocationsService.getAllEventLocations(connectionProvider, areaFilter);
          logger.info("Retrieved event cities");
          ctx.json(cities);
          ctx.status(200);
        });

    app.post(
      "/admin/saveData",
      ctx -> {
        var authService = new AuthService();

        var data = ctx.bodyAsClass(BulkUpdateInputRequest.class);
        authService.validateBulkUpdateInputRequest(data);
        ctx.result("Test");

        var connectionProvider = new ConnectionProvider();
        var bulkUpdateService = new BulkUpdateService();
        bulkUpdateService.bulkUpdate(data.getData(), connectionProvider);
      }
    );

    app.get(
        "/groups",
        ctx -> {

          try {
            var connectionProvider = new ConnectionProvider();
            var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
                ctx
            );

            var currentUser = authService.getUser(ctx, connectionProvider);
            logger.info("Current user:"+currentUser);

            var readGroupDataProvider = ReadGroupDataProvider.create(currentUser, connectionProvider);
            var groupService = new ReadGroupService(readGroupDataProvider);

            GroupPageData pageData = groupService.getGroupPageData(searchParams, connectionProvider);
            logger.info("Retrieved group data");
            ctx.json(pageData);
            ctx.status(200);
          } catch (SearchParameterException e) {
            e.printStackTrace();
            ctx.status(404);
          } catch(Exception e){
            e.printStackTrace();
            ctx.status(500);
          }
        }
    );

    app.put(
        "/groups",
        ctx -> {

          try {
            var connectionProvider = new ConnectionProvider();

            var currentUser = authService.getUser(ctx, connectionProvider);
            var groupEditService = new GroupEditService();

            var group = ctx.bodyAsClass(Group.class);

            groupEditService.editGroup(currentUser,group, connectionProvider);

            logger.info("Retrieved group data");
            ctx.status(200);
          } catch (SearchParameterException e) {
            e.printStackTrace();
            ctx.status(404);
          } catch(Exception e){
            e.printStackTrace();
            ctx.status(500);
          }
        }
    );
  }
}
