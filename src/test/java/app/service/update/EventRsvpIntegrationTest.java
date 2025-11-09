package app.service.update;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.Event;
import app.groups.Group;
import app.result.error.UnauthorizedError;
import app.result.error.group.InvalidEventParameterError;
import app.users.PermissionName;
import app.users.SessionContext;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.EventService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EventRsvpIntegrationTest {

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static SessionContext adminContext;
  private static SessionContext readOnlyUserContext;
  private static SessionContext standardUserContext;
  private static SessionContext standardUserContext2;

  private static EventService adminEventService;
  private static EventService standardUserEventService;
  private static EventService standardUserEventService2;
  private static EventService readOnlyEventService;

  private static final String ADMIN_USERNAME = "admin";
  private static final String READER_USERNAME = "reader";
  private static final String STANDARD_USER_USERNAME = "standardUser";

  @BeforeAll
  static void setup(){
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);

      adminContext = CreateUserUtils.createContextWithNewAdminUser(ADMIN_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      readOnlyUserContext = CreateUserUtils.createContextWithNewReadonlyUser(READER_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(STANDARD_USER_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      standardUserContext2 = CreateUserUtils.createContextWithNewStandardUser(STANDARD_USER_USERNAME+ UUID.randomUUID(), testConnectionProvider);

      adminEventService = adminContext.createEventService();
      standardUserEventService = standardUserContext.createEventService();
      standardUserEventService2 = standardUserContext2.createEventService();
      readOnlyEventService = readOnlyUserContext.createEventService();

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
    }
  }

  @Test
  public void testEventWithoutRsvp_correctRsvpCount() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    verifyRsvpCount(created.getId(), 0);
  }

  @Test
  public void testRsvpToValidEvent_andRemoveRsvp_loggedInUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();
    event.setDay(LocalDateTime.now().minusDays(1).getDayOfWeek().toString());

    Event created = adminEventService.createEvent(event, group.getId());
    verifyRsvpCount(created.getId(), 0);

    standardUserEventService.rsvpToEvent(created.getId());
    verifyRsvpCount(created.getId(), 1);

    standardUserEventService.removeEventRsvp(created.getId());
    verifyRsvpCount(created.getId(),0);
  }

  @Test
  public void testRsvpToTwoEventsInSameGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    Event created2 = adminEventService.createEvent(event2, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService.rsvpToEvent(created2.getId());

    created = standardUserEventService.getEvent(created.getId()).get();
    created2 = standardUserEventService.getEvent(created2.getId()).get();

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 1);
  }

  @Test
  public void testRsvpToTwoEventsInSameGroup_AndRemoveRsvpForOneEvent() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    Event created2 = adminEventService.createEvent(event2, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService.rsvpToEvent(created2.getId());
    standardUserEventService.removeEventRsvp(created2.getId());

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 0);
  }

  @Test
  public void testRsvpToOneEvent_InGroupThatHasTwoEvents_rsvpIsForCorrectEvent() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    Event created2 = adminEventService.createEvent(event2, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 0);
  }

  @Test
  public void testTwoGroupsWithOneEventEach_rsvpToOneEvent_rsvpIsForCorrectEvent() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    Event created2 = adminEventService.createEvent(event2, group2.getId());

    standardUserEventService.rsvpToEvent(created.getId());

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 0);
  }

  @Test
  public void testTwoUsersRsvpToEvent_bothRemoveRsvp_eventShowsCorrectRsvpStatusesForUsers() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService2.rsvpToEvent(created.getId());

    Optional<Event> userEventData = standardUserEventService.getEvent(created.getId());
    Optional<Event> userEventData2 = standardUserEventService2.getEvent(created.getId());

    assertTrue(userEventData.isPresent());
    assertTrue(userEventData2.isPresent());
    assertTrue(userEventData.get().getUserHasRsvp());
    assertTrue(userEventData2.get().getUserHasRsvp());
    assertEquals(2, userEventData.get().getRsvpCount());
    assertEquals(2, userEventData.get().getRsvpCount());

    standardUserEventService.removeEventRsvp(created.getId());
    standardUserEventService2.removeEventRsvp(created.getId());

    userEventData = standardUserEventService.getEvent(created.getId());
    userEventData2 = standardUserEventService2.getEvent(created.getId());

    assertTrue(userEventData.isPresent());
    assertTrue(userEventData2.isPresent());
    assertFalse(userEventData.get().getUserHasRsvp());
    assertFalse(userEventData2.get().getUserHasRsvp());
    assertEquals(0, userEventData.get().getRsvpCount());
    assertEquals(0, userEventData.get().getRsvpCount());
  }

  @Test
  public void testTwoUsersRsvpToEvent_oneRemovesRsvp() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService2.rsvpToEvent(created.getId());

    standardUserEventService.removeEventRsvp(created.getId());

    verifyRsvpCount(created.getId(), 1);
  }

  @Test
  public void testGroupWithTwoEvents_oneUserRsvpsToEachEvent_eachEventHasOneRsvp() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event event2 = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    Event created2 = adminEventService.createEvent(event2, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService2.rsvpToEvent(created2.getId());

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 1);
  }

  @Test
  public void testStandardUserWithRsvpToEvent_cannotModifyEvent() throws Exception {

    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    Event event = EventService.createRecurringEventObject();

    Event created = adminEventService.createEvent(event, group.getId());
    standardUserEventService.rsvpToEvent(created.getId());

    Optional<Event> eventFromDb = standardUserEventService.getEvent(created.getId());
    assertTrue(eventFromDb.isPresent());
    assertFalse(eventFromDb.get().getPermissions().get(PermissionName.USER_CAN_EDIT));

    created.setDescription(event.getDescription()+" Update");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          standardUserEventService.updateEvent(event);;

        }
    );
    assertEquals(exception.getMessage(), "User does not have permission to modify event");
  }


  @Test
  public void testRsvpToEventTwice_loggedInUser_oneRsvpSaved() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService.rsvpToEvent(created.getId());
    verifyRsvpCount(created.getId(),1);
  }

  @Test
  public void testEventModeratorCannotUpdateEventRsvp() throws Exception {
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());
    adminEventService.addEventModerator(created, standardUserContext.getUser());

    EventService standardUserEventService = standardUserContext.createEventService();

    Optional<Event> standardUserEventFromDb = standardUserEventService.getEvent(created.getId());
    assertTrue(standardUserEventFromDb.isPresent());
    assertFalse(standardUserEventFromDb.get().getUserCanUpdateRsvp());
  }


  @Test
  public void testRemoveRsvpToEventTwice_badRequestErrorOnSecondAttempt() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());
    standardUserEventService.removeEventRsvp(created.getId());

    Exception exception = assertThrows(
        InvalidEventParameterError.class,
        () -> {
          standardUserEventService.removeEventRsvp(created.getId());
        }
    );
    assertEquals("User does not have rsvp for event",exception.getMessage());
  }

  @Test
  public void testUserIsNotLoggedIn_eventDataShowsUpdateRsvpPermissionDisabled() throws Exception {
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    Optional<Event> readOnlyEvent = readOnlyEventService.getEvent(created.getId());

    assertTrue(readOnlyEvent.isPresent());
    assertFalse(readOnlyEvent.get().getUserCanUpdateRsvp());
  }
  @Test
  public void testRsvpToValidEvent_userIsNotLoggedIn_authorizationError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    Exception exception = assertThrows(
      UnauthorizedError.class,
      () -> {
        readOnlyEventService.rsvpToEvent(created.getId());
      }
    );
    assertEquals("User must log in to rsvp to event",exception.getMessage());
  }

  @Test
  public void testRsvpWithInvalidEventId_userIsLoggedIn_badRequestError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());

    Exception exception = assertThrows(
        InvalidEventParameterError.class,
        () -> {
          standardUserEventService.rsvpToEvent(created.getId()+1);
        }
    );
    assertEquals("Event does not exist",exception.getMessage());
  }

  @Test
  public void testRemoveRsvpToValidEvent_userIsNotLoggedIn_authorizationError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpToEvent(created.getId());

    Exception exception = assertThrows(
        UnauthorizedError.class,
        () -> {
          readOnlyEventService.removeEventRsvp(created.getId()+1);
        }
    );
    assertEquals("User must log in to remove event rsvp",exception.getMessage());
  }

  @Test
  public void testRemoveRsvpWithInvalidEventId_userIsLoggedIn_badRequestError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    Exception exception = assertThrows(
      InvalidEventParameterError.class,
      () -> {
        standardUserEventService.removeEventRsvp(created.getId()+11);

      }
    );
    assertEquals("User does not have rsvp for event",exception.getMessage());
  }

  @Test
  public void testRsvpBeforeAndAfterLastEvent_OnlyOneRsvpReturned() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    EventService standardUserEventService = standardUserContext.createEventService();
    EventService standardUserEventService2 = standardUserContext2.createEventService();

    standardUserEventService.rsvpToEvent(created.getId());
    Thread.sleep(1000);

    LocalDateTime now = LocalDateTime.now();
    created.setDay(now.getDayOfWeek().toString());
    created.setStartTime(now.toLocalTime());
    created.setEndTime(now.toLocalTime().plusHours(2));
    adminEventService.updateEvent(created);

    Thread.sleep(1000);
    standardUserEventService2.rsvpToEvent(created.getId());

    verifyRsvpCount(created.getId(),1);
  }

  @Test
  public void testRsvpBeforeLastEvent_updateRsvpAfterEventOneRsvp() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    EventService standardUserEventService = standardUserContext.createEventService();

    standardUserEventService.rsvpToEvent(created.getId());

    Thread.sleep(1000);

    LocalDateTime now = LocalDateTime.now();
    created.setDay(now.getDayOfWeek().toString());
    created.setStartTime(now.toLocalTime());
    created.setEndTime(now.toLocalTime().plusHours(2));
    adminEventService.updateEvent(created);

    Thread.sleep(1000);
    standardUserEventService.rsvpToEvent(created.getId());

    verifyRsvpCount(created.getId(),1);
  }

  @AfterEach
  public void deleteRsvpData() throws Exception {

    String deleteEventQuery =
        "TRUNCATE TABLE events CASCADE";
    String deleteEventGroupMapQuery =
        "TRUNCATE TABLE events CASCADE";
    String deleteGroupQuery =
        "TRUNCATE TABLE groups CASCADE";
    String deleteEventAdminQuery =
        "TRUNCATE TABLE event_admin_data CASCADE";
    String deleteGroupAdminQuery =
        "TRUNCATE TABLE group_admin_data CASCADE";


    PreparedStatement query1 = conn.prepareStatement(deleteEventQuery);
    PreparedStatement query2 = conn.prepareStatement(deleteEventGroupMapQuery);
    PreparedStatement query3 = conn.prepareStatement(deleteGroupQuery);
    PreparedStatement query4 = conn.prepareStatement(deleteEventAdminQuery);
    PreparedStatement query5 = conn.prepareStatement(deleteGroupAdminQuery);

    query1.execute();
    query2.execute();
    query3.execute();
    query4.execute();
    query5.execute();
  }

  private static void verifyRsvpCount(int eventId, int expectedCount) throws Exception{
    Optional<Event> eventFromDb = readOnlyEventService.getEvent(eventId);
    assertTrue(eventFromDb.isPresent());
    assertEquals(expectedCount,eventFromDb.get().getRsvpCount());
  }

}
