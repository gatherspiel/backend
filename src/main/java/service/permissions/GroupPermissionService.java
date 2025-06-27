package service.permissions;

import app.users.data.User;
import database.content.GroupsRepository;
import database.permissions.UserPermissionsRepository;

import java.sql.Connection;


public class GroupPermissionService {

  private UserPermissionsRepository userPermissionsRepository;
  private GroupsRepository groupsRepository;
  private Connection conn;
  public GroupPermissionService(Connection conn){
    userPermissionsRepository = new UserPermissionsRepository(conn);
    groupsRepository = new GroupsRepository(conn);
    this.conn = conn;
  }

  public void setGroupAdmin(User currentUser, User userToUpdate, int groupId) throws Exception{
    if(!currentUser.isSiteAdmin() && !userPermissionsRepository.canUpdateGroupAdmin(currentUser, groupId)){
      throw new Exception("User " + currentUser.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.setGroupAdmin(userToUpdate, groupId);
  }

  public void addGroupModerator(User currentUser,User userToUpdate, int groupId) throws Exception{
    if(!canEditGroup(currentUser, groupId)){
      throw new Exception("User " + currentUser.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.addGroupModerator(userToUpdate, groupId);
  }

  public boolean canEditGroup(User user, int groupId) throws Exception {
    if(user.isSiteAdmin()){
      return groupsRepository.getGroup(groupId).isPresent();
    }
    return userPermissionsRepository.hasGroupEditorRole(user, groupId);
  }
}
