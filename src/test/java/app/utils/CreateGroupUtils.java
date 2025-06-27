package app.utils;

import app.groups.data.Group;
import app.users.data.User;
import database.utils.ConnectionProvider;
import service.update.GroupEditService;

import java.sql.Connection;
import java.util.UUID;

public class CreateGroupUtils {

  private static int groupCount = 0;

  public static Group createGroup(User user, Connection conn) throws Exception{

    CreateGroupUtils.groupCount++;

    Group group = new Group();
    GroupEditService groupEditService = new GroupEditService(conn);

    group.setId(groupCount);
    group.setName("group-"+ UUID.randomUUID());
    group.setUrl("localhost:1234/"+group.getName());

    return groupEditService.insertGroup(user, group);
  }

  public static Group createGroupObject(){
    Group group = new Group();
    group.setName("group-"+UUID.randomUUID());
    group.setUrl("localhost:1234/"+group.getName());
    return group;
  }
}
