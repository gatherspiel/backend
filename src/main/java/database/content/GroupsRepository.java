package database.content;

import app.groups.Event;
import app.groups.EventLocation;
import app.groups.Group;
import app.result.error.StackTraceShortener;
import app.result.error.group.DuplicateGroupNameError;
import app.result.error.group.GroupNotFoundError;
import app.result.error.group.GroupUpdateError;
import app.users.User;
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
      if (!urlsInDb.contains(group.getUrl())) {
        urlsInDb.add(group.getUrl());

        String query =
          "INSERT INTO groups (name, url, description) VALUES(?,?,?) returning id";

        PreparedStatement insert = conn.prepareStatement(query);
        insert.setString(1, group.getName());
        insert.setString(2, group.getUrl());
        insert.setString(3, group.getDescription());

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
    select.setString(1, group.getUrl());

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
          INSERT INTO groups (name, url, description,is_hidden, game_type_tags,image_path)
          VALUES(?,?,?,?,?,?)
          returning id;
        """;

      PreparedStatement groupInsert = conn.prepareStatement(groupInsertQuery);
      groupInsert.setString(1, groupToInsert.getName());
      groupInsert.setString(2, groupToInsert.getUrl());
      groupInsert.setString(3, groupToInsert.getDescription());
      groupInsert.setBoolean(4, !groupAdmin.isSiteAdmin());
      groupInsert.setArray(5, conn.createArrayOf("game_type_tag",groupToInsert.getGameTypeTags()));
      groupInsert.setString(6, groupToInsert.getImageFilePath());

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

  public Optional<Group> getGroupWithOneTimeEvents(int groupId) throws Exception{
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
          event.setDescription(rs.getString("eventDescription"));

          Timestamp startTime = rs.getTimestamp("start_time");
          if(startTime !=null){
            event.setStartTime(startTime.toLocalDateTime().toLocalTime());
            event.setEndTime(rs.getTimestamp("end_time").toLocalDateTime().toLocalTime());
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
          return Optional.of(group);
        }
      }
    } catch(Exception e){
      logger.error("Error inserting group");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }

  public void setGroupToVisible(int groupId) throws Exception{
    String updateQuery = "UPDATE groups SET is_hidden = FALSE WHERE id = ?";
    try {
      PreparedStatement update = conn.prepareStatement(updateQuery);
      update.setInt(1, groupId);
      update.executeUpdate();
    } catch(Exception e){
      logger.error("Failed to set group to visible");
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }

  public void updateGroup(Group groupToUpdate) throws Exception{

    int updatedRows;
    try {
      String updateQuery =    """
             UPDATE groups
              SET name = ?,
              url = ?,
              description = ?,
              game_type_tags = ?,
              image_path = ?
             WHERE id = ?
          """;

      PreparedStatement update = conn.prepareStatement(updateQuery);
      update.setString(1, groupToUpdate.getName());
      update.setString(2, groupToUpdate.getUrl());
      update.setString(3, groupToUpdate.getDescription());
      update.setArray(4, conn.createArrayOf("game_type_tag",groupToUpdate.getGameTypeTags()));
      update.setString(5,groupToUpdate.getImageFilePath());
      update.setInt(6, groupToUpdate.getId());

      updatedRows = update.executeUpdate();


    } catch (Exception e){
      logger.error("Failed to update group");
      GroupUpdateError ex = new GroupUpdateError(e.getMessage());
      ex.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw ex;
    }

    if(updatedRows == 0){
      throw new GroupNotFoundError("Invalid group parameters for update");
    }
  }


  public void deleteGroup(int groupId) throws Exception{
    try {
      String deleteLocationGroupMapQuery = """
        DELETE FROM location_group_map
        WHERE group_id = ?    
      """;

      PreparedStatement deleteLocationMap = conn.prepareStatement(deleteLocationGroupMapQuery);
      deleteLocationMap.setInt(1, groupId);
      deleteLocationMap.executeUpdate();

      String deleteGroupQuery =    """
             DELETE FROM groups
             WHERE id = ?
          """;

      PreparedStatement deleteGroup = conn.prepareStatement(deleteGroupQuery);
      deleteGroup.setInt(1, groupId);
      deleteGroup.executeUpdate();

    } catch (Exception e){
      logger.error("Failed to delete group with id "+groupId);
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }
  }

}
