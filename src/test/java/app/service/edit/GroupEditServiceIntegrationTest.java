package app.service.edit;

import app.database.utils.IntegrationTestConnectionProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.permissions.EditPermissionService;

import static org.junit.jupiter.api.Assertions.fail;

public class GroupEditServiceIntegrationTest {

  @Test
  public void testUserCannotEditGroup_whenNotLoggedIn() {

  }

  @Test
  public void testUserCannotEditGroup_whenTheyAreStandardUser(){

  }

  @Test
  public void testGroupAdminCanEditGroup(){

  }

  @Test
  public void testGroupAdminCannotEditGroup_whenTheyAreNotAdminOfThatGroup(){

  }

  @Test
  public void testGroupModeratorCanEditGroup(){

  }

  @Test
  public void testGroupModeratorCannotEditGroup_whenTheyAreNotAdminOfThatGroup(){

  }

  @Test
  public void testAdminCanCreateGroup_andBecomesGroupAdmin(){

  }

  @Test
  public void testTesterCanCreateGroup_andBecomesGroupAdmin_WhenUpdateGroupFeatureFlagIsOff(){

  }

  @Test
  public void testUserCannotCreateGroup(){

  }


}
