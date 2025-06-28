package app;

import app.groups.data.Event;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.util.Optional;

public class EventsApi {
  public static Logger logger = LogUtils.getLogger();

  public static void eventEndpoints(Javalin app){
    app.get(
      "groups/events/:id/",
      ctx -> {

        try {
          var eventId = ctx.pathParam("id");
          var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());

          Optional<Event> event = sessionContext.createEventService().getEvent(Integer.parseInt(eventId));

          if(event.isEmpty()){
            ctx.result("No event found for id:"+eventId);
            ctx.status(HttpStatus.NOT_FOUND);
          }
          ctx.json(event.get());
        } catch(NumberFormatException e) {
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

      }
    );

    app.delete(
      "groups/:groupId/events/:eventId",
      ctx -> {

        try {
          var eventId = Integer.parseInt(ctx.queryParam("eventId"));
          var groupId = Integer.parseInt(ctx.queryParam("eventId"));

          var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          sessionContext.createEventService().deleteEvent(eventId, groupId);

          ctx.json("Deleted event:"+eventId);
          ctx.status(HttpStatus.OK);
        } catch(NumberFormatException e) {
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    );

    app.post(
        "groups/:groupId/events/",
        ctx -> {

          try {
            var groupId = Integer.parseInt(ctx.queryParam("eventId"));
            var event = ctx.bodyAsClass(Event.class);

            var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
            var createdEvent = sessionContext.createEventService().createEvent(event, groupId);
            ctx.result("Created event with id:"+event.getId());
            ctx.status(HttpStatus.OK);
          } catch(NumberFormatException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
          } catch(Exception e) {
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
    );

    app.put(
        "groups/:groupId/events/",
        ctx -> {

          try {
            var groupId = Integer.parseInt(ctx.queryParam("eventId"));
            var event = ctx.bodyAsClass(Event.class);

            var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
            sessionContext.createEventService().updateEvent(event, groupId);
            ctx.result("Updated event");
            ctx.status(HttpStatus.OK);
          } catch(NumberFormatException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
          } catch(Exception e) {
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
    );
  }
}
