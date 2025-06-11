package app;

import app.data.Group;
import app.request.BulkUpdateInputRequest;
import app.result.error.GroupNotFoundError;
import app.result.error.InvalidGroupRequestError;
import app.result.error.PermissionError;
import app.result.groupPage.GroupPageData;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import database.search.GroupSearchParams;
import database.user.UserRepository;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.*;
import service.auth.AuthService;
import service.auth.SupabaseAuthProvider;
import service.data.SearchParameterException;
import service.provider.ReadGroupDataProvider;
import service.read.GameLocationsService;
import service.read.ReadGroupService;
import service.read.SearchService;
import service.update.GroupEditService;
import service.user.UserService;
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

        var connectionProvider = new ConnectionProvider();
        var authService = AuthService.createSupabaseAuthService(connectionProvider.getDatabaseConnection());
        var data = ctx.bodyAsClass(BulkUpdateInputRequest.class);
        authService.validateBulkUpdateInputRequest(data);
        ctx.result("Saved data");

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

            var authService = AuthService.createSupabaseAuthService(connectionProvider.getDatabaseConnection());
            var currentUser = authService.getUser(ctx);
            logger.info("Current user:"+currentUser);

            var readGroupDataProvider = ReadGroupDataProvider.create();
            var groupService = new ReadGroupService(readGroupDataProvider);

            GroupPageData pageData = groupService.getGroupPageData(currentUser, searchParams, connectionProvider);
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

          Group group = null;
          try {
            var connectionProvider = new ConnectionProvider();

            var authService = AuthService.createSupabaseAuthService(connectionProvider.getDatabaseConnection());
            var currentUser = authService.getUser(ctx);
            var groupEditService = new GroupEditService();

            group = ctx.bodyAsClass(Group.class);

            groupEditService.editGroup(currentUser,group, connectionProvider);

            logger.info("Retrieved group data");
            ctx.status(200);
          } catch (UnrecognizedPropertyException  | GroupNotFoundError | InvalidGroupRequestError e) {
            logger.error(e.getMessage());
            ctx.status(400);
            ctx.result(e.getMessage());
          } catch(PermissionError e) {
            ctx.status(403);
            logger.error(e.getMessage());
            ctx.result(e.getMessage());
          }
          catch(Exception e){
            e.printStackTrace();

            ctx.status(500);
          }
        }
    );
  }
}
