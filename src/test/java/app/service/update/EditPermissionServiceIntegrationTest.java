package app.service.update;

import app.users.SessionContext;
import app.groups.Group;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.GroupPermissionService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EditPermissionServiceIntegrationTest {

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String USERNAME_1 = "user";
  private static final String USERNAME_2 = "user2";
  private static final String USERNAME_3 = "user3";
  private static final String USERNAME_4 = "user4";

  private static IntegrationTestConnectionProvider testConnectionProvider;


  private static SessionContext adminContext;
  private static SessionContext standardUserContext;
  private static SessionContext standardUserContext2;
  private static SessionContext standardUserContext3;
  private static SessionContext standardUserContext4;

  private static Connection conn;
  @BeforeAll
  static void setup() throws Exception{

    testConnectionProvider = new IntegrationTestConnectionProvider();
    conn = testConnectionProvider.getDatabaseConnection();

    try {
      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);

      adminContext = CreateUserUtils.createContextWithNewAdminUser( ADMIN_USERNAME,testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(USERNAME_1,testConnectionProvider);
      standardUserContext2 = CreateUserUtils.createContextWithNewStandardUser( USERNAME_2,testConnectionProvider);
      standardUserContext3 = CreateUserUtils.createContextWithNewStandardUser(USERNAME_3,testConnectionProvider);
      standardUserContext4 = CreateUserUtils.createContextWithNewStandardUser( USERNAME_4,testConnectionProvider);

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
    }
  }

  @Test
  public void testSiteAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    adminContext.createGroupPermissionService().setGroupAdmin(standardUserContext.getUser(), group.getId());

    GroupPermissionService adminPermissionService = adminContext.createGroupPermissionService();
    GroupPermissionService permissionService = standardUserContext.createGroupPermissionService();
    GroupPermissionService permissionService2 = standardUserContext2.createGroupPermissionService();

    assertTrue(permissionService.canEditGroup(group.getId()));
    assertFalse(permissionService2.canEditGroup(group.getId()));

    adminPermissionService.setGroupAdmin(standardUserContext2.getUser(), group.getId());
    assertFalse(permissionService.canEditGroup(group.getId()));
    assertTrue(permissionService2.canEditGroup(group.getId()));
  }

  @Test
  public void testGroupAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    GroupPermissionService permissionService1 = standardUserContext.createGroupPermissionService();
    GroupPermissionService permissionService2 = standardUserContext2.createGroupPermissionService();

    permissionService1.addGroupModerator(standardUserContext2.getUser(), group.getId());
    assertTrue(permissionService1.canEditGroup(group.getId()));
    assertTrue(permissionService2.canEditGroup(group.getId()));
  }

  @Test
  public void testGroupAdmin_cannotEdit_groupPermissionLevelForDifferentGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext2.createGroupPermissionService()
              .addGroupModerator(standardUserContext2.getUser(), group.getId());

        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModerator_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    GroupPermissionService permissionService1 = standardUserContext.createGroupPermissionService();
    GroupPermissionService permissionService2 = standardUserContext2.createGroupPermissionService();
    GroupPermissionService permissionService3 = standardUserContext3.createGroupPermissionService();

    permissionService1.addGroupModerator(standardUserContext2.getUser(), group.getId());
    permissionService1.addGroupModerator(standardUserContext3.getUser(), group.getId());

    assertTrue(permissionService2.canEditGroup(group.getId()));
    assertTrue(permissionService3.canEditGroup(group.getId()));
  }

  @Test
  public void testGroupModerator_cannotEdit_groupPermissionLevelForGroupAdmin() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext.createGroupPermissionService().addGroupModerator(standardUserContext2.getUser(), group.getId());
          standardUserContext2.createGroupPermissionService().setGroupAdmin(standardUserContext2.getUser(), group.getId());

        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModerator_cannotEdit_userPermissionLevelForDifferentGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(standardUserContext3.getUser(), conn);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext2.createGroupPermissionService().addGroupModerator(standardUserContext4.getUser(), group2.getId());
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"), exception.getMessage());
  }

  @Test
  public void testAdmin_cannotEdit_groupThatDoesNotExist() throws Exception {
    assertFalse(adminContext.createGroupPermissionService().canEditGroup(-1));
  }
}
