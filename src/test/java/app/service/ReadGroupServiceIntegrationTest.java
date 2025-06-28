package app.service;

import app.SessionContext;
import app.groups.data.Group;
import app.users.data.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.GroupPageData;
import app.groups.data.GroupPageEventData;
import app.users.data.UserType;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.GroupSearchParams;
import io.javalin.http.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReadGroupServiceIntegrationTest {

  private static ReadGroupService groupService;

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  Context ctx = mock(Context.class);

  private static MockedStatic<AuthService> authMock;
  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {
      conn = testConnectionProvider.getDatabaseConnection();

      authMock = mockStatic(AuthService.class);
      authMock.when(()->AuthService.getReadOnlyUser()).thenReturn(new User("reader@dmvboardgames.com", UserType.READONLY, 123));

      var session = SessionContext.createContextWithoutUser(testConnectionProvider);


      DbUtils.createTables(testConnectionProvider.getDatabaseConnection());

      DbUtils.initializeData(testConnectionProvider);

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
              params
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
              params
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
              params
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
        params
    );
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
        params
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
      () -> assertTrue(data.getDescription().contains("We will be playing board games at Glory Days Grill in Alexandria"),
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
        params
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

    var sessionContext = CreateUserUtils.createContextWithNewAdminUser( "test1_"+ UUID.randomUUID(),testConnectionProvider);
    groupService = sessionContext.createReadGroupService();

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");
    GroupPageData result = groupService.getGroupPageData(
        params
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_whenRegularUserIsLoggedIn() throws Exception{

    var sessionContext = CreateUserUtils.createContextWithNewStandardUser("test1",testConnectionProvider);
    groupService = sessionContext.createReadGroupService();

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");
    GroupPageData result = groupService.getGroupPageData(
        params
    );
    assertFalse(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_onlyShowsEditPermissions_forStandardUser_whoIsGroupAdmin() throws Exception{

    var sessionContext = CreateUserUtils.createContextWithNewStandardUser("test_3",testConnectionProvider);
    var sessionContext2 = CreateUserUtils.createContextWithNewStandardUser("test_4",testConnectionProvider);

    Group group = CreateGroupUtils.createGroup(sessionContext.getUser(), conn);
    ReadGroupService groupService1 = sessionContext.createReadGroupService();

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = groupService1.getGroupPageData(
        params
    );
    assertTrue(result.userCanEdit());

    ReadGroupService groupService2 = sessionContext2.createReadGroupService();
    GroupPageData result2 = groupService2.getGroupPageData(
        params
    );
    assertFalse(result2.userCanEdit());
  }

  @Test
  public void testGetGroupData_showsEditPermissions_whenUserIsGroupModerator() throws Exception{

    var sessionContext = CreateUserUtils.createContextWithNewStandardUser("test_5",testConnectionProvider);
    var sessionContext2 = CreateUserUtils.createContextWithNewStandardUser("test_6",testConnectionProvider);

    GroupPermissionService groupPermissionService = sessionContext.createGroupPermissionService();
    ReadGroupService readGroupService = sessionContext.createReadGroupService();

    Group group = CreateGroupUtils.createGroup(sessionContext.getUser(), conn);
    groupPermissionService.addGroupModerator(sessionContext2.getUser(), group.getId());

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = readGroupService.getGroupPageData(
        params
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_whenUserIsNotLoggedIn() throws Exception{

    var sessionContext = CreateUserUtils.createContextWithNewStandardUser("test_7",testConnectionProvider);
    Group group = CreateGroupUtils.createGroup(sessionContext.getUser(), conn);

    var readOnlyContext = SessionContext.createContextWithoutUser(testConnectionProvider);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.name);
    GroupPageData result = readOnlyContext.createReadGroupService().getGroupPageData(
        params
    );
    assertFalse(result.userCanEdit());
  }
}
