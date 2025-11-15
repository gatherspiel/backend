package database.permissions;

import app.groups.Event;
import app.result.error.StackTraceShortener;
import app.users.*;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserPermissionsRepository
{
  Logger logger;
  Connection conn;
  public UserPermissionsRepository(Connection conn){
    logger = LogUtils.getLogger();
    this.conn = conn;
  }

  public void setGroupAdmin(User userToUpdate, int groupId) throws Exception{
    String query = """
            UPDATE group_admin_data
              SET user_id = ?
              WHERE group_id = ?
              AND group_admin_level = cast(? as group_admin_level)
        """;
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, userToUpdate.getId());
    insert.setInt(2, groupId);
    insert.setString(3, GroupAdminType.GROUP_ADMIN.toString());
    insert.executeUpdate();
  }

  public void addGroupModerator(User currentUser, int groupId) throws Exception{
    String query = """
            INSERT INTO group_admin_data (user_id, group_id, group_admin_level)
          VALUES(?, ?, cast(? as group_admin_level))
        """;
    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, currentUser.getId());
    insert.setInt(2, groupId);
    insert.setString(3, GroupAdminType.GROUP_MODERATOR.toString());
    insert.executeUpdate();
  }

  public boolean canUpdateGroupAdmin(User user, int groupId) throws Exception {
    String query =  """
                      SELECT (group_admin_level)
                      FROM group_admin_data
                      WHERE user_id = ?
                      AND group_id = ?
                      AND group_admin_level = cast(? as group_admin_level)
                    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, user.getId());
    select.setInt(2, groupId);
    select.setString(3, GroupAdminType.GROUP_ADMIN.toString());
    ResultSet rs = select.executeQuery();
    return rs.next();
  }

  private ResultSet getGroupEditorRoles(int groupId) throws Exception {
    String query =  """
                      SELECT * from groups
                      FULL JOIN group_admin_data on group_admin_data.group_id = groups.id
                      WHERE groups.id = ?
                    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, groupId);

    return select.executeQuery();
  }

  public HashMap<PermissionName, Boolean> getPermissionsForGroup(int groupId, User user) throws Exception{
    HashMap<PermissionName, Boolean> permissions = new HashMap<>();
    if(user.isSiteAdmin()){
      permissions.put(PermissionName.USER_CAN_EDIT, true);
    }
    if(user.isLoggedInUser()){
      permissions.put(PermissionName.USER_CAN_UPDATE_GROUP_MEMBERSHIP, true);
    }

    String query =  """
      SELECT group_admin_level from groups
      FULL JOIN group_admin_data on group_admin_data.group_id = groups.id
      WHERE groups.id = ?
      AND user_id = ?
    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, groupId);
    select.setInt(2, user.getId());

    ResultSet rs = select.executeQuery();
    while(rs.next()){
      String adminLevel = rs.getString("group_admin_level");
      if(adminLevel.equals(GroupAdminType.GROUP_ADMIN.toString())||
          adminLevel.equals(GroupAdminType.GROUP_MODERATOR.toString())){
        permissions.put(PermissionName.USER_CAN_EDIT, true);
        permissions.put(PermissionName.USER_IS_MEMBER, true);
      }
      if(adminLevel.equals(GroupAdminType.GROUP_MEMBER.toString())){
        permissions.put(PermissionName.USER_IS_MEMBER, true);
      }
    }
    return permissions;
  }

  public Set<User> getEventRoles(Event event) throws Exception {

    try {
      String query =
        """
          SELECT
            users.id as userId,
            users.email as email,
            events.id as eventId,
            event_admin_level,
            users.image_path,
            username
         from events
          FULL JOIN event_admin_data on event_admin_data.event_id = events.id
          LEFT JOIN users on event_admin_data.user_id = users.id
          WHERE events.id = ?
          AND users.id IS NOT NULL
          AND (rsvp_time IS NULL OR rsvp_time > ?)
        """;

      PreparedStatement select = conn.prepareStatement(query);
      select.setInt(1, event.getId());
      select.setTimestamp(2, Timestamp.valueOf(event.getPrevious()));

      ResultSet rs = select.executeQuery();

      Set<User> editors = new HashSet<>();
      while(rs.next()){

        String eventAdminLevel = rs.getString("event_admin_level");
        User user = new User();

        if(eventAdminLevel != null && eventAdminLevel.equals(EventAdminType.EVENT_MODERATOR.toString())){
          UserData userData = new UserData();
          userData.setImageFilePath(rs.getString("image_path"));
          userData.setUsername(rs.getString("username"));
          user.setUserData(userData);
          user.setUserType(UserType.EVENT_ADMIN);
        }
        user.setId(rs.getInt("userId"));
        user.setEmail(rs.getString("email"));
        editors.add(user);
      }
      return editors;
    } catch(Exception e){
      logger.error("Failed to get event editor roles");
      Exception ex = new RuntimeException(e.getMessage());
      ex.setStackTrace(StackTraceShortener.generateDisplayStackTrace(ex.getStackTrace()));
      throw e;
    }

  }

  public boolean hasGroupEditorRole(User user, int groupId) throws Exception {
    ResultSet rs = getGroupEditorRoles(groupId);
    while(rs.next()){
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("group_admin_level");

      if(user_id == user.getId()) {
        if(groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString()) ||
            groupAdminLevel.equals(GroupAdminType.GROUP_MODERATOR.toString())){
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasEventEditorRole(User user, int eventId) throws Exception {

    try {
      String query =  """
          SELECT
            1
         from events
          FULL JOIN event_admin_data on event_admin_data.event_id = events.id
          LEFT JOIN users on event_admin_data.user_id = users.id
          WHERE events.id = ?
          AND users.id = ?
          AND event_admin_data.event_admin_level = 'event_moderator'
        """;

      PreparedStatement select = conn.prepareStatement(query);
      select.setInt(1, eventId);
      select.setInt(2, user.getId());

      ResultSet rs = select.executeQuery();

      boolean results = rs.next();
      return results;

    } catch (Exception e){
      logger.error(e);
      throw e;
    }
  }

  public boolean isGroupAdmin(User user, int groupId) throws Exception {
    ResultSet rs = getGroupEditorRoles(groupId);
    while (rs.next()) {
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("group_admin_level");
      if (user_id == user.getId()) {
        if (groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString())) {
          return true;
        }
      }
    }
    return false;
  }

}
