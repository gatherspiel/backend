package app.service.auth;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import database.user.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.fail;

public class RegisterUserIntegrationTest {

  private static IntegrationTestConnectionProvider testConnectionProvider;

  private static AuthService authService;
  private static UserRepository userRepository;
 /* @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();

      userRepository = new UserRepository();
      authService = new AuthService(new NoErrorMockAuthProvider(), userRepository);
      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }


  @Test
  public void registerWithDuplicateUsername_throwsError_withNoDatabaseUpdate(){
    userRepository.c
  }

  @Test
  public void registerWithValidUsernameAndPassword_createsInactiveUser(){

  }

  @Test
  public void testInactiveUserCannotAuthenticate(){

  }

  @Test
  public void testActiveUserCanAuthenticate(){

  }

  @Test
  public void testActiveSiteAdminCanGiveModeratorPermission_newUser(){

  }

  @Test
  public void testActiveStandardUser_cannotUpdatePermission_forNewUser(){

  }
  */
}
