package service.update;

import app.groups.data.Group;
import app.users.data.User;
import app.result.error.InvalidGroupRequestError;
import app.result.error.PermissionError;
import database.content.GroupsRepository;
import database.permissions.UserPermissionsRepository;
import database.utils.ConnectionProvider;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;

public class GroupEditService {

  Logger logger;
  GroupsRepository groupsRepository;
  UserPermissionsRepository userPermissionsRepository;
  Connection connection;
  User user;
  public GroupEditService(Connection connection, User user) {
    logger = LogUtils.getLogger();
    groupsRepository = new GroupsRepository(connection);
    userPermissionsRepository = new UserPermissionsRepository(connection);
    this.connection = connection;
    this.user = user;
  }


  public void editGroup(Group groupToUpdate) throws Exception{

    if(groupToUpdate.getId() <=0) {
      throw new InvalidGroupRequestError("Invalid group id: "+groupToUpdate.getId());
    }
    validateGroupData(groupToUpdate);

    if(!userPermissionsRepository.hasGroupEditorRole(user, groupToUpdate.getId()) && !user.isSiteAdmin())  {
      throw new PermissionError("User does not have permissions to edit group: " + groupToUpdate.getName());
    }
    groupsRepository.updateGroup(groupToUpdate);
  }

  public Group insertGroup(Group groupToInsert) throws Exception{

    validateGroupData(groupToInsert);
    if(!user.isLoggedInUser()){
      var message = "Cannot insert group. User is not logged in";
      logger.error(message);
      throw new Exception(message);
    }

    return groupsRepository.insertGroup(user, groupToInsert);
  }

  public void deleteGroup(int groupId) throws Exception {

    if(!userPermissionsRepository.isGroupAdmin(user, groupId) && !user.isSiteAdmin())  {
      throw new PermissionError("User does not have permissions to delete group: " + groupId);
    }

    groupsRepository.deleteGroup(groupId);
  }

  private void validateGroupData(Group group) throws Exception{

    if(group.getName().contains("_")){
      var message = "Group name cannot have _ characters";
      logger.info(message);
      throw new Exception(message);
    }


  }
}
