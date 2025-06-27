package app.utils;

import app.groups.data.Group;
import app.users.data.User;
import database.utils.ConnectionProvider;
import service.update.GroupEditService;

import java.util.UUID;

public class CreateGroupUtils {

  private static int groupCount = 0;

  public static Group createGroup(User user, ConnectionProvider testConnectionProvider) throws Exception{

    CreateGroupUtils.groupCount++;

    Group group = new Group();
    GroupEditService groupEditService = new GroupEditService();

    group.setId(groupCount);
    group.setName("group-"+ UUID.randomUUID());
    group.setUrl("localhost:1234/"+group.getName());

    return groupEditService.insertGroup(user, group, testConnectionProvider);
  }

  public static Group createGroupObject(){
    Group group = new Group();
    group.setName("group-"+UUID.randomUUID());
    group.setUrl("localhost:1234/"+group.getName());
    return group;
  }
}
