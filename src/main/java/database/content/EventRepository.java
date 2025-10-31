package database.content;

import app.groups.data.Event;
import app.groups.data.EventLocation;
import app.groups.data.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Optional;

import app.result.error.StackTraceShortener;
import app.result.error.group.DuplicateEventError;
import app.result.error.group.InvalidEventParameterError;
import app.users.data.EventAdminType;
import app.users.data.User;
import database.user.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.data.SearchParameterValidator;
import service.update.GroupEventRsvpData;

public class EventRepository {

  private Connection conn;

  private static final Logger logger = LogManager.getLogger(
      EventRepository.class
  );

  public EventRepository(Connection conn){
    this.conn = conn;
  }

  public void addEvents(Group[] groups) throws Exception {
    GroupsRepository groupsRepository = new GroupsRepository(conn);
    LocationsRepository locationsRepository = new LocationsRepository(conn);
    EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);

    for (Group group : groups) {
      int groupId = groupsRepository.getGroupId(group);

      for (Event event : group.getEvents()) {

        if(event.getIsRecurring()){
          continue;
        }
        int eventId = getEventId(event.getName(), group.getUrl());
        if (eventId == -1) {
          if (!SearchParameterValidator.isValidAddress(event.getLocation())) {
            String query =
              "INSERT INTO events(description, name, url) values(?,?,?) returning id";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setString(1, event.getDescription());
            insert.setString(2, event.getName());
            insert.setString(3, group.getUrl());

            ResultSet rs = insert.executeQuery();
            rs.next();

            eventId = rs.getInt(1);
          } else {
            int location_id = locationsRepository.insertLocation(
              event.getLocation()
            );
            String query =
              "INSERT INTO events(location_id, description, name, url) values(?,?,?,?) returning id";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setInt(1, location_id);
            insert.setString(2, event.getDescription());
            insert.setString(3, event.getName());
            insert.setString(4, group.getUrl());

            ResultSet rs = insert.executeQuery();
            rs.next();
            eventId = rs.getInt(1);
          }
        }
        updateEventGroupMap(groupId, eventId, conn);
        event.setId(eventId);

        eventTimeRepository.createWeeklyRecurrence(event);
      }
    }
  }

  public Event createEvent(Event event, int groupId) throws Exception{

    try {
      LocationsRepository locationsRepository = new LocationsRepository(conn);
      EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);

      int location_id = locationsRepository.insertLocation(
          event.getEventLocation()
      );

      String query =
          "INSERT INTO events(location_id, description, name, url,image_path) values(?,?,?,?,?) returning id";
      PreparedStatement insert = conn.prepareStatement(query);
      insert.setInt(1, location_id);
      insert.setString(2, event.getDescription());
      insert.setString(3, event.getName());
      insert.setString(4, event.getUrl());
      insert.setString(5, event.getImageFilePath());

      ResultSet rs = insert.executeQuery();
      rs.next();

      int eventId = rs.getInt(1);

      updateEventGroupMap(groupId, eventId, conn);
      event.setId(eventId);

      if (!event.getIsRecurring()) {
        if (event.getStartTime() != null && event.getEndTime() != null) {
          eventTimeRepository.createEventDate(
              event.getId(),
              event.getStartDate().atTime(event.getStartTime()),
              event.getEndDate().atTime(event.getEndTime()));
        } else {
          eventTimeRepository.setEventDay(event);
        }
      } else {
        eventTimeRepository.createWeeklyRecurrence(event);
      }

      return event;
    } catch(Exception e){
      logger.error("Query error in createEvent");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      e.printStackTrace();
      if(e.getMessage().contains("duplicate key value violates unique constraint")){
        throw new DuplicateEventError("Cannot create two events with the same name and url");
      }
      throw(e);
    }
  }

  public void deleteEvent(int eventId, int groupId) throws Exception {

    EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);
    eventTimeRepository.deleteEventTimeInfo(eventId);

    deleteEventGroupMapItem(groupId, eventId);

    String query =
        "DELETE FROM events where id = ?";
    PreparedStatement statement = conn.prepareStatement(query);
    statement.setInt(1, eventId);
    statement.executeUpdate();
  }

  public void deleteAllEventsInGroup(int groupId) throws Exception {

    EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);

    eventTimeRepository.deleteEventTimeInfoForGroup(groupId);
    eventTimeRepository.deleteOrphanedTimeData();

    deleteEventGroupMapItems(groupId);

    String query =
      """
        DELETE from events where id NOT IN (
          SELECT event_id from event_group_map
        ) AND events.is_convention IS NOT true
      """;
    PreparedStatement delete = conn.prepareStatement(query);
    delete.executeUpdate();
  }

  public Event updateEvent(Event event) throws Exception{

    try {
      LocationsRepository locationsRepository = new LocationsRepository(conn);
      EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);

      int location_id = locationsRepository.insertLocation(
          event.getEventLocation()
      );

      String query =
          """
               UPDATE events SET
               location_id = ?,
               description = ?,
               name = ?,
               url = ?,
               image_path = ?
               WHERE id = ?
              """;
      PreparedStatement update = conn.prepareStatement(query);
      update.setInt(1, location_id);
      update.setString(2, event.getDescription());
      update.setString(3, event.getName());
      update.setString(4, event.getUrl());
      update.setString(5, event.getImageFilePath());
      update.setInt(6, event.getId());

      update.executeUpdate();

      if (event.getStartTime() != null && event.getEndTime() != null && !event.getIsRecurring()) {
        eventTimeRepository.updateEventDate(
            event.getId(),
            event.getStartDate().atTime(event.getStartTime()),
            event.getEndDate().atTime(event.getEndTime()));
      }
      if (event.getIsRecurring()) {
        eventTimeRepository.updateWeeklyRecurrence(event);
      }
      return event;
    } catch (Exception e){
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      e.printStackTrace();
      if(e.getMessage().contains("duplicate key value violates unique constraint")){
        throw new DuplicateEventError("Cannot create two events with the same name and url");
      }
      throw(e);
    }
  }

  public Optional<Event> getEvent(int id) throws Exception{

    String query = """        
        SELECT
         events.id as eventId,
         events.url,
         events.name as eventName,
         events.image_path,
         events.description as eventDescription,
         event_time.start_time as oneTimeEventStartTime,
         weekly_event_time.start_time as recurringEventStartTime,
         event_time.end_time as oneTimeEventEndTime,
         weekly_event_time.end_time as recurringEventEndTime,
         COALESCE(event_time.day_of_week,weekly_event_time.day_of_week) as event_day,
         groups.id as groupId,
         groups.name as groupName,
         locations.city as city,
         locations.zip_code as zip_code,
         locations.state as state,
         locations.street_address as street_address
         from events
        LEFT JOIN event_time on event_time.event_id = events.id
        LEFT JOIN locations on events.location_id = locations.id
        LEFT JOIN event_group_map on events.id = event_group_map.event_id
        LEFT JOIN groups on event_group_map.group_id = groups.id
        LEFT JOIN weekly_event_time on weekly_event_time.event_id = events.id
        WHERE events.id = ?
        """;
    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, id);
    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      Event event = new Event();
      event.setId(id);
      event.setUrl(rs.getString("url"));
      event.setName(rs.getString("eventName"));
      event.setDescription(rs.getString("eventDescription"));
      event.setImageFilePath(rs.getString("image_path"));

      String groupName = rs.getString("groupName");
      int groupId  = rs.getInt("groupId");
      event.setGroupName(groupName);
      event.setGroupId(groupId);

      Timestamp oneTimeEventStartTime = rs.getTimestamp("oneTimeEventStartTime");
      Timestamp oneTimeEventEndTime = rs.getTimestamp("oneTimeEventEndTime");
      if(oneTimeEventStartTime != null){
        var startDateTime = oneTimeEventStartTime.toLocalDateTime();
        event.setStartTime(startDateTime.toLocalTime());
        event.setStartDate(startDateTime.toLocalDate());
      }
      if(oneTimeEventEndTime != null){
        var endDateTime = oneTimeEventEndTime.toLocalDateTime();
        event.setEndTime(endDateTime.toLocalTime());
        event.setEndDate(endDateTime.toLocalDate());
      } else {
        event.setEndTime(event.getStartTime().plusHours(1));
      }

      Timestamp recurringEventStartTime = rs.getTimestamp("recurringEventStartTime");
      Timestamp recurringEventEndTime = rs.getTimestamp("recurringEventEndTime");
      if(recurringEventStartTime != null && recurringEventEndTime != null){
        event.setStartTime(recurringEventStartTime.toLocalDateTime().toLocalTime());
        event.setEndTime(recurringEventEndTime.toLocalDateTime().toLocalTime());
        event.setDay(rs.getString("event_day"));
        event.setIsRecurring(true);
      }

      String rsvpQueryStr = """
          SELECT COUNT(user_id) as rsvp_count,event_id
          FROM event_rsvp
          WHERE event_id = ?
          AND rsvp_time > ?
              GROUP BY event_id
          """;

      PreparedStatement rsvpQuery = conn.prepareStatement(rsvpQueryStr);
      rsvpQuery.setInt(1,id);
      rsvpQuery.setTimestamp(2, Timestamp.valueOf(event.getPrevious()));
      ResultSet rs2 = rsvpQuery.executeQuery();
      if(rs2.next()){
        event.setRsvpCount(rs2.getInt("rsvp_count"));
      }

      EventLocation eventLocation = new EventLocation();
      eventLocation.setCity(rs.getString("city"));
      eventLocation.setZipCode(rs.getInt("zip_code"));
      eventLocation.setState(rs.getString("state"));
      eventLocation.setStreetAddress(rs.getString("street_address"));
      event.setEventLocation(eventLocation);
      return Optional.of(event);
    }

    return Optional.empty();
  }

  public int getEventId(String eventTitle, String url)
    throws Exception {
    String query = "SELECT * from events where name = ? and url=?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, eventTitle);
    select.setString(2, url);
    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      return rs.getInt(1);
    }
    return -1;
  }

  public void addEventModerator(Event event, User moderatorToAdd) throws Exception{

    if(moderatorToAdd.getId() == 0){
      UserRepository userRepository = new UserRepository(conn);
      User userFromDb = userRepository.getActiveUserFromEmail(moderatorToAdd.getEmail());
      moderatorToAdd.setId(userFromDb.getId());
    }
    String query = """
      INSERT into event_admin_data (user_id, event_id, event_admin_level)
      VALUES (?,?,cast(? as event_admin_level))
    """;

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, moderatorToAdd.getId());
    insert.setInt(2, event.getId());
    insert.setString(3, EventAdminType.EVENT_MODERATOR.toString());

    insert.executeUpdate();
  }

  public void clearEventModerators(Event event) throws Exception{

    String query = """
      DELETE from event_admin_data
      WHERE  event_id = ?
    """;

    PreparedStatement delete = conn.prepareStatement(query);
    delete.setInt(1, event.getId());
    delete.executeUpdate();
  }

  public void removeEventModerator(Event event, User moderatorToRemove) throws Exception{

    String query = """
      DELETE from event_admin_data
      WHERE user_id = ?
      AND event_id = ?
    """;

    PreparedStatement delete = conn.prepareStatement(query);
    delete.setInt(1, moderatorToRemove.getId());
    delete.setInt(2, event.getId());
    delete.executeUpdate();
  }

  public void rsvpToEvent(int eventId, User user) throws Exception{

    try {
      String query = """
        INSERT INTO event_rsvp(event_id,user_id,rsvp_time) values(?, ?, ?)
        ON CONFLICT(event_id,user_id) DO UPDATE set rsvp_time = current_timestamp
        """;
      PreparedStatement insert = conn.prepareStatement(query);
      insert.setInt(1, eventId);
      insert.setInt(2, user.getId());
      insert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
      insert.executeUpdate();
    } catch(Exception e){
      if(e.getMessage().contains("duplicate key value")){
        throw new InvalidEventParameterError("User already has RSVP for event");
      }
      if(e.getMessage().contains("insert or update on table \"event_rsvp\" violates foreign key constraint \"event_rsvp_event_id_fkey\"")){
        throw new InvalidEventParameterError("Event does not exist");
      }
      e.printStackTrace();
      throw e;
    }
  }

  public void removeEventRsvp(int eventId, User user) throws Exception{

    try {
      String query = "DELETE from  event_rsvp WHERE event_id = ? and user_id = ?";
      PreparedStatement delete = conn.prepareStatement(query);
      delete.setInt(1, eventId);
      delete.setInt(2, user.getId());


      int update = delete.executeUpdate();
      if(update == 0){
        throw new InvalidEventParameterError("User does not have rsvp for event");
      }
    } catch(Exception e){
      logger.error(e.getMessage());
      throw e;
    }

  }

  public GroupEventRsvpData getEventRsvpsForGroup(int groupId, User user) throws Exception{

    String query = """
         SELECT
            event_group_map.event_id,
            COUNT(rsvp_a.user_id),
            rsvp_b.user_id
          from event_group_map
              LEFT JOIN event_rsvp as rsvp_a on rsvp_a.event_id = event_group_map.event_id
              LEFT JOIN event_rsvp as rsvp_b on rsvp_b.event_id = event_group_map.event_id  AND rsvp_b.user_id = ?
        
        WHERE group_id =?
        
        GROUP BY event_group_map.event_id,group_id,rsvp_b.user_id
        ORDER BY event_group_map.event_id
      """;

    PreparedStatement select = conn.prepareStatement(query);

    select.setInt(1, user.getId());
    select.setInt(2, groupId);
    ResultSet rs = select.executeQuery();

    HashMap<Integer,AbstractMap.SimpleEntry<Integer,Boolean>> rsvpData = new HashMap<>();
    GroupEventRsvpData groupEventRsvpData = new GroupEventRsvpData();
    while(rs.next()){
      AbstractMap.SimpleEntry<Integer,Boolean> eventRsvpData  = new AbstractMap.SimpleEntry<>(
            rs.getInt("count"),
            rs.getInt("user_id") > 0
        );
      rsvpData.put(rs.getInt("event_id"), eventRsvpData);
    }


    String moderatorQuery = """
         SELECT
            event_group_map.event_id,
            user_id
          from event_group_map
            JOIN event_admin_data on event_admin_data.event_id = event_group_map.event_id
        WHERE group_id =?

      """;

    PreparedStatement moderatorSelect = conn.prepareStatement(moderatorQuery);

    moderatorSelect.setInt(1, groupId);
    ResultSet moderatorRs = moderatorSelect.executeQuery();

    while(moderatorRs.next()){

      AbstractMap.SimpleEntry<Integer,Boolean> eventRsvpData = rsvpData.get(moderatorRs.getInt("event_id"));

      if(moderatorRs.getInt("user_id") == user.getId()){
         groupEventRsvpData.setUserCanRsvp(false,moderatorRs.getInt("event_id"));
      }

      if(eventRsvpData == null && moderatorRs.getInt("event_id") > 0){
        eventRsvpData = new AbstractMap.SimpleEntry<>(
            moderatorRs.getInt(1),
            moderatorRs.getInt("user_id") == user.getId()
        );
        rsvpData.put(moderatorRs.getInt("event_id"), eventRsvpData);
      } else {
        eventRsvpData = new AbstractMap.SimpleEntry<>(
            eventRsvpData.getKey() + 1,
            eventRsvpData.getValue() || moderatorRs.getInt("user_id") == user.getId());
        rsvpData.put(moderatorRs.getInt("event_id"), eventRsvpData);
      }

    }

    groupEventRsvpData.setRsvpData(rsvpData);
    return groupEventRsvpData;
  }

  private void updateEventGroupMap(
    int groupId,
    int eventId,
    Connection conn
  )
    throws Exception {
    /*TODO: Add unique index to enable an ON CONFLICT DO NOTHING clause on the insert query instead of a
        select query.*/
    String exists =
      "SELECT * from event_group_map where group_id=? AND event_id=?";
    PreparedStatement select = conn.prepareStatement(exists);
    select.setInt(1, groupId);
    select.setInt(2, eventId);
    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      return;
    }
    String query =
      "INSERT INTO event_group_map(group_id, event_id) values(?,?)";
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, groupId);
    insert.setInt(2, eventId);
    insert.executeUpdate();
  }

  private void deleteEventGroupMapItems(int groupId) throws Exception {
    String query =
        "DELETE from event_group_map WHERE group_id = ?";
    PreparedStatement delete = conn.prepareStatement(query);
    delete.setInt(1, groupId);
    delete.executeUpdate();
  }

  private void deleteEventGroupMapItem(int groupId, int eventId) throws Exception {
    String query =
        "DELETE from event_group_map WHERE group_id = ? AND event_id = ?";
    PreparedStatement delete = conn.prepareStatement(query);
    delete.setInt(1, groupId);
    delete.setInt(2, eventId);

    delete.executeUpdate();
  }


}
