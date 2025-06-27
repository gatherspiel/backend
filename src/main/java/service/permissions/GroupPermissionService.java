package service.permissions;

import app.data.auth.User;
import database.content.GroupsRepository;
import database.permissions.UserPermissionsRepository;
import database.utils.ConnectionProvider;

import java.sql.Connection;


/*
 TODO: Consider creating a permission repository class for each user type and passing it as a constructor parameter
 */
public class GroupPermissionService {

  private UserPermissionsRepository userPermissionsRepository;
  private GroupsRepository groupsRepository;
  public GroupPermissionService(){
    userPermissionsRepository = new UserPermissionsRepository();
    groupsRepository = new GroupsRepository();
  }

  public void setGroupAdmin(User currentUser, User userToUpdate, int groupId, Connection connection) throws Exception{
    if(!currentUser.isSiteAdmin() && !userPermissionsRepository.canUpdateGroupAdmin(currentUser, groupId, connection)){
      throw new Exception("User " + currentUser.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.setGroupAdmin(userToUpdate, groupId, connection);
  }

  public void addGroupModerator(User currentUser,User userToUpdate, int groupId, Connection connection) throws Exception{
    if(!canEditGroup(currentUser, groupId, connection)){
      throw new Exception("User " + currentUser.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.addGroupModerator(userToUpdate, groupId, connection);
  }

  public boolean canEditGroup(User user, int groupId, Connection connection) throws Exception {
    if(user.isSiteAdmin()){
      return groupsRepository.getGroup(groupId, connection).isPresent();
    }
    return userPermissionsRepository.hasGroupEditorRole(user, groupId, connection);
  }
}
