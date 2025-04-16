package database;

import app.data.Convention;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

import net.bytebuddy.asm.Advice;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class ConventionsRepository {
  Logger logger;

  public ConventionsRepository() {
    logger = LogUtils.getLogger();
  }

  public HashMap<Integer, Convention> getConventions(Connection conn, LocalDate searchStartDate) throws Exception{
    String query = "SELECT * from events\n" +
        "JOIN event_time on event_time.event_id = events.id\n" +
        "WHERE is_convention is TRUE\n" +
        "AND start_time >= ?";

    PreparedStatement select = conn.prepareStatement(query);
    select.setTimestamp(1, Timestamp.valueOf(searchStartDate.atStartOfDay()));
    ResultSet rs = select.executeQuery();

    HashMap<Integer, Convention> conventions = new HashMap<Integer, Convention>();

    HashMap<Integer, Integer> conventionDays = new HashMap<Integer, Integer>();
    while(rs.next()){

      int conventionId = rs.getInt("event_id");
      if(!conventions.containsKey(conventionId)){
        Convention convention = new Convention();
        convention.setId(conventionId);
        convention.setUrl(rs.getString("url"));
        convention.setName(rs.getString("name"));

        String date = rs.getTimestamp("start_time")
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString();
        convention.setDays(new String[]{date});
        conventionDays.put(
            conventionId,
            1
        );
        conventions.put(conventionId, convention);
      }
      else {
        conventionDays.put(conventionId, conventionDays.get(conventionId) + 1);
      }
    }

    logger.info("Found conventions");

    for(Integer conventionId: conventionDays.keySet()){
      int days = conventionDays.get(conventionId);
      if(days > 1){
        conventions.get(conventionId).addDays(days - 1);
      }
    }
    return conventions;
  }
  public void insertConventions(Convention[] conventions, Connection conn)
    throws Exception {
    EventRepository eventRepository = new EventRepository();
    EventTimeRepository eventTimeRepository = new EventTimeRepository();
    for (Convention convention : conventions) {
      logger.debug(convention.getName());

      int eventId = eventRepository.getEvent(
        convention.getName(),
        convention.getUrl(),
        conn
      );
      if (eventId == -1) {
        String query =
          "INSERT INTO events (url, name, is_convention) VALUES(?, ?, true) returning id";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.clearBatch();
        insert.setString(1, convention.getUrl());
        insert.setString(2, convention.getName());
        ResultSet rs = insert.executeQuery();
        rs.next();
        convention.setId(rs.getInt(1));
      } else {
        convention.setId(eventId);
      }

      // Date values should be formated similar to the following date: 3/22/2025
      for (String date : convention.getDays()) {
        String[] data = date.split("/");

        LocalDate localDate = LocalDate.of(
          Integer.parseInt(data[2]),
          Integer.parseInt(data[0]),
          Integer.parseInt(data[1])
        );
        eventTimeRepository.setEventDate(convention.getId(), localDate, conn);
      }
    }
  }
}
