package app.service.auth;

import app.data.auth.User;
import app.data.auth.UserType;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.result.error.DuplicateUsernameException;
import app.utils.CreateUserUtils;
import database.user.UserRepository;
import io.javalin.http.Context;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;
import service.user.UserService;

import java.sql.Connection;


import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class RegisterUserIntegrationTest {


  private static IntegrationTestConnectionProvider testConnectionProvider;

  private static AuthService authService;
  private static AuthService authServiceWithError;

  private static UserService userService;
  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();

      Connection transactionConn = testConnectionProvider.getConnectionWithManualCommit();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);

      userService = new UserService(UserService.DataProvider.createDataProvider(transactionConn));

      authServiceWithError = new AuthService(new MockAuthProviderInvalidToken(), userService);
      authService = new AuthService(new NoErrorMockAuthProvider(), userService);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }


  @Test
  public void registerWithDuplicateUsername_throwsError_withNoDatabaseUpdate() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);
    userService.createStandardUser(user.getEmail());

    int userCount1 = userService.countUsers();
    Exception exception = assertThrows(
        Exception.class,
        ()->{
          authService.registerUser(user.getEmail(), "1234", UserType.USER);
        }
    );

    assertEquals(exception.getMessage(), "Username: " +user.getEmail()+ " already exists");
    assertTrue(exception instanceof DuplicateUsernameException);

    int userCount2 = userService.countUsers();
    assertEquals(userCount1, userCount2);
  }

  @Test
  public void registerWithValidUsernameAndPassword_createsInactiveUser() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);

    authService.registerUser(user.getEmail(), "1234", UserType.USER);

    User createdUser = userService.getUser(user.getEmail());
    assertEquals(user.getEmail(), createdUser.getEmail());
  }

  @Test
  public void registerUser_authenticationError_userNotCreated() throws Exception {
    User user = CreateUserUtils.createUserObject(UserType.USER);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          authServiceWithError.registerUser(user.getEmail(), "1234", UserType.USER);
        }
    );
    assertTrue(exception.getMessage().contains("Failed to create user"), exception.getMessage());
    assertTrue(exception instanceof AuthService.RegisterUserException,exception.getClass().getName());
    User savedUser = userService.getUser(user.getEmail());
    assertNull(savedUser);
  }


  @Test
  public void testInactiveUserCannotAuthenticate() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);
    authService.registerUser(user.getEmail(), "1234", UserType.USER);

    Context context = mock(Context.class);
    User readOnly = authService.getUser(context, testConnectionProvider);

    assertEquals(readOnly.getEmail(),AuthService.getReadOnlyUser().getEmail());
    assertEquals(readOnly.getAdminLevel(),UserType.READONLY.toString());
  }

  @Test
  public void testActiveUsersHasCorrectUserType() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);
    User user2 = CreateUserUtils.createUserObject(UserType.TESTER);

    authService.registerUser(user.getEmail(), "1234", UserType.USER);
    authService.registerUser(user2.getEmail(), "1234", UserType.USER);

    User savedUser = userService.getUser(user.getEmail());
    User savedUser2 = userService.getUser(user2.getEmail());

    assertEquals(user, savedUser);
    assertEquals(user2, savedUser2);
  }

  @Test
  public void testActiveUserCanAuthenticate() throws Exception{
    User user = CreateUserUtils.createUserObject(UserType.USER);
    authService.registerUser(user.getEmail(), "1234", UserType.USER);

    userService.activateUser(user.getEmail());

    Context context = mock(Context.class);
    User authenticatedUser = authService.getUser(context, testConnectionProvider);
    assertEquals(user, authenticatedUser);
  }

}
