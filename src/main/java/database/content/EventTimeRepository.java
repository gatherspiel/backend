package database.content;

import app.groups.data.Event;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;

public class EventTimeRepository {
  private static final Logger logger = LogManager.getLogger(
    EventTimeRepository.class
  );

  Connection conn;

  public EventTimeRepository(Connection conn){
    this.conn = conn;
  }

  //TODO: Handle case where a date and time is specified.
  public void setEventDay(Event event) throws Exception {
    if (!hasEventDay(event)) {
      String day = event.getDay();
      if(day == null){
        logger.warn("No day has been specified. The current day will be used");
        day = LocalDateTime.now().getDayOfWeek().toString();
      }
      String query =
        "INSERT into event_time (day_of_week, event_id) VALUES(cast(? AS dayofweek), ?)";
      PreparedStatement insert = conn.prepareStatement(query);
      insert.setString(1, day);
      insert.setInt(2, event.getId());
      insert.executeUpdate();
    }
  }

  public void deleteEventTimeInfo(int eventId) throws Exception {

    String query = "DELETE  FROM event_time where event_id = ?";
    PreparedStatement delete = conn.prepareStatement(query);
    delete.setInt(1, eventId);
    delete.executeUpdate();
  }

  public boolean hasEventDay(Event event) throws Exception {

    if(event.getDay().isBlank()){
      return false;
    }
    try {
      String day = event.getDay();
      String query =
        "SELECT * from event_time where day_of_week = cast(? AS dayofweek) AND event_id = ?";
      PreparedStatement select = conn.prepareStatement(query);
      select.setString(1, day);
      select.setInt(2, event.getId());
      ResultSet rs = select.executeQuery();

      return rs.next();
    } catch (PSQLException e) {
      logger.error("Query error in hasEventDay");
      throw e;
    }
  }

  public void setEventDate(int eventId, LocalDateTime start, LocalDateTime end)
    throws Exception {

    String query =
        "INSERT into event_time (event_id, start_time, end_time) VALUES(?, ?, ?)";
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, eventId);
    insert.setTimestamp(2, Timestamp.valueOf(start));
    insert.setTimestamp(3, Timestamp.valueOf(end));

    insert.executeUpdate();

  }
}
