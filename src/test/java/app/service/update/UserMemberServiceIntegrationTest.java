package app.service.update;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;

import app.groups.Event;
import app.groups.Group;
import app.result.group.GroupPageData;
import app.users.SessionContext;
import app.users.UserMemberData;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.GroupSearchParams;
import database.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.ReadGroupService;
import service.update.EventService;
import service.update.GroupPermissionService;
import service.update.UserMemberService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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
  public void testStandardUserRsvpToEvent_eventWithNextOccurrenceIsDisplayed_EventIsOnCurrentDay() throws Exception{

    //The test may not produce an accurate result if it is run immediately before midnight
    if(LocalTime.now().equals(LocalTime.MIDNIGHT.minusMinutes(1))){
      System.out.println("Waiting until after midnight to run test");
      Thread.sleep(60000);
    }

    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    LocalTime startTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    DayOfWeek startDay = DayOfWeek.from(LocalDate.now());
    event.setStartTime(startTime);
    event.setDay(startDay.toString());
    Event created = adminEventService.createEvent(event,group.getId());

    EventService standardUserEventService = standardUserContext2.createEventService();
    standardUserEventService.rsvpToEvent(created.getId());

    UserMemberData userGroupMemberData = standardUserContext2.createUserMemberService().getUserMemberData();
    assertIterableEquals(Collections.singletonList(created), userGroupMemberData.getAttendingEvents());

    Event eventFromDb = userGroupMemberData.getAttendingEvents().first();
    assertEquals(event.getId(), eventFromDb.getId());
    assertEquals(group.getId(), eventFromDb.getGroupId());
    assertEquals(startTime, eventFromDb.getStartTime());
    assertEquals(startDay, eventFromDb.getDay());

    LocalDate expectedDate = LocalDate.now();
    LocalDate startDate = eventFromDb.getStartDate();
    assertEquals(expectedDate, startDate);
  }

  @Test
  public void testStandardUserRsvpToEvent_eventWithNextOccurrenceIsDisplayed_EventIsNotOnCurrentDay() throws Exception{

    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    LocalTime startTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    DayOfWeek startDay = DayOfWeek.from(LocalDate.now().minusDays(2));
    event.setStartTime(startTime);
    event.setDay(startDay.toString());
    Event created = adminEventService.createEvent(event,group.getId());

    EventService standardUserEventService = standardUserContext2.createEventService();
    standardUserEventService.rsvpToEvent(created.getId());

    UserMemberData userGroupMemberData = standardUserContext2.createUserMemberService().getUserMemberData();
    assertIterableEquals(Collections.singletonList(created), userGroupMemberData.getAttendingEvents());

    Event eventFromDb = userGroupMemberData.getAttendingEvents().first();
    assertEquals(event.getId(), eventFromDb.getId());
    assertEquals(group.getId(), eventFromDb.getGroupId());
    assertEquals(startTime, eventFromDb.getStartTime());
    assertEquals(startDay, eventFromDb.getDay());

    LocalDate expectedDate = LocalDate.now().with(TemporalAdjusters.next(startDay));
    LocalDate startDate = eventFromDb.getStartDate();
    assertEquals(expectedDate, startDate);
  }

  @Test
  public void testStandardUserRsvpData_doesNotShowPastEventRsvp() throws Exception{
    EventService adminEventService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event,group.getId());

    EventService standardUserEventService = standardUserContext2.createEventService();
    standardUserEventService.rsvpToEventWithTime(created.getId(), LocalDateTime.now().minusDays(20));


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
    standardUserEventService.rsvpToEvent(created.getId());

    LocalTime startTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    DayOfWeek startDay = DayOfWeek.from(LocalDate.now());
    created.setStartTime(startTime);
    created.setDay(startDay.toString());
    adminEventService.updateEvent(created);

    UserMemberData userGroupMemberData = standardUserContext2.createUserMemberService().getUserMemberData();
    assertIterableEquals(EMPTY_EVENT_SET, userGroupMemberData.getAttendingEvents());

    Event eventFromDb = userGroupMemberData.getModeratingEvents().first();
    assertEquals(startTime, eventFromDb.getStartTime());
    assertEquals(startDay, eventFromDb.getDay());

    LocalDate expectedDate = LocalDate.now();
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
    standardUserEventService2.rsvpToEvent(event2.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData memberData = userMemberService.getUserMemberData();
    assertIterableEquals(Collections.singletonList(event1), memberData.getModeratingEvents());
    assertIterableEquals(Collections.singletonList(event2), memberData.getAttendingEvents());
  }

  @Test
  public void testUserJoinsMultipleGroups_groupsAreInAlphabeticalOrder() throws Exception{
    Group group1 = CreateGroupUtils.createGroupWithName(standardUserContext.getUser(),"A"+UUID.randomUUID(), conn);
    Group group2 = CreateGroupUtils.createGroupWithName(standardUserContext.getUser(), "C"+UUID.randomUUID(), conn);
    Group group3 = CreateGroupUtils.createGroupWithName(standardUserContext.getUser(), "B"+UUID.randomUUID(), conn);

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();
    userMemberService.joinGroup(group1.getId());
    userMemberService.joinGroup(group2.getId());
    userMemberService.joinGroup(group3.getId());

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(group1,group3,group2),userMemberData.getJoinedGroups());
  }

  @Test
  public void testUserIsModeratorForMultipleGroups_groupsAreInAlphabeticalOrder() throws Exception{
    Group group1 = CreateGroupUtils.createGroupWithName(
        standardUserContext.getUser(),"Bgroup"+UUID.randomUUID(), conn);
    Group group2 = CreateGroupUtils.createGroupWithName(
        standardUserContext.getUser(),"Agroup"+UUID.randomUUID(), conn);
    Group group3 = CreateGroupUtils.createGroupWithName(
        standardUserContext.getUser(),"Cgroup"+UUID.randomUUID(), conn);

    GroupPermissionService groupPermissionService = standardUserContext.createGroupPermissionService();
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(),group1.getId());
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(),group2.getId());
    groupPermissionService.addGroupModerator(standardUserContext2.getUser(),group3.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();
    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(group2,group1,group3),userMemberData.getModeratingGroups());
  }

  @Test
  public void testUserIsModeratorForMultipleEvents_moderatorInformationIsInAlphabeticalOrder() throws Exception{
    Event event1 = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();
    Event event3 = EventService.createRecurringEventObject();
    event2.setStartTime(LocalTime.NOON.minusHours(1));
    event1.setStartTime(LocalTime.NOON);
    event3.setStartTime(LocalTime.NOON.plusHours(1));

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    EventService groupCreatorEventService = standardUserContext.createEventService();

    Event eventFromDb1 = groupCreatorEventService.createEvent(event1, group.getId());
    Event eventFromDb2 = groupCreatorEventService.createEvent(event2, group.getId());
    Event eventFromDb3 = groupCreatorEventService.createEvent(event3, group.getId());

    groupCreatorEventService.addEventModerator(event1, standardUserContext2.getUser());
    groupCreatorEventService.addEventModerator(event2, standardUserContext2.getUser());
    groupCreatorEventService.addEventModerator(event3, standardUserContext2.getUser());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(eventFromDb2,eventFromDb1, eventFromDb3),userMemberData.getModeratingEvents());
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
    Event eventFromDb1 = groupCreatorEventService.createEvent(event1, group.getId());
    Event eventFromDb2 = groupCreatorEventService.createEvent(event2, group.getId());
    Event eventFromDb3 = groupCreatorEventService.createEvent(event3, group.getId());

    EventService eventService2 = standardUserContext2.createEventService();
    eventService2.rsvpToEvent(event1.getId());
    eventService2.rsvpToEvent(event2.getId());
    eventService2.rsvpToEvent(event3.getId());

    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    UserMemberData userMemberData = userMemberService.getUserMemberData();
    assertIterableEquals(Arrays.asList(eventFromDb2,eventFromDb1,eventFromDb3),userMemberData.getAttendingEvents());
  }

  @Test
  public void testUserJoinsGroup_groupPageShowsCorrectMemberStatus_forUserModerator_andReader() throws Exception{

    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    LinkedHashMap<String, String> readGroupParams = new LinkedHashMap<>();
    readGroupParams.put(GroupSearchParams.NAME, group.getName());

    UserMemberService memberService = standardUserContext2.createUserMemberService();
    memberService.joinGroup(group.getId());

    ReadGroupService standardUserGroupService = standardUserContext2.createReadGroupService();
    GroupPageData standardUserGroupData = standardUserGroupService.getGroupPageData(readGroupParams);
    assertTrue(standardUserGroupData.userIsMember());

    ReadGroupService groupModeratorGroupService = adminContext.createReadGroupService();
    GroupPageData groupModeratorGroupData = groupModeratorGroupService.getGroupPageData(readGroupParams);
    assertTrue(groupModeratorGroupData.userIsMember());

    ReadGroupService readOnlyGroupService = readOnlyUserContext.createReadGroupService();
    GroupPageData readOnlyGroupData = readOnlyGroupService.getGroupPageData(readGroupParams);
    assertFalse(readOnlyGroupData.userIsMember());
  }

  @Test
  public void testUserAttemptsToJoinGroupThatDoesNotExistError() {
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
  public void testUserAttemptsToLeaveGroupThatDoesNotExistError()  {
    UserMemberService userMemberService = standardUserContext2.createUserMemberService();

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        userMemberService.leaveGroup(-1);
      }
    );

    assertEquals("User is not a member of the group",exception.getMessage());
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
