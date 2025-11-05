package service.read;

import app.users.data.User;
import app.users.data.UserGroupMemberData;
import database.user.UserRepository;

import java.sql.Connection;

public class UserGroupMemberService {

  Connection connection;
  User user;
  UserRepository userRepository;

  public UserGroupMemberService(Connection connection, User user, UserRepository userRepository){
    this.connection = connection;
    this.user = user;
    this.userRepository = userRepository;
  }

  public UserGroupMemberData getUserGroupMemberData(){
    /*
    TODO
    -Get groups a user is a part of.
    -Get events a user is a part of.
    -Separate groups and events that a user is member of and one which a user is an organizer of.
     */
    return null;
  }

  public void joinGroup(){

  }

  public void leaveGroup(){

  }

}
