package service.update.permissions;

import app.data.auth.User;
import database.permissions.UserPermissionsRepository;
import database.utils.ConnectionProvider;

public class GroupPermissionService {

  private UserPermissionsRepository userPermissionsRepository;

  public GroupPermissionService(){
    this.userPermissionsRepository = new UserPermissionsRepository();
  }
  public void setGroupAdmin(User currentUser, int groupId, ConnectionProvider connectionProvider) throws Exception{

    this.userPermissionsRepository.setGroupAdmin(currentUser, groupId, connectionProvider.getDatabaseConnection());
  }

  public boolean canEditGroup(User user, int groupId, ConnectionProvider connectionProvider) throws Exception {

    if(user.isSiteAdmin()){
      return true;
    }
    return this.userPermissionsRepository.canEditGroup(user, groupId, connectionProvider.getDatabaseConnection());
  }
}
