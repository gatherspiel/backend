package service.update.permissions;

import app.data.auth.User;
import database.permissions.EditPermissionsRepository;
import database.utils.ConnectionProvider;

import java.sql.PreparedStatement;

public class EditPermissionService {

  private EditPermissionsRepository editPermissionsRepository;

  public EditPermissionService(){
    this.editPermissionsRepository = new EditPermissionsRepository();
  }
  public void setGroupAdmin(User currentUser, int groupId, ConnectionProvider connectionProvider) throws Exception{

    this.editPermissionsRepository.setGroupAdmin(currentUser, groupId, connectionProvider.getDatabaseConnection());
  }
}
