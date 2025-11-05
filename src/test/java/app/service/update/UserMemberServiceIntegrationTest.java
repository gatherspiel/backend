package app.service.update;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;

import app.groups.data.Event;
import app.groups.data.Group;
import app.users.data.SessionContext;
import app.users.data.UserMemberData;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.EventService;
import service.update.GroupPermissionService;
import service.update.UserMemberService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMemberServiceIntegrationTest {

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String STANDARD_USER_USERNAME = "testUser";

  private static final TreeSet<Group> EMPTY_GROUP_SET = new TreeSet<>();
  private static final TreeSet<Event> EMPTY_EVENT_SET = new TreeSet<>();

  private static SessionContext adminContext;
  private static SessionContext readOnlyUserContext;
  private static SessionContext standardUserContext;
  private static SessionContext standardUserContext2;
  private static SessionContext standardUserContext3;

  private static UserRepository userRepository;

  @BeforeAll
  static void setup(){
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {
      conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);

      adminContext = CreateUserUtils.createContextWithNewAdminUser(
          ADMIN_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);
      readOnlyUserContext = CreateUserUtils.createContextWithNewReadonlyUser(
          STANDARD_USER_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(
          STANDARD_USER_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);
      standardUserContext2 = CreateUserUtils.createContextWithNewStandardUser(
          STANDARD_USER_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);
      standardUserContext3 = CreateUserUtils.createContextWithNewStandardUser(
          STANDARD_USER_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);

      userRepository = new UserRepository(conn);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
    }
  }

  @Test
  public void testNewUser_isNotPartOfAnyGroups() throws Exception{
    SessionContext newUserContext = CreateUserUtils.createContextWithNewStandardUser(
        STANDARD_USER_USERNAME + UUID.randomUUID(),
        testConnectionProvider);

    UserMemberService userMemberService = newUserContext.createUserMemberService();

    UserMemberData memberData = userMemberService.getUserMemberData();

    assertIterableEquals(EMPTY_GROUP_SET, memberData.getJoinedGroups());
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getModeratingGroups());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getAttendingEvents());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getModeratingEvents());
  }

  @Test
  public void testNewAdmin_IsNotPartOfAnyGroupsAsModeratorOrAdmin() throws Exception{
    SessionContext newUserContext = CreateUserUtils.createContextWithNewAdminUser(
        ADMIN_USERNAME + UUID.randomUUID(),
        testConnectionProvider);

    UserMemberService userMemberService = newUserContext.createUserMemberService();

    UserMemberData memberData = userMemberService.getUserMemberData();

    assertIterableEquals(EMPTY_GROUP_SET, memberData.getJoinedGroups());
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getModeratingGroups());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getAttendingEvents());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getModeratingEvents());
  }

  @Test
  public void testNewAdminJoinsGroup_IsNotAGroupOrEventModerator_IsNotAttendingEvents() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    SessionContext newUserContext = CreateUserUtils.createContextWithNewAdminUser(
        ADMIN_USERNAME + UUID.randomUUID(),
        testConnectionProvider);

    UserMemberService userMemberService = newUserContext.createUserMemberService();
    userMemberService.joinGroup(group.getId());

    UserMemberData memberData = userMemberService.getUserMemberData();
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getModeratingEvents());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getAttendingEvents());

    assertIterableEquals(EMPTY_GROUP_SET, memberData.getModeratingGroups());
    assertIterableEquals(Collections.singletonList(group), memberData.getJoinedGroups());
  }

  @Test
  public void testStandardUserRsvpToEvent_nextEventTimeIsDisplayed() throws Exception{
    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    LocalTime startTime = LocalTime.now();
    DayOfWeek startDay = DayOfWeek.from(LocalDate.now());
    event.setStartTime(startTime);
    event.setDay(startDay.toString());
    Event created = adminEventService.createEvent(event,group.getId());

    EventService standardUserEventService = standardUserContext2.createEventService();
    standardUserEventService.rsvpTpEvent(created.getId());

    UserMemberData userGroupMemberData = standardUserContext2.createUserMemberService().getUserMemberData();
    assertIterableEquals(Collections.singletonList(created), userGroupMemberData.getAttendingEvents());

    Event eventFromDb = userGroupMemberData.getAttendingEvents().first();
    assertEquals(startTime, eventFromDb.getStartTime());
    assertEquals(startDay, eventFromDb.getDay());

    LocalDate expectedDate = LocalDate.now().with(TemporalAdjusters.next(startDay));
    assertEquals(expectedDate, eventFromDb.getStartDate());
  }

  @Test
  public void testStandardUserRsvpData_doesNotShowPastEventRsvp() throws Exception{
    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event,group.getId());

    EventService standardUserEventService = standardUserContext2.createEventService();
    standardUserEventService.rsvpTpEvent(created.getId());

    LocalTime startTime = LocalTime.now();
    DayOfWeek startDay = DayOfWeek.from(LocalDate.now());
    created.setStartTime(startTime);
    created.setDay(startDay.toString());
    adminEventService.updateEvent(created);

    UserMemberData userGroupMemberData = standardUserContext2.createUserMemberService().getUserMemberData();
    assertIterableEquals(EMPTY_EVENT_SET, userGroupMemberData.getAttendingEvents());
  }

  @Test
  public void testEventModeratorAddedBeforeLastEvent_ModeratorDataShowsNextEventDate() throws Exception{
    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event,group.getId());

    adminEventService.addEventModerator(created, standardUserContext2.getUser());

    EventService standardUserEventService = standardUserContext2.createEventService();
    standardUserEventService.rsvpTpEvent(created.getId());

    LocalTime startTime = LocalTime.now();
    DayOfWeek startDay = DayOfWeek.from(LocalDate.now());
    created.setStartTime(startTime);
    created.setDay(startDay.toString());
    adminEventService.updateEvent(created);

    UserMemberData userGroupMemberData = standardUserContext2.createUserMemberService().getUserMemberData();
    assertIterableEquals(Collections.singletonList(created), userGroupMemberData.getAttendingEvents());

    Event eventFromDb = userGroupMemberData.getModeratingEvents().first();
    assertEquals(startTime, eventFromDb.getStartTime());
    assertEquals(startDay, eventFromDb.getDay());

    LocalDate expectedDate = LocalDate.now().with(TemporalAdjusters.next(startDay));
    assertEquals(expectedDate, eventFromDb.getStartDate());
  }

  @Test
  public void test_groupAndEventWithModerator_AndEventRsvp_otherUserIsNotMember() throws Exception{
    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event,group.getId());
    adminEventService.addEventModerator(created, standardUserContext2.getUser());

    UserMemberData memberData = standardUserContext3.createUserMemberService().getUserMemberData();
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getJoinedGroups());
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getModeratingGroups());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getAttendingEvents());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getModeratingEvents());
  }

  @Test
  public void testUserCanJoinAndLeaveGroup_correctUserGroupDataIsVisible() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    UserMemberService userMemberService2 = standardUserContext2.createUserMemberService();
    userMemberService2.joinGroup(group.getId());
    assertIterableEquals(Collections.singletonList(group),userMemberService2.getUserMemberData().getJoinedGroups());

    UserMemberService userMemberService3 = standardUserContext3.createUserMemberService();
    UserMemberData memberData = userMemberService3.getUserMemberData();
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getJoinedGroups());

    userMemberService2.leaveGroup(group.getId());

    assertIterableEquals(EMPTY_GROUP_SET,userMemberService2.getUserMemberData().getJoinedGroups());
    assertIterableEquals(EMPTY_GROUP_SET,userMemberService3.getUserMemberData().getJoinedGroups());
  }

  @Test
  public void testGroupCreator_automaticallyIsShownAsGroupModerator_withoutJoiningGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    UserMemberService userMemberService = standardUserContext.createUserMemberService();
    UserMemberData memberData = userMemberService.getUserMemberData();
    assertIterableEquals(Collections.singletonList(group), memberData.getModeratingGroups());
  }

  @Test
  public void testGroupModerator_leavesGroup_isNoLongerGroupModerator() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    GroupPermissionService groupPermissionService = standardUserContext.createGroupPermissionService();
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(), group.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();
    userMemberService.leaveGroup(group.getId());

    UserMemberData memberData = userMemberService.getUserMemberData();
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getModeratingGroups());
  }

  @Test
  public void testUserIsGroupModerator_andEventModerator_userIsNotShownAsStandardMember() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    GroupPermissionService groupPermissionService = standardUserContext.createGroupPermissionService();
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(), group.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();
    UserMemberData memberData = userMemberService.getUserMemberData();
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getJoinedGroups());
  }

  @Test
  public void testUserIsGroupMember_isModeratorForOneEvent_andRsvpForOtherEvent() throws Exception{
    Event event1 = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    EventService groupCreatorEventService = standardUserContext.createEventService();
    groupCreatorEventService.createEvent(event1, group.getId());
    groupCreatorEventService.createEvent(event2, group.getId());

    groupCreatorEventService.addEventModerator(event1, standardUserContext2.getUser());

    EventService standardUserEventService2 = standardUserContext2.createEventService();
    standardUserEventService2.rsvpTpEvent(event2.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData memberData = userMemberService.getUserMemberData();
    assertIterableEquals(Collections.singletonList(event1), memberData.getModeratingEvents());
    assertIterableEquals(Collections.singletonList(event2), memberData.getModeratingGroups());

  }

  @Test
  public void testUserJoinsMultipleGroups_groupsAreInAlphabeticalOrder() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();
    userMemberService.joinGroup(group.getId());
    userMemberService.joinGroup(group2.getId());

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(group,group2),userMemberData.getJoinedGroups());
  }

  @Test
  public void testUserIsModeratorForMultipleGroups_groupsAreInAlphabeticalOrder() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    GroupPermissionService groupPermissionService = standardUserContext.createGroupPermissionService();
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(),group.getId());
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(),group2.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(group,group2),userMemberData.getModeratingGroups());
  }

  @Test
  public void testUserIsModeratorForMultipleEvents_moderatorInformationIsInAlphabeticalOrder() throws Exception{
    Event event1 = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    EventService groupCreatorEventService = standardUserContext.createEventService();
    groupCreatorEventService.createEvent(event1, group.getId());
    groupCreatorEventService.createEvent(event2, group.getId());

    groupCreatorEventService.addEventModerator(event1, standardUserContext2.getUser());
    groupCreatorEventService.addEventModerator(event2, standardUserContext2.getUser());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(event1,event2),userMemberData.getModeratingEvents());
  }

  @Test
  public void testUserHasRsvpToMultipleEvents_eventsAreInChronologicalOrder() throws Exception{
    Event event1 = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();
    Event event3 = EventService.createRecurringEventObject();

    event1.setStartTime(LocalTime.NOON);
    event2.setStartTime(LocalTime.NOON.minusHours(2));
    event3.setStartTime(LocalTime.NOON.plusHours(2));

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    EventService groupCreatorEventService = standardUserContext.createEventService();
    groupCreatorEventService.createEvent(event1, group.getId());
    groupCreatorEventService.createEvent(event2, group.getId());
    groupCreatorEventService.createEvent(event3, group.getId());

    EventService eventService2 = standardUserContext2.createEventService();
    eventService2.rsvpTpEvent(event1.getId());
    eventService2.rsvpTpEvent(event2.getId());
    eventService2.rsvpTpEvent(event3.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(event2,event1,event3),userMemberData.getModeratingEvents());
  }

  @Test
  public void testUserAttemptsToJoinGroupThatDoesNotExistError() throws Exception {
    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        userMemberService.joinGroup(-1);
      }
    );

    assertEquals("Invalid group",exception.getMessage());
  }

  @Test
  public void testUserAttemptsToLeaveGroupThatDoesNotExistError() throws Exception {
    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        userMemberService.leaveGroup(-1);
      }
    );

    assertEquals("Invalid group",exception.getMessage());
  }

  @Test
  public void testUserAttemptsToJoinGroupTwice_secondAttemptIsError() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();
    userMemberService.joinGroup(group.getId());

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        userMemberService.joinGroup(group.getId());
      }
    );

    assertEquals("User is already a member of the group",exception.getMessage());
  }

  @Test
  public void testUserAttemptsToLeaveGroupTheyAreNotAPartOf_IsError() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          userMemberService.leaveGroup(group.getId());
        }
    );

    assertEquals("User is not a member of the group",exception.getMessage());
  }

  @Test
  public void testNonLoggedInUserAttemptsToJoinGroup_unauthorizedError(){

    UserMemberService userMemberService = readOnlyUserContext.createUserMemberService();

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          userMemberService.joinGroup(-1);
        }
    );

    assertEquals("Unauthorized",exception.getMessage());
  }

  @Test
  public void testNonLoggedInUserAttemptsToLeaveGroup_unauthorizedError(){
    UserMemberService userMemberService = readOnlyUserContext.createUserMemberService();

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          userMemberService.leaveGroup(-1);
        }
    );

    assertEquals("Unauthorized",exception.getMessage());
  }

  @Test
  public void testNonLoggedInUser_AttemptsToRetrieveUserData_unauthorizedError(){
    UserMemberService userMemberService = readOnlyUserContext.createUserMemberService();

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          userMemberService.getUserMemberData();
        }
    );

    assertEquals("Unauthorized",exception.getMessage());
  }

  @AfterEach
  public void cleanup() throws Exception{
    String deleteEventAdminQuery =
        "TRUNCATE TABLE event_admin_data CASCADE";
    String deleteGroupAdminQuery =
        "TRUNCATE TABLE group_admin_data CASCADE";

    PreparedStatement query1 = conn.prepareStatement(deleteEventAdminQuery);
    PreparedStatement query2 = conn.prepareStatement(deleteGroupAdminQuery);

    query1.execute();
    query2.execute();
  }
}
