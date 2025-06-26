package app.service.edit;

import app.data.Event;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.Group;
import app.utils.CreateGroupUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.EventEditService;
import service.user.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EventEditServiceIntegrationTest {

  private static final String EVENT_NAME_1="Catan Event";
  private static final String EVENT_NAME_2="Power Grid";

  private static Event event1;
  private static Event event2;

  private static String DAY_1 = "Monday";
  private static String DAY_2 = "Tuesday";

  private static final String LOCATION_1 ="Crystal City";
  private static final String LOCATION_2="Ballston";

  private static final String SUMMARY_1="Resource management and trading Euro";
  private static final String SUMMARY_2="Come play Power Grid";

  private static final String URL_1="http://localhost:8000";
  private static final String URL_2="http://localhost:8001";


  private static final String ADMIN_USERNAME = "unitTest";
  private static final String GROUP_ADMIN_USERNAME = "groupAdmin";
  private static final String STANDARD_USER_USERNAME = "testUser";


  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static EventEditService eventEditService;
  private static Connection conn;

  private static User admin;
  private static User groupAdmin;
  private static User standardUser;


  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {

      conn = testConnectionProvider.getDatabaseConnection();
      eventEditService = new EventEditService();
      DbUtils.createTables(conn);

      event1=EventEditService.createEventObject(EVENT_NAME_1, DAY_1, LOCATION_1, SUMMARY_1, URL_1);
      event2=EventEditService.createEventObject(EVENT_NAME_2, DAY_2, LOCATION_2, SUMMARY_2, URL_1);

      UserService createUserService
          = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()));

      admin = createUserService.createAdmin(ADMIN_USERNAME);
      groupAdmin = createUserService.createAdmin(GROUP_ADMIN_USERNAME);
      standardUser = createUserService.createStandardUser(STANDARD_USER_USERNAME);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @BeforeEach
  public void createGroup() throws Exception{
    CreateGroupUtils.createGroup(groupAdmin, testConnectionProvider);
  }
  @Test
  public void testCreateOneEvent() throws Exception{
    Event event = eventEditService.addEvent(event1, 1, admin);
    Event eventFromDb = eventEditService.getEvent(event.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDb.getName());
  }

  @Test
  public void testCreateMultipleEvents() throws Exception{
    Event eventA = eventEditService.addEvent(event1, 1, admin);
    Event eventB = eventEditService.addEvent(event2, 1, admin);

    Event eventFromDbA = eventEditService.getEvent(eventA.getId()).get();
    Event eventFromDbB = eventEditService.getEvent(eventB.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDbA.getName());
    assertEquals(EVENT_NAME_2, eventFromDbB.getName());

    assertEquals(EVENT_NAME_1, eventFromDbA.getSummary());
    assertEquals(EVENT_NAME_2, eventFromDbB.getSummary());

    assertEquals(EVENT_NAME_1, eventFromDbA.getDay());
    assertEquals(EVENT_NAME_2, eventFromDbB.getDay());

    assertEquals(EVENT_NAME_1, eventFromDbA.getLocation());
    assertEquals(EVENT_NAME_2, eventFromDbB.getLocation());

    assertEquals(EVENT_NAME_1, eventFromDbA.getUrl());
    assertEquals(EVENT_NAME_2, eventFromDbB.getUrl());
  }

  @Test
  public void testCreateEvent_GroupDoesNotExist() throws Exception {
    Exception exception = assertThrows(
      Exception.class,
      () -> {
        eventEditService.addEvent(event1, 1, admin);
      }
    );
    assertTrue(exception.getMessage().contains("Cannot add event for group that does not exist"));
  }

  @Test
  public void testCreateMultipleEventsToDifferentGroups_AndFirstOneIsChanged() throws Exception{
    Event eventA = eventEditService.addEvent(event1, 1, admin);
    Event eventB = eventEditService.addEvent(event2, 1, admin);

    Event event = EventEditService.createEventObject(EVENT_NAME_1+"_updated",DAY_1, LOCATION_1, SUMMARY_1, URL_1);

    eventEditService.updateEvent(event, eventA.getId(), admin);
    Event eventFromDbA = eventEditService.getEvent(eventA.getId()).get();
    Event eventFromDbB = eventEditService.getEvent(eventB.getId()).get();

    assertEquals(EVENT_NAME_1,eventFromDbA.getName());
    assertEquals(EVENT_NAME_2, eventFromDbB.getName());
  }

  @Test
  public void testCreateMultipleEvents_GroupPageData_HasCorrectEvents() throws Exception {

    eventEditService.addEvent(new Event(), 1, admin);
    eventEditService.addEvent(new Event(), 1, admin);
    eventEditService.addEvent(new Event(), 1, admin);

    
    ReadGroupService readGroupService = new ReadGroupService(ReadGroupDataProvider.create());
    Group group = readGroupService.getGroup(1, testConnectionProvider).get();

    assertEquals(group.events.length, 3);

    Set<Integer> ids = new HashSet<>();
    for(int i =0; i<group.events.length; i++){
      ids.add(group.events[i].getId());
    }

    var expected = new HashSet<>(Arrays.asList(1,2,3));
    assertEquals(ids, expected);
  }
  @Test
  public void testGroupAdmin_CanEditEvent_ForTheirGroup() throws Exception{
    Event event = eventEditService.addEvent(new Event(), 1, admin);

    Event savedEventData = EventEditService.createEventObject(EVENT_NAME_1+"_updated", DAY_1, LOCATION_1, SUMMARY_1, URL_1);
    eventEditService.updateEvent(savedEventData, event.getId(),groupAdmin);

    Event eventFromDb = eventEditService.getEvent(event1.getId()).get();
    assertEquals(eventFromDb.getName(), EVENT_NAME_1+"_updated");
  }

  @Test
  public void testGroupAdmin_CannotEditEvent_ForOtherGroup() throws Exception{

    CreateGroupUtils.createGroup(admin, testConnectionProvider);
    eventEditService.addEvent(event2, 2, admin);

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        Event updated = EventEditService.createEventObject("Catan event", DAY_1, LOCATION_1, SUMMARY_1, URL_1);
        eventEditService.updateEvent(updated, event2.getId(), groupAdmin);
      }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testStandardUser_CannotEditEvent() throws Exception {
    Event event = eventEditService.addEvent(event1, 1, admin);
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          Event updated = EventEditService.createEventObject("Catan event", DAY_1, LOCATION_1, SUMMARY_1, URL_1);
          eventEditService.updateEvent(updated, event.getId(), groupAdmin);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupAdmin_CanDeleteEvent_ForTheirGroup() throws Exception{
    Event event = eventEditService.addEvent(event1, 1, admin);

    eventEditService.deleteEvent(event.getId(), groupAdmin);
    Optional<Event> groupEvent = eventEditService.getEvent(event1.getId());
    assertTrue(groupEvent.isEmpty());
  }

  @Test
  public void testGroupAdmin_CannotDeleteEvent_ForOtherGroup() throws Exception{
    Event event = eventEditService.addEvent(event1, 2, admin);
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          eventEditService.deleteEvent(event.getId(), groupAdmin);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testStandardUser_CannotDeleteEvent() throws Exception {
    Event event = eventEditService.addEvent(event1, 1, admin);
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          eventEditService.deleteEvent(event.getId(), standardUser);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @AfterEach
  public void deleteAllEvents() throws Exception{
    String deleteEventQuery =
        "TRUNCATE TABLE events";
    String deleteEventGroupMapQuery =
        "TRUNCATE TABLE events";
    String deleteGroupQuery =
        "TRUNCATE TABLE groups";

    PreparedStatement query1 = conn.prepareStatement(deleteEventQuery);
    PreparedStatement query2 = conn.prepareStatement(deleteEventGroupMapQuery);
    PreparedStatement query3 = conn.prepareStatement(deleteGroupQuery);

    query1.execute();
    query2.execute();
    query3.execute();
  }




}
