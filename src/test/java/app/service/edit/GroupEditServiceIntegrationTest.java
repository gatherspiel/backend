package app.service.edit;

import app.SessionContext;
import app.groups.data.Group;
import app.users.data.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.GroupEditService;
import service.user.UserService;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GroupEditServiceIntegrationTest {

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String USERNAME_2 = "user";
  private static final String USERNAME_3 = "user2@test";
  private static final String USERNAME_4 = "user3@test";

  private static User admin;
  private static User standardUser;
  private static User standardUser2;
  private static User standardUser3;


  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static SessionContext adminContext;
  private static SessionContext standardUserContext;
  private static SessionContext standardUserContext2;
  private static SessionContext standardUserContext3;
  private static SessionContext readOnlyUserContext;

  private static void assertGroupsAreEqual(Group group1, Group group2){
    assertEquals(group1.id, group2.id);
    assertEquals(group1.url, group2.url);
    assertEquals(group1.name, group2.name);
    assertEquals(group1.description, group2.description);

  }

  @BeforeAll
  static void setup() throws Exception{
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);

      adminContext = CreateUserUtils.createContextWithNewAdminUser(testConnectionProvider, ADMIN_USERNAME);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(testConnectionProvider, USERNAME_2);
      standardUserContext2 = CreateUserUtils.createContextWithNewStandardUser(testConnectionProvider, USERNAME_3);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(testConnectionProvider, USERNAME_4);
      readOnlyUserContext = SessionContext.createContextWithoutUser(testConnectionProvider);

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testUserCannotEditGroup_whenNotLoggedIn() throws Exception {
    Group group = CreateGroupUtils.createGroup(admin, conn);
    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          readOnlyUserContext.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }


  @Test
  public void testUserCannotEditGroup_whenTheyAreStandardUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(admin, conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testSiteAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    adminContext.createGroupEditService().editGroup(updated);

    Optional<Group> updatedFromDb = adminContext.createReadGroupService().getGroup(group.getId());

    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }


  @Test
  public void testGroupAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    standardUserContext.createGroupEditService().editGroup(updated);

    Optional<Group> updatedFromDb = standardUserContext.createReadGroupService().getGroup(group.getId());
    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }

  @Test
  public void testUserCannotEditGroupThatDoesNotExist() throws Exception{
    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId((int)(Math.random()*999999));
    Exception exception = assertThrows(
        Exception.class,
        ()->{
          adminContext.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("not found"));

  }

  @Test
  public void testGroupAdminCannotEditGroup_whenTheyAreNotAdminOfThatGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, conn);
    CreateGroupUtils.createGroup(standardUser2,conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());


    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext2.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModeratorCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    standardUserContext.createGroupPermissionService().addGroupModerator(standardUser2, group.getId());

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    standardUserContext2.createGroupEditService().editGroup(updated);

    Optional<Group> updatedFromDb  = standardUserContext2.createReadGroupService().getGroup(group.getId());
    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }

  @Test
  public void testGroupModeratorCannotEditGroup_whenTheyAreNotAdminOfThatGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, conn);
    Group group2 = CreateGroupUtils.createGroup(standardUser2, conn);

    standardUserContext.createGroupPermissionService().addGroupModerator(standardUser3, group.getId());

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group2.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext3.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupAdminCanDeleteGroup() throws Exception {

    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    standardUserContext.createGroupEditService().deleteGroup(group.getId());

    Optional<Group> groupInDb = standardUserContext.createReadGroupService().getGroup(group.getId());
    assertTrue(groupInDb.isEmpty());
  }

  @Test
  public void testStandardUserCannotDeleteGroup() throws Exception {

    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext2.createGroupEditService().deleteGroup(group.getId());
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }
}
