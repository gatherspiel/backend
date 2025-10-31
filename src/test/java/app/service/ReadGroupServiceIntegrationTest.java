package app.service;

import app.users.data.SessionContext;
import app.groups.data.*;
import app.result.group.GroupPageData;
import app.users.data.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.users.data.UserData;
import app.users.data.UserType;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.GroupSearchParams;
import database.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import service.auth.AuthService;
import service.update.EventService;
import service.update.GroupPermissionService;
import service.read.ReadGroupService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReadGroupServiceIntegrationTest {

  private static ReadGroupService groupService;

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static SessionContext adminContext;
  private static SessionContext adminContext2;
  private static SessionContext sessionContext;
  private static SessionContext standardUserContext;

  private static MockedStatic<AuthService> authMock;

  private static final String ADMIN_USERNAME = "unitTest";

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {
      conn = testConnectionProvider.getDatabaseConnection();

      authMock = mockStatic(AuthService.class);
      authMock.when(()->AuthService.getReadOnlyUser()).thenReturn(new User("reader@dmvboardgames.com", UserType.READONLY, 123));

      DbUtils.createTables(testConnectionProvider.getDatabaseConnection());
      DbUtils.initializeData(testConnectionProvider);

      adminContext = CreateUserUtils.createContextWithNewAdminUser(ADMIN_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      adminContext2 = CreateUserUtils.createContextWithNewAdminUser(ADMIN_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      sessionContext = SessionContext.createContextWithoutUser(testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser("user_"+UUID.randomUUID(),testConnectionProvider);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
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
        sessionContext.createReadGroupService().getGroupPageData(params);
      }
    );
    exception.printStackTrace();
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
        groupService.getGroupPageData(params);
      }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }

  @Test
  public void testValidNameAndValidTag_CorrectGroupInformation() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");

    GroupPageData result = groupService.getGroupPageData(
        params
    );
    Assertions.assertAll(
        () -> assertEquals("Alexandria Board Game Group", result.getName()),
        () -> assertEquals("https://www.meetup.com/board-games-at/", result.getUrl())
    );
  }

  @Test
  public void testEventsWithoutValidLocations_areNotVisibleInGroupData() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria-Arlington Regional Gaming Group");

    GroupPageData result = adminContext.createReadGroupService().getGroupPageData(
        params
    );

    assertEquals(result.getOneTimeEventData().size(),0);
  }

  @Test
  public void testValidNameAndValidTag_Group_ValidRecurringEventsWithOneEventEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");

    GroupPageData result = sessionContext.createReadGroupService().getGroupPageData(
        params
    );

    Set<Event> eventData = result.getWeeklyEventData();

    for(Event data: eventData) {


      Assertions.assertAll(
        () -> assertEquals(data.getDay(), DayOfWeek.MONDAY),
        () -> assertEquals(data.getName(), "Game Night at Glory Days"),
        () -> assertTrue(data.getDescription().contains("We will be playing board games at Glory Days Grill in Alexandria"),
          data.getDescription()),
        () -> assertEquals(data.getLocation().toString(), "3141 Duke Street,Alexandria,VA 22314"),
        () -> assertTrue(data.getIsRecurring())
      );
    }

    assertTrue(eventData.size()==1);

  }

  @Test
  public void testValidNameAndValidTag_Group_CorrectNumberOfEventsWithFourEventsEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");

    GroupPageData result = sessionContext.createReadGroupService().getGroupPageData(
        params
    );

    TreeSet<Event> eventData = result.getWeeklyEventData();
    int correctEventNames = 0;

    DayOfWeek[] days = new DayOfWeek[4];
    for(Event data: eventData) {

      days[correctEventNames] = data.getDay();

      assertTrue(data.getIsRecurring());
      if(data.getName().equals("High Interaction Board Games at Western Market Food Hall in DC")){
        assertEquals(DayOfWeek.SUNDAY,data.getDay());
        assertEquals("We play a variety of high interaction games with a focus on cooperative games, hidden identity games, and high interaction(German-style) Euros.",
            data.getDescription());
        assertEquals(LocalTime.NOON.plusHours(1),data.getStartTime());
        assertEquals(LocalTime.NOON.plusHours(5),data.getEndTime());
        correctEventNames ++;
      }

      if(data.getName().equals("Board Game Night @ Board Room in Clarendon, Wed, 6:30-10:00")){
        assertEquals(DayOfWeek.WEDNESDAY,data.getDay());
        assertEquals("Let's play board games. Not the ones your ancestors played but the really cool ones of the new millennium. We play everything from fun, social games to light to heavy strategy games.",
            data.getDescription());

        assertEquals(LocalTime.NOON.plusHours(6).plusMinutes(30),data.getStartTime());
        assertEquals(LocalTime.NOON.plusHours(10),data.getEndTime());
        correctEventNames ++;
      }

      if(data.getName().equals("Bring Your Own Eurogames Night at the Crystal City Shops next to We the Pizza")){
        assertEquals(DayOfWeek.FRIDAY,data.getDay());
        assertEquals("We play a variety of game with a focus on Eurogames",data.getDescription());
        assertEquals(LocalTime.NOON.plusHours(6).plusMinutes(30),data.getStartTime());
        assertEquals(LocalTime.NOON.plusHours(10),data.getEndTime());
        correctEventNames ++;
      }

      if(data.getName().equals("Bring Your Own Game Night in DC at Nanny O’Briens")){
        assertEquals(DayOfWeek.MONDAY,data.getDay());
        assertEquals("Hello again! After a long hiatus, the Monday meet up at Nanny O’Briens is back! We’ll have some games to play, but feel free to bring your favorites as well. Stop by for a game and a drink, and say hello! We’ll be in the back room",
            data.getDescription());
        assertEquals(LocalTime.NOON.plusHours(6).plusMinutes(30),data.getStartTime());
        assertEquals(LocalTime.NOON.plusHours(10).plusMinutes(30),data.getEndTime());
        correctEventNames ++;
      }
    }

    assertEquals(days[0],DayOfWeek.MONDAY);
    assertEquals(days[1],DayOfWeek.WEDNESDAY);
    assertEquals(days[2],DayOfWeek.FRIDAY);
    assertEquals(days[3],DayOfWeek.SUNDAY);

    assertEquals(4,correctEventNames);
    assertEquals(0, result.getOneTimeEventData().size());


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

    var adminEditService = adminContext.createGroupEditService();
    adminEditService.setGroupToVisible(group.getId());

    ReadGroupService groupService1 = sessionContext.createReadGroupService();

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.getName());
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

    var adminEditService = adminContext.createGroupEditService();
    adminEditService.setGroupToVisible(group.getId());

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.getName());
    GroupPageData result = readGroupService.getGroupPageData(
        params
    );
    assertTrue(result.userCanEdit());
  }

  @Test
  public void testGetGroupData_doesNotShowEditPermissions_rsvpsDisabled_whenUserIsNotLoggedIn() throws Exception{

    var readOnlyContext = SessionContext.createContextWithoutUser(testConnectionProvider);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");

   GroupPageData result = readOnlyContext.createReadGroupService().getGroupPageData(
        params
    );
    assertFalse(result.userCanEdit());
    assertEquals(4,result.getWeeklyEventData().size());

    for(Event event: result.getWeeklyEventData()) {
      assertFalse(event.getUserCanUpdateRsvp());
    }

  }
  @Test
  public void testEventCreatedWithHost_rsvpCountIsOne_AndOnlyHostHasRsvp_andHostCannotUpdateRsvp() throws Exception{

    String deleteEventAdminQuery =
        "TRUNCATE TABLE event_admin_data CASCADE";
    String deleteGroupAdminQuery =
        "TRUNCATE TABLE group_admin_data CASCADE";
    String deleteRsvpQuery =
        "TRUNCATE TABLE event_rsvp CASCADE";


    PreparedStatement query4 = conn.prepareStatement(deleteEventAdminQuery);
    PreparedStatement query5 = conn.prepareStatement(deleteGroupAdminQuery);
    PreparedStatement query6 = conn.prepareStatement(deleteRsvpQuery);
    query4.execute();
    query5.execute();
    query6.execute();

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");

    GroupPageData result = sessionContext.createReadGroupService().getGroupPageData(
        params
    );

    EventService adminEventService = adminContext.createEventService();

    for (Event data : result.getWeeklyEventData()) {
      if (data.getName().equals("High Interaction Board Games at Western Market Food Hall in DC")) {
        adminEventService.addEventModerator(data, standardUserContext.getUser());
      }
    }


    GroupPageData readerResult = sessionContext.createReadGroupService().getGroupPageData(params);
    Event foundEvent = null;
    for (Event data : readerResult.getWeeklyEventData()) {
      if (data.getName().equals("High Interaction Board Games at Western Market Food Hall in DC")) {
        foundEvent = data;
      }
    }
    assertFalse(foundEvent.getUserHasRsvp());
    assertEquals(foundEvent.getRsvpCount(), 1);

    GroupPageData moderatorResult = standardUserContext.createReadGroupService().getGroupPageData(params);
    Event standardUserFoundEvent = null;
    for (Event data : moderatorResult.getWeeklyEventData()) {

      if (data.getName().equals("High Interaction Board Games at Western Market Food Hall in DC")) {
        standardUserFoundEvent = data;
      }
    }
    assertTrue(standardUserFoundEvent.getUserHasRsvp());
    assertEquals(standardUserFoundEvent.getRsvpCount(), 1);
    assertFalse(standardUserFoundEvent.getUserCanUpdateRsvp());
  }

  @Test
  public void testRsvpToEventRsvp_StatusIsYes_OnlyForThatEvent() throws Exception {


    String deleteEventAdminQuery =
        "TRUNCATE TABLE event_admin_data CASCADE";
    String deleteGroupAdminQuery =
        "TRUNCATE TABLE group_admin_data CASCADE";
    String deleteRsvpQuery =
        "TRUNCATE TABLE event_rsvp CASCADE";


    PreparedStatement query4 = conn.prepareStatement(deleteEventAdminQuery);
    PreparedStatement query5 = conn.prepareStatement(deleteGroupAdminQuery);
    PreparedStatement query6 = conn.prepareStatement(deleteRsvpQuery);
    query4.execute();
    query5.execute();
    query6.execute();

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");

    GroupPageData result = sessionContext.createReadGroupService().getGroupPageData(
        params
    );

    EventService eventService = adminContext.createEventService();

    for (Event data : result.getWeeklyEventData()) {
      if (data.getName().equals("High Interaction Board Games at Western Market Food Hall in DC")) {
        eventService.rsvpTpEvent(data.getId());
      }
    }

    GroupPageData adminResult =  adminContext.createReadGroupService().getGroupPageData(params);
    GroupPageData adminResult2 =  adminContext2.createReadGroupService().getGroupPageData(params);

    for (Event data : adminResult.getWeeklyEventData()) {
      if (data.getName().equals("High Interaction Board Games at Western Market Food Hall in DC")) {
        assertTrue(data.getUserHasRsvp());
      } else {
        assertFalse(data.getUserHasRsvp());
      }
    }

    for(Event data: adminResult2.getWeeklyEventData()){
      assertFalse(data.getUserHasRsvp());
    }

  }
}
