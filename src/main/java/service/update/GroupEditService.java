package service.update;

import app.groups.data.Group;
import app.result.error.StackTraceShortener;
import app.users.data.User;
import app.result.error.group.InvalidGroupRequestError;
import app.result.error.PermissionError;
import database.content.EventRepository;
import database.content.GroupsRepository;
import database.files.ImageRepository;
import database.permissions.UserPermissionsRepository;
import database.user.UserRepository;
import org.apache.logging.log4j.Logger;
import service.EmailService;
import utils.LogUtils;

import java.sql.Connection;

public class GroupEditService {

  Logger logger;
  GroupsRepository groupsRepository;
  UserPermissionsRepository userPermissionsRepository;
  Connection connection;
  User user;
  EmailService emailService;
  public GroupEditService(Connection connection, User user) {
    logger = LogUtils.getLogger();
    groupsRepository = new GroupsRepository(connection);
    userPermissionsRepository = new UserPermissionsRepository(connection);
    this.emailService = new EmailService(user);
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

    //The group has a new image
    if(groupToUpdate.getImage() != null){
      ImageRepository imageRepository = new ImageRepository();
      imageRepository.uploadImage(groupToUpdate.getImage(),groupToUpdate.getImageBucketKey());
    }
    groupsRepository.updateGroup(groupToUpdate);
  }

  public Group insertGroup(Group groupToInsert) throws Exception{
    validateGroupData(groupToInsert);
    if(!user.isLoggedInUser()){
      var message = "Cannot insert group. User is not logged in";
      logger.error(message);
      Exception ex = new Exception(message);
      ex.setStackTrace(StackTraceShortener.generateDisplayStackTrace(ex.getStackTrace()));
      throw ex;
    }

    Group group = groupsRepository.insertGroup(user, groupToInsert);

    if(groupToInsert.getImage() != null){
      ImageRepository imageRepository = new ImageRepository();
      imageRepository.uploadImage(groupToInsert.getImage(),groupToInsert.getImageBucketKey());
    }
    this.emailService.sendGroupCreatedNotification(group);
    return group;
  }

  public void deleteGroup(int groupId) throws Exception {
    if(!userPermissionsRepository.isGroupAdmin(user, groupId) && !user.isSiteAdmin())  {
      throw new PermissionError("User does not have permissions to delete group: " + groupId);
    }

    EventRepository eventRepository = new EventRepository(connection);
    eventRepository.deleteAllEventsInGroup(groupId);

    UserRepository userRepository = new UserRepository(connection);
    userRepository.deleteMemberDataForGroup(groupId);
    groupsRepository.deleteGroup(groupId);
  }

  public void setGroupToVisible(int groupId) throws Exception {
    if(!user.isSiteAdmin())  {
      throw new PermissionError("User does not have permissions to make group visible" + groupId);
    }
    groupsRepository.setGroupToVisible(groupId);

  }

  private void validateGroupData(Group group) throws Exception{
    if(group.getName().contains("_")){
      var message = "Group name cannot have _ characters";
      logger.info(message);
      throw new Exception(message);
    }
  }
}
