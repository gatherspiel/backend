package app.service.edit.permissions;

import app.data.Group;
import app.data.auth.User;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.GroupEditService;
import service.update.permissions.EditPermissionService;
import service.user.CreateUserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EditPermissionServiceIntegrationTest {

  private static final String ADMIN_EMAIL = "unitTest@test";
  private static final String USER_EMAIL ="user@test";
  private static CreateUserService createUserService;
  private static EditPermissionService editPermissionService;
  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static GroupEditService groupEditService;
  @BeforeAll
  static void setup(){
    testConnectionProvider = new IntegrationTestConnectionProvider();
    editPermissionService = new EditPermissionService();
    groupEditService = new GroupEditService();
    try {

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }
  @Test
  public void testSiteAdmin_canEdit_groupPermissionLevelForUser(){
    User admin =createUserService.createAdmin(ADMIN_EMAIL);
    User user = createUserService.createStandardUser(USER_EMAIL);

    Group group = CreateGroupUtils.createRandomGroup();
    editPermissionService.setGroupAdmin(user, admin, group.getUUID());

    assertTrue(groupEditService.canEditGroup(user, group.getUUID()));

  }

  //TODO: Add logic to tests.
  @Test
  public void testGroupAdmin_updateGroupAdmin_previousGroupAdminBecomesModerator(){

  }

  @Test
  public void testSiteAdmin_updateGroupAdmin_previousGroupAdminBecomesModerator(){

  }

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
