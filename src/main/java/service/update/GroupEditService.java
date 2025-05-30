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

  public boolean canEditGroup(User user, UUID groupId) {

    return false;
  }

  public Group editGroup(){

    return null;
  }

  public Group insertGroup(User user, Group groupToInsert, Connection conn) throws Exception{

    if(!user.isLoggedInUser()){
      var message = "Cannot insert group. User is not logged in";
      logger.error(message);
      throw new Exception(message);
    }

    groupsRepository.insertGroup(user, groupToInsert, conn);
    return null;
  }

  public Group deleteGroup(UUID groupId){
    return null;
  }
}
