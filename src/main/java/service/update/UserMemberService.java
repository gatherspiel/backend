package service.update;

import app.users.data.User;
import app.users.data.UserMemberData;
import database.user.UserRepository;

import java.sql.Connection;

public class UserMemberService {

  Connection connection;
  User user;
  UserRepository userRepository;

  public UserMemberService(Connection connection, User user, UserRepository userRepository){
    this.connection = connection;
    this.user = user;
    this.userRepository = userRepository;
  }

  public UserMemberData getUserMemberData(){
    /*
    TODO
    -Get groups a user is a part of.
    -Get events a user is a part of.
    -Separate groups and events that a user is member of and one which a user is an organizer of.
     */
    return null;
  }

  public void joinGroup(int groupId){
    //TODO: Implement
  }

  public void leaveGroup(int groupId){
    //TODO: Implement

  }

}
