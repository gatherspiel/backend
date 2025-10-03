package app;

import app.groups.data.Event;
import app.result.error.group.DuplicateEventError;
import app.users.data.SessionContext;
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
      "groups/events/{id}/",
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
          ctx.status(HttpStatus.OK);
        } catch(NumberFormatException e) {
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
          e.printStackTrace();
          ctx.result(e.getMessage());
          logger.debug("Error:"+e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

      }
    );

    app.delete(
      "groups/{groupId}/events/{id}/",
      ctx -> {

        try {
          var eventId = Integer.parseInt(ctx.pathParam("id"));
          var groupId = Integer.parseInt(ctx.pathParam("groupId"));

          var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          sessionContext.createEventService().deleteEvent(eventId, groupId);

          logger.info("Deleted event:"+eventId);
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
        "groups/{groupId}/events/",
        ctx -> {

          try {
            var groupId = Integer.parseInt(ctx.pathParam("groupId"));
            var event = ctx.bodyAsClass(Event.class);
            var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
            var createdEvent = sessionContext.createEventService().createEvent(event, groupId);
            ctx.result("Created event with id:"+createdEvent.getId());
            ctx.status(HttpStatus.OK);
          } catch(NumberFormatException | DuplicateEventError e) {
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
        "groups/{groupId}/events/",
        ctx -> {

          try {
            var groupId = Integer.parseInt(ctx.pathParam("groupId"));
            var event = ctx.bodyAsClass(Event.class);

            var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
            sessionContext.createEventService().updateEvent(event, groupId);
            ctx.result("Updated event");
            ctx.status(HttpStatus.OK);
          } catch(NumberFormatException | DuplicateEventError e) {
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
