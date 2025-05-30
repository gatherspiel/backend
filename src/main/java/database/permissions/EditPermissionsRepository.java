package database.permissions;

import app.data.auth.User;
import database.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EditPermissionsRepository
{
  public void setGroupAdmin(User currentUser, int groupId, Connection conn) throws Exception{
    String query = """
          DO $$
          
          BEGIN
            DELETE FROM group_admin_data
              WHERE group_id = ?
              AND group_admin_level = 'admin'
          
            INSERT_INTO group_admin_data (user_id, group_id, group_admin_level)
            VALUES(?, ?, 'admin'
          END $$  
        """;

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, groupId);
    insert.setInt(2, currentUser.getId());
    insert.setInt(3, groupId);
    insert.executeUpdate();
  }
}
