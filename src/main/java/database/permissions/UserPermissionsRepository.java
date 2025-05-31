package database.permissions;

import app.data.auth.GroupAdminType;
import app.data.auth.User;
import app.data.auth.UserType;
import org.apache.hc.core5.http.HttpException;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserPermissionsRepository
{
  Logger logger;
  public UserPermissionsRepository(){
    logger = LogUtils.getLogger();
  }
  public void setGroupAdmin(User currentUser, int groupId, Connection conn) throws Exception{
    String query = """
            UPDATE group_admin_data
              SET user_id = ?
              WHERE group_id = ?
              AND group_admin_level = cast(? as group_admin_level)
        """;

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, currentUser.getId());
    insert.setInt(2, groupId);
    insert.setString(3, GroupAdminType.GROUP_ADMIN.toString());

    insert.executeUpdate();
  }

  public boolean canEditGroup(User user, int groupId, Connection conn) throws Exception {

    String query =  """
                      SELECT (group_admin_level)
                      FROM group_admin_data 
                      WHERE user_id = ?
                      AND group_id = ?
                    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, user.getId());
    select.setInt(2, groupId);

    ResultSet rs = select.executeQuery();
    if(!rs.next()){
      logger.info("Did not find user "+user.getId() + " permission for group "+groupId);
      return false;
    }
    var groupAdminLevel = rs.getString("group_admin_level");


    return groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString()) ||
        groupAdminLevel.equals(GroupAdminType.GROUP_MODERATOR.toString());
  }
}
