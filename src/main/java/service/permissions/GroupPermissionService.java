package service.permissions;

import app.users.data.User;
import database.content.GroupsRepository;
import database.permissions.UserPermissionsRepository;

import java.sql.Connection;


public class GroupPermissionService {

  private UserPermissionsRepository userPermissionsRepository;
  private GroupsRepository groupsRepository;
  private Connection conn;
  private User user;
  public GroupPermissionService(Connection conn, User user){
    userPermissionsRepository = new UserPermissionsRepository(conn);
    groupsRepository = new GroupsRepository(conn);
    this.conn = conn;
    this.user = user;
  }

  public void setGroupAdmin(User userToUpdate, int groupId) throws Exception{
    if(!user.isSiteAdmin() && !userPermissionsRepository.canUpdateGroupAdmin(user, groupId)){
      throw new Exception("User " + user.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.setGroupAdmin(userToUpdate, groupId);
  }

  public void addGroupModerator(User userToUpdate, int groupId) throws Exception{
    if(!canEditGroup(groupId)){
      throw new Exception("User " + user.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.addGroupModerator(userToUpdate, groupId);
  }

  public boolean canEditGroup( int groupId) throws Exception {
    if(user.isSiteAdmin()){
      return groupsRepository.getGroup(groupId).isPresent();
    }
    return userPermissionsRepository.hasGroupEditorRole(user, groupId);
  }
}
