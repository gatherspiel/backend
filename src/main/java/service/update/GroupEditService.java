package service.update;

import app.data.Group;
import app.data.auth.User;

import java.util.UUID;

public class GroupEditService {



  public boolean canEditGroup(User user, UUID groupId) {

    return false;
  }

  public Group editGroup(){

    return null;
  }

  public Group createGroup(Group groupToCreate){
      /*
    TODO: Add logic here

    If a user has permissions to edit or the group, edit the group and return the modified group.
    If a user does not have permission to edit or delete the group, return an error without modifying the group.
   */
    return null;
  }

  public Group deleteGroup(UUID groupId){
    return null;
  }
}
