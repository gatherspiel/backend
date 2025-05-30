package app.utils;

import app.data.Group;
import app.data.auth.User;
import database.utils.ConnectionProvider;
import service.update.GroupEditService;

public class CreateGroupUtils {
  public static Group createGroup(User user, ConnectionProvider testConnectionProvider) throws Exception{

    Group group = new Group();
    GroupEditService groupEditService = new GroupEditService();

    group.setName("group_"+group.getUUID().toString());

    groupEditService.insertGroup(user, group, testConnectionProvider);

    return group;
  }
}
