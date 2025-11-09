package service.update;

import app.result.error.UnauthorizedError;
import app.users.User;
import app.users.UserMemberData;
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

  public UserMemberData getUserMemberData() throws Exception{
    if(!user.isLoggedInUser()){
      throw new UnauthorizedError("Unauthorized");
    }
    return userRepository.getUserMemberData(user.getId());
  }

  public void joinGroup(int groupId) throws Exception{
    if(!user.isLoggedInUser()){
      throw new UnauthorizedError("Unauthorized");
    }
    userRepository.joinGroup(groupId, user);
  }

  public void leaveGroup(int groupId) throws Exception{
    if(!user.isLoggedInUser()){
      throw new UnauthorizedError("Unauthorized");
    }
    userRepository.leaveGroup(groupId, user);
  }
}
