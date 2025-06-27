package app.utils;

import app.users.data.User;
import app.users.data.UserType;

public class CreateUserUtils {

  public static int id = 0;

  public static User createUserObject(UserType userType){

    User user = new User("User_"+id, userType, id);
    id++;

    return user;
  }
}
