package app.utils;

import app.data.auth.User;
import app.data.auth.UserType;

import java.util.UUID;

public class CreateUserUtils {

  public static int id = 0;

  public static User createUserObject(UserType userType){

    User user = new User("User_"+id, userType, id);
    id++;

    return user;
  }
}
