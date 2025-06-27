package app.service;

import app.groups.data.Group;
import app.users.data.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.GroupPageData;
import app.groups.data.GroupPageEventData;
import app.utils.CreateGroupUtils;
import database.search.GroupSearchParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.user.UserService;

import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadGroupServiceIntegrationTest {

  private static ReadGroupService groupService;
  private static UserService userService;
  private static User user;

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;
  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      conn = testConnectionProvider.getDatabaseConnection();
      var dataProvider = UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection());
      userService = new UserService(dataProvider);

      user = AuthService.getReadOnlyUser();
      Connection conn = testConnectionProvider.getDatabaseConnection();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
      groupService = new ReadGroupService(ReadGroupDataProvider.create(conn),conn);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testInvalidNameAndTag() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "test");
    params.put(GroupSearchParams.NAME, "test2");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          GroupPageData result = groupService.getGroupPageData(
              AuthService.getReadOnlyUser(),
              params,
              testConnectionProvider
          );
        }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }

  @Test
  public void testInvalidNameAndValidTag() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "test2");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          GroupPageData result = groupService.getGroupPageData(
              AuthService.getReadOnlyUser(),
              params,
              testConnectionProvider
          );
        }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }

  @Test
  public void testValidNameAndInvalidTag() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "test");
    params.put(GroupSearchParams.NAME, "Game_Nights_at_Crossroads");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          GroupPageData result = groupService.getGroupPageData(
              AuthService.getReadOnlyUser(),
              params,
              testConnectionProvider
          );
        }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }


  @Test
  public void testValidNameAndValidTag_correctGroupInformation() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");


    GroupPageData result = groupService.getGroupPageData(
        AuthService.getReadOnlyUser(),
        params,
        testConnectionProvider
    );
        //TODO: Verify event id;
    Assertions.assertAll(
        () -> assertEquals("Alexandria Board Game Group", result.getName()),
        () -> assertEquals("https://www.meetup.com/board-games-at/", result.getUrl()),
        () -> assertEquals(result.getDescription().contains("Like playing board games after meeting new people?"), true)
    );
  }

  @Test
  public void testValidNameAndValidTag_Group_ValidEventsWithOneEventEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");

    GroupPageData result = groupService.getGroupPageData(
        AuthService.getReadOnlyUser(),
        params,
        testConnectionProvider
    );

    Set<GroupPageEventData> eventData = result.getEventData();

    LocalDate prevDate = null;
    for(GroupPageEventData data: eventData) {

      if(prevDate != null){
        assertTrue(data.getEventDate().isAfter(prevDate));
      }

      Assertions.assertAll(
         () -> assertEquals(data.getEventDate().getDayOfWeek(), DayOfWeek.MONDAY),
         () -> assertEquals(data.getName(), "Game Night at Glory Days"),
      () -> assertTrue(data.getDescription().contains("Like playing board games after meeting new people?"),
          data.getDescription()),
      () -> assertEquals(data.getLocation().toString(), "3141 Duke Street, Alexandria, VA 22314")
      );
      prevDate = data.getEventDate();
    }

    assertTrue(eventData.size()==4 || eventData.size()==5);

  }

  @Test
  public void testValidNameAndValidTag_Group_CorrectNumberOfEventsWithFourEventsEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");

    GroupPageData result = groupService.getGroupPageData(
        AuthService.getReadOnlyUser(),
        params,
        testConnectionProvider
    );

    Set<GroupPageEventData> eventData = result.getEventData();
    int eventCount = 0;

    LocalDate prevDate = null;
    for(GroupPageEventData data: eventData) {

      if(prevDate != null){
        assertTrue(data.getEventDate().isAfter(prevDate));
      }
      eventCount++;

      prevDate = data.getEventDate();
    }

    assertTrue(eventData.size()<=16 || eventData.size()<=20);
  }

  @Test
  public void testGetGroupData_showsEditPermissionsForAdminUser() throws Exception{

    var dataProvider=  UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection());

    User admin = new UserService(dataProvider).createAdmin("test_1");

    groupService = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");
    GroupPageData result = groupService.getGroupPageData(
        admin,
        params,
        testConnectionProvider
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_whenRegularUserIsLoggedIn() throws Exception{
    User standardUser = userService.createStandardUser("test_2");

    groupService = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");
    GroupPageData result = groupService.getGroupPageData(
        standardUser,
        params,
        testConnectionProvider
    );
    assertFalse(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_onlyShowsEditPermissions_forStandardUser_whoIsGroupAdmin() throws Exception{
    User standardUser = userService.createStandardUser("test_3");
    User standardUser2 = userService.createStandardUser("test_4");

    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    ReadGroupService groupService1 = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        standardUser,
        params,
        testConnectionProvider
    );
    assertTrue(result.userCanEdit());

    ReadGroupService groupService2 = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);
    GroupPageData result2 = groupService2.getGroupPageData(
        standardUser2,
        params,
        testConnectionProvider
    );
    assertFalse(result2.userCanEdit());
  }

  @Test
  public void testGetGroupData_showsEditPermissions_whenUserIsGroupModerator() throws Exception{
    User standardUser = userService.createStandardUser("test_5");
    User standardUser2 = userService.createStandardUser("test_6");

    GroupPermissionService groupPermissionService = new GroupPermissionService(conn);

    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    groupPermissionService.addGroupModerator(standardUser, standardUser2, group.getId());

    ReadGroupService groupService1 = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        standardUser,
        params,
        testConnectionProvider
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_whenUserIsNotLoggedIn() throws Exception{
    User standardUser = userService.createStandardUser("test_7");

    Group group = CreateGroupUtils.createGroup(standardUser, conn);

    User readOnlyUser = AuthService.getReadOnlyUser();
    ReadGroupService groupService1 = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        readOnlyUser,
        params,
        testConnectionProvider
    );
    assertFalse(result.userCanEdit());
  }
}
