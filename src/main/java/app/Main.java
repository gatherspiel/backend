package app;

import app.data.InputRequest;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import service.AuthService;
import service.BulkUpdateService;
import service.SearchService;
import service.TestService;

import java.util.LinkedHashMap;

public class Main {

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


          //TODO: Add logic and read query parameters

          var connectionProvider = new ConnectionProvider();
          var searchParams = GroupSearchParams.generateParameterMapFromQueryString(ctx);
          var searchService = new SearchService();

          var groupSearchResult = searchService.getGroups(searchParams, connectionProvider);

          ctx.json(groupSearchResult);
          ctx.status(200);

          System.out.println("Finished");

        }
    );

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
