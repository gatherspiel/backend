package service.update;

import app.data.Group;
import app.data.auth.User;
import app.result.error.InvalidGroupRequestError;
import app.result.error.PermissionError;
import database.content.GroupsRepository;
import database.permissions.UserPermissionsRepository;
import database.utils.ConnectionProvider;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.util.UUID;

public class GroupEditService {

  Logger logger;
  GroupsRepository groupsRepository;
  UserPermissionsRepository userPermissionsRepository;
  public GroupEditService() {
    logger = LogUtils.getLogger();
    groupsRepository = new GroupsRepository();
    userPermissionsRepository = new UserPermissionsRepository();
  }


  public void editGroup(User user, Group groupToUpdate, ConnectionProvider connectionProvider) throws Exception{
    validateGroupData(groupToUpdate);

    if(!userPermissionsRepository.canEditGroup(user, groupToUpdate.getId(), connectionProvider.getDatabaseConnection()) && !user.isSiteAdmin())  {
      throw new PermissionError("User does not have permissions to edit group: " + groupToUpdate.getName());
    }
    System.out.println("User can edit group");
    groupsRepository.updateGroup(groupToUpdate, connectionProvider.getDatabaseConnection());
  }

  public Group insertGroup(User user, Group groupToInsert, ConnectionProvider connectionProvider) throws Exception{

    validateGroupData(groupToInsert);
    if(!user.isLoggedInUser()){
      var message = "Cannot insert group. User is not logged in";
      logger.error(message);
      throw new Exception(message);
    }

    return groupsRepository.insertGroup(user, groupToInsert, connectionProvider.getDatabaseConnection());
  }

  public Group deleteGroup(UUID groupId){
    return null;
  }

  private void validateGroupData(Group group) throws Exception{

    if(group.getName().contains("_")){
      var message = "Group name cannot have _ characters";
      logger.info(message);
      throw new Exception(message);
    }

    if(group.getId() <=0) {
      throw new InvalidGroupRequestError("Invalid group id: "+group.getId());
    }
  }
}
