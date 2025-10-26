package app.service.edit;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.Event;
import app.groups.data.Group;
import app.result.error.UnauthorizedError;
import app.result.error.group.InvalidEventParameterError;
import app.users.data.SessionContext;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.EventService;

import java.sql.Connection;
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

    Event created = adminEventService.createEvent(event, group.getId());
    verifyRsvpCount(created.getId(), 0);

    standardUserEventService.rsvpTpEvent(created.getId());
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

    standardUserEventService.rsvpTpEvent(created.getId());
    standardUserEventService.rsvpTpEvent(created2.getId());

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

    standardUserEventService.rsvpTpEvent(created.getId());
    standardUserEventService.rsvpTpEvent(created2.getId());
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

    standardUserEventService.rsvpTpEvent(created.getId());

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

    standardUserEventService.rsvpTpEvent(created.getId());

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 0);
  }

  @Test
  public void testTwoUsersRsvpToEvent_bothRemoveRsvp() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());
    standardUserEventService2.rsvpTpEvent(created.getId());

    standardUserEventService.removeEventRsvp(created.getId());
    standardUserEventService.removeEventRsvp(created.getId());

    verifyRsvpCount(created.getId(), 0);
  }

  @Test
  public void testTwoUsersRsvpToEvent_oneRemovesRsvp() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());

    standardUserEventService.removeEventRsvp(created.getId());
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

    standardUserEventService.rsvpTpEvent(created.getId());
    standardUserEventService2.rsvpTpEvent(created2.getId());

    verifyRsvpCount(created.getId(), 1);
    verifyRsvpCount(created2.getId(), 1);
  }

  @Test
  public void testRsvpToEventTwice_loggedInUser_badRequestErrorOnSecondAttempt() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());

    Exception exception = assertThrows(
        InvalidEventParameterError.class,
        () -> {
          standardUserEventService2.rsvpTpEvent(created.getId());
        }
    );
    assertEquals("User already has event rsvp",exception.getMessage());
  }

  @Test
  public void testRemoveRsvpToEventTwice_badRequestErrorOnSecondAttempt() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());
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
  public void testRsvpToValidEvent_userIsNotLoggedIn_authorizationError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());

    Exception exception = assertThrows(
      UnauthorizedError.class,
      () -> {
        standardUserEventService.rsvpTpEvent(created.getId());
      }
    );
    assertEquals("User must log in to RSVP to an event",exception.getMessage());
  }

  @Test
  public void testRsvpWithInvalidEventId_userIsLoggedIn_badRequestError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());

    Exception exception = assertThrows(
        InvalidEventParameterError.class,
        () -> {
          standardUserEventService.rsvpTpEvent(created.getId()+1);
        }
    );
    assertEquals("Event does not exist",exception.getMessage());
  }

  @Test
  public void testRemoveRsvpToValidEvent_userIsNotLoggedIn_authorizationError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());

    Exception exception = assertThrows(
        InvalidEventParameterError.class,
        () -> {
          readOnlyEventService.removeEventRsvp(created.getId()+1);
        }
    );
    assertEquals("User must log in to renove rsvp to an event",exception.getMessage());
  }

  @Test
  public void testRemoveRsvpWithInvalidEventId_userIsLoggedIn_badRequestError() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = EventService.createRecurringEventObject();
    Event created = adminEventService.createEvent(event, group.getId());

    standardUserEventService.rsvpTpEvent(created.getId());
    standardUserEventService.rsvpTpEvent(created.getId());

    Exception exception = assertThrows(
      InvalidEventParameterError.class,
      () -> {
        standardUserEventService.removeEventRsvp(created.getId());

      }
    );
    assertEquals("Event does not exist",exception.getMessage());
  }

  private static void verifyRsvpCount(int eventId, int expectedCount) throws Exception{
    Optional<Event> eventFromDb = readOnlyEventService.getEvent(eventId);
    assertTrue(eventFromDb.isPresent());
    assertEquals(eventFromDb.get().getRsvpCount(), expectedCount);
  }

}
