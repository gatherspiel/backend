package database.content;

import app.groups.data.Event;
import app.groups.data.EventLocation;
import app.groups.data.Group;
import app.result.error.StackTraceShortener;
import app.result.error.group.DuplicateGroupNameError;
import app.users.data.User;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GroupsRepository {

  private static Logger logger = LogUtils.getLogger();

  private Connection conn;

  public GroupsRepository(Connection conn){
    this.conn = conn;
  }

  public void insertGroups(Group[] groups) throws Exception {
    LocationsRepository locationsRepository = new LocationsRepository(conn);
    Set<String> urlsInDb = getGroupsInDatabase(groups);
    for (Group group : groups) {
      if (!urlsInDb.contains(group.url)) {
        urlsInDb.add(group.url);

        String query =
          "INSERT INTO groups (name, url, description) VALUES(?,?,?) returning id";

        PreparedStatement insert = conn.prepareStatement(query);
        insert.setString(1, group.name);
        insert.setString(2, group.url);
        insert.setString(3, group.description);

        ResultSet rs = insert.executeQuery();
        if (rs.next()) {
          int groupId = rs.getInt(1);
          for (String location : group.getCities()) {
            int locationId = locationsRepository.getLocationIdForCity(
              location.trim()
            );

            String groupLocationQuery =
              "INSERT INTO location_group_map(location_id, group_id) VALUES(?, ?)";
            PreparedStatement groupLocationInsert = conn.prepareStatement(
              groupLocationQuery
            );
            groupLocationInsert.setInt(1, locationId);
            groupLocationInsert.setInt(2, groupId);
            groupLocationInsert.executeUpdate();
          }
        } else {
          logger.error("Error inserting groups");
          throw new Exception();
        }
      }
    }
  }

  // This is a workaround for an issue related to using prepared statements with duplicates.
  // TODO: Use SQL in clause to optimize performance.
  public Set<String> getGroupsInDatabase(Group[] groups)
    throws Exception {
    String query = "SELECT url from groups";

    PreparedStatement select = conn.prepareStatement(query);
    ResultSet rs = select.executeQuery();

    Set<String> urlsInDb = new HashSet<String>();
    while (rs.next()) {
      urlsInDb.add(rs.getString(1));
    }
    return urlsInDb;
  }

  public int getGroupId(Group group) throws Exception {
    String query = "SELECT * from groups where url = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, group.url);

    ResultSet rs = select.executeQuery();
    if (!rs.next()) {
      return -1;
    }
    return rs.getInt(1);
  }

  public Group insertGroup(User groupAdmin, Group groupToInsert) throws Exception{

    int groupId = -1;
    try {
      String groupInsertQuery=
          """
              INSERT INTO groups (name, url, description)
              VALUES(?,?,?)
              returning id;
          """;

      PreparedStatement groupInsert = conn.prepareStatement(groupInsertQuery);
      groupInsert.setString(1, groupToInsert.getName());
      groupInsert.setString(2, groupToInsert.getUrl());
      groupInsert.setString(3, groupToInsert.getDescription());
      ResultSet rs = groupInsert.executeQuery();

      if(!rs.next()){
        throw new Exception("Failed to insert group");
      }
      groupId = rs.getInt(1);
    } catch(Exception e){
      logger.error(e.getMessage());
      if(e.getMessage().contains("already exists")){
        throw new DuplicateGroupNameError("Cannot create multiple groups with the same name");
      }
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw e;
    }


    try {


      String groupPermissionInsertQuery = """
             INSERT INTO group_admin_data (user_id, group_id, group_admin_level)
             VALUES(?, ?, 'group_admin')
          """;
      PreparedStatement groupPermissionInsert = conn.prepareStatement(groupPermissionInsertQuery);
      groupPermissionInsert.setInt(1, groupAdmin.getId());
      groupPermissionInsert.setInt(2, groupId);

      groupPermissionInsert.executeUpdate();
      groupToInsert.setId(groupId);

      logger.info("Created group with name:"+groupToInsert.getName());
      return groupToInsert;
    } catch(Exception e) {
      logger.error("Failed to set user:"+groupAdmin.getEmail() + "with id:" + groupAdmin.getId() + " as group admin");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }

  public Optional<Group> getGroup(int groupId) throws Exception{
    try {
      String query = """
          SELECT 
            groups.name,
            groups.url,
            groups.description,
            events.name as eventName,
            events.description as eventDescription,
            events.url as eventUrl,
            event_time.day_of_week,
            event_time.start_time,
            event_time.end_time,
            events.id as eventId,
            locations.state,
            locations.street_address,
            locations.zip_code,
            locations.city as city,
            locs.city as groupCity
            from groups 
          LEFT JOIN event_group_map on groups.id = event_group_map.group_id
          LEFT JOIN events on event_group_map.event_id = events.id
          LEFT JOIN event_time on event_time.event_id = events.id
          LEFT JOIN locations on events.location_id = locations.id
          LEFT JOIN location_group_map on groups.id = location_group_map.group_id
          LEFT JOIN locations as locs on location_group_map.location_id = locs.id
          WHERE groups.id = ?
        """;
      PreparedStatement statement = conn.prepareStatement(query);
      statement.setInt(1, groupId);

      ResultSet rs = statement.executeQuery();
      if(!rs.next()) {
        return Optional.empty();
      }

      Group group = new Group();
      group.setId(groupId);
      group.setName(rs.getString("name"));
      group.setUrl(rs.getString("url"));
      group.setDescription(rs.getString("description"));

      ArrayList<Event> events = new ArrayList<>();
      while(true){

        if(rs.getString("eventUrl") != null){
          Event event = new Event();
          event.setId(rs.getInt("eventId"));
          event.setUrl(rs.getString("eventUrl"));
          event.setName(rs.getString("eventName"));
          event.getDescription(rs.getString("eventDescription"));

          Timestamp startTime = rs.getTimestamp("start_time");
          if(startTime !=null){
            event.setStartTime(startTime.toLocalDateTime());
            event.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
          }

          EventLocation eventLocation = new EventLocation();

          String state = rs.getString("state");
          if(state != null){
            eventLocation.setState(state);
          }
          eventLocation.setStreetAddress(rs.getString("street_address"));
          eventLocation.setZipCode(rs.getInt("zip_code"));
          eventLocation.setCity(rs.getString("city"));

          event.setEventLocation(eventLocation);
          events.add(event);
        }


        if(!rs.next()){
          group.setEvents(events.toArray(new Event[0]));
          return Optional.of(group);
        }
      }
    } catch(Exception e){
      logger.error("Error inserting group");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }

  public void updateGroup(Group groupToUpdate) throws Exception{
    try {
      String updateQuery =    """
             UPDATE groups
              SET name = ?,
              url = ?,
              description = ?
             WHERE id = ?    
          """;

      PreparedStatement update = conn.prepareStatement(updateQuery);
      update.setString(1, groupToUpdate.getName());
      update.setString(2, groupToUpdate.getUrl());
      update.setString(3, groupToUpdate.getDescription());
      update.setInt(4, groupToUpdate.getId());

      update.executeUpdate();

    } catch (Exception e){
      logger.error("Failed to update group");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }


  public void deleteGroup(int groupId) throws Exception{
    try {
      String deleteQuery =    """
             DELETE FROM groups
             WHERE id = ?
          """;

      PreparedStatement delete = conn.prepareStatement(deleteQuery);
      delete.setInt(1, groupId);
      delete.executeUpdate();

    } catch (Exception e){
      logger.error("Failed to update group");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }

}
