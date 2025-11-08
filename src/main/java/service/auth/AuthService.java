package service.auth;

import app.result.error.RegisterUserInvalidEmailException;
import app.result.error.StackTraceShortener;
import app.users.User;
import app.users.UserType;
import app.result.error.DuplicateUsernameException;
import app.users.RegisterUserRequest;
import app.users.RegisterUserResponse;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import service.auth.supabase.SupabaseAuthProvider;
import utils.LogUtils;

import java.sql.Connection;
import java.util.Optional;

public class AuthService {

  private AuthProvider authProvider;
  private UserService userService;
  private static final Logger logger = LogUtils.getLogger();

  public AuthService(AuthProvider authProvider, UserService userService){
    this.authProvider = authProvider;
    this.userService = userService;
  }

  public class RegisterUserException extends RuntimeException {
    public RegisterUserException(String message) {
      super(message);
    }
  }

  public class RegisterUserInvalidDataException extends RuntimeException {
    public RegisterUserInvalidDataException(String message) {
      super(message);
    }
  }

  public RegisterUserResponse registerUser(RegisterUserRequest request, UserType userType) throws Exception{

    validateRegisterUserRequest(request);
    if(userService.userExists(request.getEmail())) {
      throw new DuplicateUsernameException("Username: " + request.getEmail() + " already exists");
    }

    if(userType.equals(UserType.USER)) {
      userService.createStandardUser(request.getEmail());
    } else if(userType.equals(UserType.SITE_ADMIN)) {
      userService.createAdmin(request.getEmail());
    } else if(userType.equals(UserType.TESTER)) {
      userService.createTester(request.getEmail());
    } else {
      throw new Exception("Cannot create user with type:"+userType.toString());
    }

    try {
      var registerUserResponse = authProvider.registerUser(request);
      userService.commitChanges();
      logger.info("Created user with username:"+request.getEmail());
      return registerUserResponse;
    } catch (Exception e) {
      userService.rollbackChanges();

      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      e.printStackTrace();
      if(e.getMessage().contains("Unable to validate email address")){
        logger.error("Invalid email");
        throw new RegisterUserInvalidEmailException(e.getMessage());
      }
      throw new RegisterUserException("Failed to create user due to error:"+e.getMessage());
    }
  }

  private void validateRegisterUserRequest(RegisterUserRequest request){
    if(request.getEmail() == null ||request.getEmail().isBlank()){
      throw new RegisterUserInvalidDataException("Invalid email");
    }
    if(request.getPassword() == null ||request.getPassword().isBlank()){
      throw new RegisterUserInvalidDataException("Invalid password");
    }
  }
  /**
   *
   * @return Returns currently logged in user, or read only user if the user is not logged in.
   */
  public User getUser(Context ctx) throws Exception{
    logger.info("Retrieving current user");

    String userToken = ctx.header("authToken");

    Optional<String> username =  authProvider.getUsernameFromToken(userToken);

    if(!username.isPresent()){
      return getReadOnlyUser();
    }

    User user = userService.getActiveUser(username.get());
    if(user == null){
      return AuthService.getReadOnlyUser();
    }
    return user;
  }


  /**
   *
   * @return Returns read only user
   */
  public static User getReadOnlyUser(){
    User user = new User("reader@dmvboardgames.com", UserType.READONLY, -1);
    return user;
  }


  public static User getUser(Connection conn, Context ctx) throws Exception{

    String userToken = ctx.header("authToken");
    if(userToken == null){
      return getReadOnlyUser();
    }

    UserService userService = new UserService(UserService.DataProvider.createDataProvider(conn),AuthService.getReadOnlyUser());
    SupabaseAuthProvider supabaseAuthProvider = new SupabaseAuthProvider();

    AuthService authService = new AuthService(supabaseAuthProvider, userService);
    return authService.getUser(ctx);
  }

  public static RegisterUserResponse registerUser(Context ctx) throws Exception{
    var connectionProvider = new ConnectionProvider();

    UserService userService = new UserService(UserService.DataProvider.createDataProvider(connectionProvider.getConnectionWithManualCommit()),AuthService.getReadOnlyUser());
    SupabaseAuthProvider supabaseAuthProvider = new SupabaseAuthProvider();

    AuthService authService = new AuthService(supabaseAuthProvider, userService);

    var data = ctx.bodyAsClass(RegisterUserRequest.class);
    return authService.registerUser(data, UserType.USER);
  }

}
