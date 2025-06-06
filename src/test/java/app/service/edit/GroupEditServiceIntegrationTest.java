package app.service.edit;

import app.data.Group;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.GroupEditService;
import service.user.UserService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class GroupEditServiceIntegrationTest {

  private static final String ADMIN_EMAIL = "unitTest@test";
  private static final String USER_EMAIL = "user@test";

  private static User admin;
  private static User standardUser;


  private static UserService createUserService;
  private static GroupEditService groupEditService;
  private static ReadGroupService readGroupService;
  private static IntegrationTestConnectionProvider testConnectionProvider;

  private  static AuthService authService;

  private static void assertGroupsAreEqual(Group group1, Group group2){
    assertEquals(group1.id, group2.id);
    assertEquals(group1.url, group2.url);
    assertEquals(group1.name, group2.name);
    assertEquals(group1.summary, group2.summary);

  }
  static void setup(){
    testConnectionProvider = new IntegrationTestConnectionProvider();
    groupEditService = new GroupEditService();
    createUserService = new UserService();
    authService = new AuthService();

    ReadGroupDataProvider dataProvider = ReadGroupDataProvider.create(admin, testConnectionProvider);
    readGroupService = new ReadGroupService(dataProvider);
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);

      admin = createUserService.createAdmin(ADMIN_EMAIL, testConnectionProvider);
      standardUser = createUserService.createStandardUser(USER_EMAIL, testConnectionProvider);


    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testUserCannotEditGroup_whenNotLoggedIn() throws Exception {
    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    User readOnlyUser = authService.getReadOnlyUser();

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(readOnlyUser, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }


  @Test
  public void testUserCannotEditGroup_whenTheyAreStandardUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(standardUser, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    groupEditService.editGroup(standardUser, updated, testConnectionProvider);


    Group updatedFromDb = readGroupService.getGroup(group.getId(), testConnectionProvider);

    assertGroupsAreEqual(updatedFromDb, group);
  }

  //TODO: Update tests
  @Test
  public void testUserCannotEditGroupThatDoesNotExist(){

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




}
