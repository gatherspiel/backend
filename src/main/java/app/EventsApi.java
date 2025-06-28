package app;

import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class EventsApi {
  public static Logger logger = LogUtils.getLogger();

  public static void eventEndpoints(Javalin app){
    app.get(
        "/events",
        ctx -> {
          var eventId = ctx.queryParam("id");

          var connectionProvider = new ConnectionProvider();
          var connection = connectionProvider.getDatabaseConnection();

        }
    );
  }

}
