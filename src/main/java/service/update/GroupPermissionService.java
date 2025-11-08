package service.update;

import app.groups.Event;
import app.users.PermissionName;
import app.users.User;
import database.content.GroupsRepository;
import database.permissions.UserPermissionsRepository;

import java.sql.Connection;
import java.util.HashMap;


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

  public HashMap<PermissionName, Boolean> getPermissionsForGroup(int groupId) throws Exception {
    return userPermissionsRepository.getPermissionsForGroup(groupId, user);
  }

  public boolean canEditGroup(int groupId) throws Exception {
    if(user.isSiteAdmin()){
      return groupsRepository.getGroupWithOneTimeEvents(groupId).isPresent();
    }
    return userPermissionsRepository.hasGroupEditorRole(user, groupId);
  }

  public boolean canEditEvent(Event event) throws Exception {

    if(user.isSiteAdmin()){
      return true;
    } else {

      return userPermissionsRepository.hasGroupEditorRole(user,event.getGroupId()) ||
          userPermissionsRepository.hasEventEditorRole(user, event.getId());
    }
  }
}
