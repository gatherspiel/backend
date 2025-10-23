package database.permissions;

import app.result.error.StackTraceShortener;
import app.users.data.EventAdminType;
import app.users.data.GroupAdminType;
import app.users.data.User;
import app.users.data.UserData;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

  public Set<UserData> getEventEditorRoles(int eventId) throws Exception {

    try {
      String query =  """
                      SELECT
                        users.id as userId,
                        events.id as eventId,
                        event_admin_level,
                        users.image_path,
                        username  
                     from events
                      FULL JOIN event_admin_data on event_admin_data.event_id = events.id
                      LEFT JOIN users on event_admin_data.user_id = users.id
                      WHERE events.id = ?
                    """;

      PreparedStatement select = conn.prepareStatement(query);
      select.setInt(1, eventId);

      ResultSet rs = select.executeQuery();

      Set<UserData> editors = new HashSet<>();
      while(rs.next()){
        String eventAdminLevel = rs.getString("event_admin_level");

        if(eventAdminLevel != null && eventAdminLevel.equals(EventAdminType.EVENT_MODERATOR.toString())){
          UserData userData = new UserData();
          userData.setImageFilePath(rs.getString("image_path"));
          userData.setUsername(rs.getString("username"));
          editors.add(userData);
        }
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
