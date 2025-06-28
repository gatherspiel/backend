package app.utils;

import app.SessionContext;
import app.users.data.User;
import app.users.data.UserType;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import service.auth.AuthService;
import service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class CreateUserUtils {

  public static int id = 0;

  public static User createUserObject(UserType userType){

    User user = new User("User_"+id, userType, id);
    id++;

    return user;
  }

  public static SessionContext createContextWithExistingUser(User user, ConnectionProvider provider) throws Exception{
    var context = SessionContext.createContextWithoutUser(provider);
    context.setUser(user);
    return context;
  }

  public static SessionContext createContextWithNewAdminUser(String username, ConnectionProvider provider) throws Exception {

    var sessionContext = SessionContext.createContextWithoutUser(provider);
    var userService = sessionContext.createUserService();
    User admin = userService.createAdmin(username);
    sessionContext.setUser(admin);

    return sessionContext;
  }

  public static SessionContext createContextWithNewStandardUser( String username,ConnectionProvider provider) throws Exception {

    var sessionContext = SessionContext.createContextWithoutUser(provider);
    var userService = sessionContext.createUserService();
    User user = userService.createStandardUser(username);
    sessionContext.setUser(user);



    return sessionContext;
  }
}
