package database.content;

import app.data.event.Event;
import app.groups.data.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import service.data.SearchParameterValidator;

public class EventRepository {

  //TODO: Make this logic use address objects.
  public void addEvents(Group[] groups, Connection conn) throws Exception {
    GroupsRepository groupsRepository = new GroupsRepository();
    LocationsRepository locationsRepository = new LocationsRepository();
    EventTimeRepository eventTimeRepository = new EventTimeRepository();

    for (Group group : groups) {
      int groupId = groupsRepository.getGroupId(group, conn);
      for (Event event : group.events) {
        int eventId = getEventId(event.getName(), group.url, conn);
        if (eventId == -1) {
          if (!SearchParameterValidator.isValidAddress(event.getLocation())) {
            String query =
              "INSERT INTO events(description, name, url) values(?,?,?) returning id";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setString(1, event.getSummary());
            insert.setString(2, event.getName());
            insert.setString(3, group.url);

            ResultSet rs = insert.executeQuery();
            rs.next();

            eventId = rs.getInt(1);
          } else {
            int location_id = locationsRepository.insertLocation(
              event.getLocation(),
              conn
            );
            String query =
              "INSERT INTO events(location_id, description, name, url) values(?,?,?,?) returning id";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setInt(1, location_id);
            insert.setString(2, event.getSummary());
            insert.setString(3, event.getName());
            insert.setString(4, group.url);

            ResultSet rs = insert.executeQuery();
            rs.next();
            eventId = rs.getInt(1);
          }
        }
        updateEventGroupMap(groupId, eventId, conn);
        event.setId(eventId);
        eventTimeRepository.setEventDay(event, conn);
      }
    }
  }


  public Event addEvent(Event event, int groupId, Connection conn) throws Exception{

    LocationsRepository locationsRepository = new LocationsRepository();
    EventTimeRepository eventTimeRepository = new EventTimeRepository();

    //TODO: Update insertLocation to retrieve the existing location id if it already exists.
    int location_id = locationsRepository.insertLocation(
        event.getEventLocation(),
        conn
    );

    String query =
        "INSERT INTO events(location_id, description, name, url) values(?,?,?,?) returning id";
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, location_id);
    insert.setString(2, event.getSummary());
    insert.setString(3, event.getName());
    insert.setString(4, event.getUrl());

    ResultSet rs = insert.executeQuery();
    rs.next();

    int eventId = rs.getInt(1);

    updateEventGroupMap(groupId, eventId, conn);
    event.setId(eventId);
    eventTimeRepository.setEventDay(event, conn);
    return event;
  }

  public void deleteEvent(Event event, int groupId, Connection conn) throws Exception {
    EventTimeRepository eventTimeRepository = new EventTimeRepository();
    eventTimeRepository.deleteEventTimeInfo(event, conn);

    deleteEventGroupMapItem(groupId, event.getLocation(), conn);

    String query =
        "DELETE FROM events where id = ?";
    PreparedStatement statement = conn.prepareStatement(query);
    statement.setInt(1, event.getId());
    statement.executeUpdate();
  }


    public Event updateEvent(Event event, int groupId, Connection conn) throws Exception{

    LocationsRepository locationsRepository = new LocationsRepository();
    EventTimeRepository eventTimeRepository = new EventTimeRepository();

    //TODO: Update insertLocation to retrieve the existing location id if it already exists.
    int location_id = locationsRepository.insertLocation(
        event.getLocation(),
        conn
    );

    String query =
        "UPDATE events(location_id, description, name, url) values(?,?,?,?)";
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, location_id);
    insert.setString(2, event.getSummary());
    insert.setString(3, event.getName());
    insert.setString(4, event.getUrl());

    ResultSet rs = insert.executeQuery();
    rs.next();

    int eventId = rs.getInt(1);

    updateEventGroupMap(groupId, eventId, conn);
    event.setId(eventId);
    eventTimeRepository.setEventDay(event, conn);
    return event;
  }


  /*
    TODO
      -Make sure database join is correct
      -Add date column.
   */
  public Optional<Event> getEvent(int id, Connection conn) throws Exception{


    String query = """
        
        SELECT * from events
        LEFT JOIN event_time on event_time.event_id = events.id
        LEFT JOIN  locations on events.location_id = locations.id
        where id = ? 
        
        """;
    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, id);

    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      Event event = new Event();
      event.setUrl(rs.getString("url"));
      event.setName(rs.getString("name"));
      event.setSummary(rs.getString("summary"));
      event.setDay(rs.getString("day_of_week"));
      return Optional.of(event);
    }

    return Optional.empty();
  }

  public int getEventId(String eventTitle, String url, Connection conn)
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
    int locationId,
    Connection conn
  )
    throws Exception {
    /*TODO: Add unique index to enable an ON CONFLICT DO NOTHING clause on the insert query instead of a
        select query.*/
    String exists =
      "SELECT * from event_group_map where group_id=? AND event_id=?";
    PreparedStatement select = conn.prepareStatement(exists);
    select.setInt(1, groupId);
    select.setInt(2, locationId);
    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      return;
    }
    String query =
      "INSERT INTO event_group_map(group_id, event_id) values(?,?)";
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, groupId);
    insert.setInt(2, locationId);
    insert.executeUpdate();
  }

  //TODO: Update
  private void deleteEventGroupMapItem(int groupId, String location, Connection conn) {

  }


}
