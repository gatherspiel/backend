package database.permissions;

import app.result.error.StackTraceShortener;
import app.result.error.group.EventNotFoundError;
import app.users.data.EventAdminType;
import app.users.data.GroupAdminType;
import app.users.data.User;
import app.result.error.group.GroupNotFoundError;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    ResultSet rs = select.executeQuery();
    if(!rs.next()){
      var message = "Group "+groupId + " not found";
      logger.error(message);
      throw new GroupNotFoundError(message);
    }
    return rs;
  }

  private ResultSet getEventEditorRoles(int eventId) throws Exception {
    String query =  """
                      SELECT * from events
                      FULL JOIN event_admin_data on event_admin_data.event_id = events.id
                      WHERE events.id = ?
                    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, eventId);

    ResultSet rs = select.executeQuery();
    if(!rs.next()){
      var message = "Event "+eventId + " not found";
      logger.error(message);
      throw new EventNotFoundError(message);
    }
    return rs;
  }

  public boolean hasGroupEditorRole(User user, int groupId) throws Exception {
    ResultSet rs = getGroupEditorRoles(groupId);
    while(true){
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("group_admin_level");
      if(user_id == user.getId()) {
        if(groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString()) ||
            groupAdminLevel.equals(GroupAdminType.GROUP_MODERATOR.toString())){
          return true;
        }
      }
      if(!rs.next()){
        return false;
      }
    }
  }

  public boolean hasEventEditorRole(User user, int groupId) throws Exception {
    ResultSet rs = getGroupEditorRoles(groupId);
    while(true){
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("event_admin_level");
      if(user_id == user.getId()) {
        if(groupAdminLevel.equals(EventAdminType.EVENT_MODERATOR.toString())){
          return true;
        }
      }
      if(!rs.next()){
        return false;
      }
    }
  }

  public boolean isGroupAdmin(User user, int groupId) throws Exception {
    ResultSet rs = getGroupEditorRoles(groupId);
    while (true) {
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("group_admin_level");
      if (user_id == user.getId()) {
        if (groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString())) {
          return true;
        }
      }
      if (!rs.next()) {
        return false;
      }
    }
  }

}
