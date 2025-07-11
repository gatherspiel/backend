package database.content;

import app.groups.data.Event;
import app.groups.data.EventLocation;
import app.groups.data.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import service.data.SearchParameterValidator;

public class EventRepository {

  private Connection conn;

  public EventRepository(Connection conn){
    this.conn = conn;
  }

  //TODO: Make this logic use address objects.
  public void addEvents(Group[] groups) throws Exception {
    GroupsRepository groupsRepository = new GroupsRepository(conn);
    LocationsRepository locationsRepository = new LocationsRepository(conn);
    EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);

    for (Group group : groups) {
      int groupId = groupsRepository.getGroupId(group);
      for (Event event : group.events) {
        int eventId = getEventId(event.getName(), group.url);
        if (eventId == -1) {
          if (!SearchParameterValidator.isValidAddress(event.getLocation())) {
            String query =
              "INSERT INTO events(description, name, url) values(?,?,?) returning id";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setString(1, event.getDescription());
            insert.setString(2, event.getName());
            insert.setString(3, group.url);

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
            insert.setString(4, group.url);

            ResultSet rs = insert.executeQuery();
            rs.next();
            eventId = rs.getInt(1);
          }
        }
        updateEventGroupMap(groupId, eventId, conn);
        event.setId(eventId);
        eventTimeRepository.setEventDay(event);
      }
    }
  }


  public Event addEvent(Event event, int groupId) throws Exception{

    LocationsRepository locationsRepository = new LocationsRepository(conn);
    EventTimeRepository eventTimeRepository = new EventTimeRepository(conn);

    int location_id = locationsRepository.insertLocation(
        event.getEventLocation()
    );

    String query =
        "INSERT INTO events(location_id, description, name, url) values(?,?,?,?) returning id";
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, location_id);
    insert.setString(2, event.getDescription());
    insert.setString(3, event.getName());
    insert.setString(4, event.getUrl());

    ResultSet rs = insert.executeQuery();
    rs.next();

    int eventId = rs.getInt(1);

    updateEventGroupMap(groupId, eventId, conn);
    event.setId(eventId);

    if(event.getStartTime() != null && event.getEndTime() != null){
      eventTimeRepository.setEventDate(event.getId(), event.getStartTime(), event.getEndTime());
    } else {
      eventTimeRepository.setEventDay(event);
    }
    return event;
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

  public Event updateEvent(Event event) throws Exception{

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
       url = ?
       WHERE id = ?
      """;
    PreparedStatement update = conn.prepareStatement(query);
    update.setInt(1, location_id);
    update.setString(2, event.getDescription());
    update.setString(3, event.getName());
    update.setString(4, event.getUrl());
    update.setInt(5, event.getId());

    update.executeUpdate();

    if(event.getStartTime() != null && event.getEndTime() != null){
      eventTimeRepository.setEventDate(event.getId(), event.getStartTime(), event.getEndTime());
    }
    return event;
  }

  public Optional<Event> getEvent(int id) throws Exception{
    String query = """
        
        SELECT 
         events.id as eventId,
         events.url,
         events.name as eventName,
         events.description as eventDescription,
         start_time,
         end_time,
         day_of_week,
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
        where events.id = ? 
        
        """;
    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, id);

    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      Event event = new Event();
      event.setUrl(rs.getString("url"));
      event.setName(rs.getString("eventName"));
      event.getDescription(rs.getString("eventDescription"));

      Timestamp start = rs.getTimestamp("start_time");
      Timestamp end = rs.getTimestamp("end_time");

      String groupName = rs.getString("groupName");
      int groupId  = rs.getInt("groupId");
      event.setGroupName(groupName);
      event.setGroupId(groupId);

      if(start != null){
        event.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());

      } else {
        String dayOfWeek = rs.getString("day_of_week");
        var startTime = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(dayOfWeek.toUpperCase())));
        event.setStartTime(startTime);
      }
      if(end != null){
        event.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
      } else {
        event.setEndTime(event.getStartTime().plusHours(1));
      }
      event.setId(rs.getInt("eventId"));

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

  //TODO: Update
  private void deleteEventGroupMapItem(int groupId, int eventId) throws Exception {
    String query =
        "DELETE from event_group_map WHERE group_id = ? AND event_id = ?";
    PreparedStatement delete = conn.prepareStatement(query);
    delete.setInt(1, groupId);
    delete.setInt(2, eventId);

    delete.executeUpdate();


  }


}
