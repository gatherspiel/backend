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
  private static final String USER_EMAIL_3 = "user3@test";
  private static final String USER_EMAIL_4 = "user4@test";

  private static CreateUserService createUserService;
  private static GroupPermissionService groupPermissionService;
  private static IntegrationTestConnectionProvider testConnectionProvider;


  private static User admin;
  private static User user;
  private static User user2;
  private static User user3;
  private static User user4;

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

      admin = createUserService.createAdmin(ADMIN_EMAIL, testConnectionProvider);
      user = createUserService.createStandardUser(USER_EMAIL, testConnectionProvider);
      user2 = createUserService.createStandardUser(USER_EMAIL_2, testConnectionProvider);
      user3 = createUserService.createStandardUser(USER_EMAIL_3, testConnectionProvider);
      user4 = createUserService.createStandardUser(USER_EMAIL_4, testConnectionProvider);

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }
  @Test
  public void testSiteAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    groupPermissionService.setGroupAdmin(admin, user, group.getId(), testConnectionProvider);
    assertTrue(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertFalse(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));

    groupPermissionService.setGroupAdmin(admin, user2, group.getId(), testConnectionProvider);
    assertFalse(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
  }

  @Test
  public void testGroupAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    groupPermissionService.addGroupModerator(user, user2, group.getId(), testConnectionProvider);
    assertTrue(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
  }

  @Test
  public void testGroupAdmin_cannotEdit_groupPermissionLevelForDifferentGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupPermissionService.addGroupModerator(user2, user2, group.getId(), testConnectionProvider);

        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModerator_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    groupPermissionService.addGroupModerator(user, user2, group.getId(), testConnectionProvider);
    groupPermissionService.addGroupModerator(user2, user3, group.getId(), testConnectionProvider);

    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user3, group.getId(), testConnectionProvider));
  }

  @Test
  public void testGroupModerator_cannotEdit_groupPermissionLevelForGroupAdmin() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupPermissionService.addGroupModerator(user, user2, group.getId(), testConnectionProvider);
          groupPermissionService.setGroupAdmin(user2, user2, group.getId(), testConnectionProvider);

        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModerator_cannotEdit_userPermissionLevelForDifferentGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);
    Group group2 = CreateGroupUtils.createGroup(user3, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupPermissionService.addGroupModerator(user, user4, group2.getId(), testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }
}
