package app.service;

import app.data.Group;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.result.groupPage.GroupPageData;
import app.result.groupPage.GroupPageEventData;
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

public class ReadGroupServiceIntegrationTest {

  private static ReadGroupService groupService;
  private static IntegrationTestConnectionProvider testConnectionProvider;

  private static User user;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      AuthService authService = new AuthService();
      user = authService.getReadOnlyUser();
      Connection conn = testConnectionProvider.getDatabaseConnection();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
      groupService = new ReadGroupService(ReadGroupDataProvider.create(user, testConnectionProvider));
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
        params,
        testConnectionProvider
    );

    Assertions.assertAll(
        () -> assertEquals("Alexandria Board Game Group", result.getName()),
        () -> assertEquals("https://www.meetup.com/board-games-at/", result.getUrl()),
        () -> assertEquals(result.getSummary().contains("Like playing board games after meeting new people?"), true)
    );
  }

  @Test
  public void testValidNameAndValidTag_Group_ValidEventsWithOneEventEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");

    GroupPageData result = groupService.getGroupPageData(
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
      Assertions.assertAll(
         () -> assertEquals(data.getEventDate().getDayOfWeek(), DayOfWeek.MONDAY),
         () -> assertEquals(data.getName(), "Game Night at Glory Days"),
      () -> assertEquals(data.getDescription().contains("We will be playing board games at Glory Days Grill in Alexandria "),
          true),
      () -> assertEquals(data.getLocation(), "3141 Duke Street, Alexandria, VA 22314")
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

    User admin = new UserService().createAdmin("test_1", testConnectionProvider);

    groupService = new ReadGroupService(ReadGroupDataProvider.create(admin, testConnectionProvider));

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");
    GroupPageData result = groupService.getGroupPageData(
        params,
        testConnectionProvider
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_whenRegularUserIsLoggedIn() throws Exception{
    User standardUser = new UserService().createStandardUser("test_2", testConnectionProvider);

    groupService = new ReadGroupService(ReadGroupDataProvider.create(standardUser, testConnectionProvider));

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");
    GroupPageData result = groupService.getGroupPageData(
        params,
        testConnectionProvider
    );
    assertFalse(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_onlyShowsEditPermissions_forStandardUser_whoIsGroupAdmin() throws Exception{
    User standardUser = new UserService().createStandardUser("test_3", testConnectionProvider);
    User standardUser2 = new UserService().createStandardUser("test_4", testConnectionProvider);

    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    ReadGroupService groupService1 = new ReadGroupService(ReadGroupDataProvider.create(standardUser, testConnectionProvider));
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        params,
        testConnectionProvider
    );
    assertTrue(result.userCanEdit());

    ReadGroupService groupService2 = new ReadGroupService(ReadGroupDataProvider.create(standardUser2, testConnectionProvider));
    GroupPageData result2 = groupService2.getGroupPageData(
        params,
        testConnectionProvider
    );
    assertFalse(result2.userCanEdit());
  }

  @Test
  public void testGetGroupData_showsEditPermissions_whenUserIsGroupModerator() throws Exception{
    User standardUser = new UserService().createStandardUser("test_5", testConnectionProvider);
    User standardUser2 = new UserService().createStandardUser("test_6", testConnectionProvider);

    GroupPermissionService groupPermissionService = new GroupPermissionService();

    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    groupPermissionService.addGroupModerator(standardUser, standardUser2, group.getId(), testConnectionProvider);

    ReadGroupService groupService1 = new ReadGroupService(ReadGroupDataProvider.create(standardUser2, testConnectionProvider));
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        params,
        testConnectionProvider
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_whenUserIsNotLoggedIn() throws Exception{
    User standardUser = new UserService().createStandardUser("test_7", testConnectionProvider);

    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    User readOnlyUser = new AuthService().getReadOnlyUser();
    ReadGroupService groupService1 = new ReadGroupService(ReadGroupDataProvider.create(readOnlyUser, testConnectionProvider));
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        params,
        testConnectionProvider
    );
    assertFalse(result.userCanEdit());
  }

}
