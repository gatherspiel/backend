package app.utils;

import app.SessionContext;
import app.users.data.User;
import app.users.data.UserType;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import service.auth.AuthService;

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
    var authMock = mockStatic(AuthService.class);
    var context = mock(Context.class);

    authMock.when(()->AuthService.getUser(any(), context)).thenReturn(user);
    return SessionContext.createContextWithUser(context, provider);
  }

  public static SessionContext createContextWithNewAdminUser(ConnectionProvider provider, String username) throws Exception {

    var sessionContext = SessionContext.createContextWithoutUser(provider);
    var userService = sessionContext.createUserService();
    User admin = userService.createAdmin(username);
    sessionContext.setUser(admin);

    return sessionContext;
  }

  public static SessionContext createContextWithNewStandardUser(ConnectionProvider provider, String username) throws Exception {

    var sessionContext = SessionContext.createContextWithoutUser(provider);
    var userService = sessionContext.createUserService();
    User user = userService.createStandardUser(username);
    sessionContext.setUser(user);

    return sessionContext;
  }
}
