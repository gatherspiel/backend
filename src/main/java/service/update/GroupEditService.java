package service.update;

import app.data.Group;
import app.data.auth.User;
import database.content.GroupsRepository;
import database.utils.ConnectionProvider;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.util.UUID;

public class GroupEditService {

  Logger logger;
  GroupsRepository groupsRepository;
  public GroupEditService() {
    logger = LogUtils.getLogger();
    groupsRepository = new GroupsRepository();
  }



  public Group editGroup(){

    return null;
  }

  public Group insertGroup(User user, Group groupToInsert, ConnectionProvider connectionProvider) throws Exception{

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
}
