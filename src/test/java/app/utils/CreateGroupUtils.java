package app.utils;

import app.data.Group;
import app.data.auth.User;
import database.utils.ConnectionProvider;
import service.update.GroupEditService;

public class CreateGroupUtils {
  public static Group createGroup(User user, ConnectionProvider testConnectionProvider) throws Exception{

    Group group = new Group();
    GroupEditService groupEditService = new GroupEditService();

    group.setName("group-"+group.getUUID().toString());
    group.setUrl("localhost:1234/"+group.getName());

    return groupEditService.insertGroup(user, group, testConnectionProvider);
  }
}
