package app.service.edit.permissions;

import app.data.Group;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.GroupEditService;
import service.update.permissions.GroupPermissionService;
import service.user.CreateUserService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EditPermissionServiceIntegrationTest {

  private static final String ADMIN_EMAIL = "unitTest@test";
  private static final String USER_EMAIL = "user@test";
  private static final String USER_EMAIL_2 = "user2@test";
  private static CreateUserService createUserService;
  private static GroupPermissionService groupPermissionService;
  private static IntegrationTestConnectionProvider testConnectionProvider;
  @BeforeAll
  static void setup(){
    testConnectionProvider = new IntegrationTestConnectionProvider();
    groupPermissionService = new GroupPermissionService();
    createUserService = new CreateUserService();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }
  @Test
  public void testSiteAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    User admin = createUserService.createAdmin(ADMIN_EMAIL, testConnectionProvider);
    User user = createUserService.createStandardUser(USER_EMAIL, testConnectionProvider);
    User user2 = createUserService.createStandardUser(USER_EMAIL_2, testConnectionProvider);

    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    groupPermissionService.setGroupAdmin(user, group.getId(), testConnectionProvider);
    assertTrue(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertFalse(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));

    groupPermissionService.setGroupAdmin(user2, group.getId(), testConnectionProvider);
    assertFalse(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
    //TODO: Test permission removed
  }

  //TODO: Add logic to tests.


  @Test
  public void testGroupAdmin_canEdit_groupPermissionLevelForUser(){

  }


  @Test
  public void testGroupAdmin_cannotEdit_groupPermissionLevelForDifferentGroup(){

  }

  @Test
  public void testGroupModerator_canEdit_groupPermissionLevelForUser(){

  }


  @Test
  public void testGroupModerator_cannotEdit_groupPermissionLevelForGroupAdmin(){

  }

  @Test
  public void testGroupModerator_cannotEdit_groupPermissionLevelForDifferentGroup(){

  }
}
