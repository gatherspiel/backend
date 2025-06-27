package app.service.edit;

import app.groups.data.Event;
import app.users.data.User;
import app.groups.data.EventLocation;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.Group;
import app.utils.CreateGroupUtils;
import database.content.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.EventEditService;
import service.user.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

  private static LocalDateTime START_TIME_1 = LocalDateTime.now().plusHours(1);
  private static LocalDateTime END_TIME_1 = LocalDateTime.now().plusHours(5);

  private static LocalDateTime START_TIME_2 = LocalDateTime.now().plusHours(1).plusDays(1);
  private static LocalDateTime END_TIME_2 = LocalDateTime.now().plusHours(5).plusDays(1);

  private static EventLocation location1;
  private static EventLocation location2;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {

      conn = testConnectionProvider.getDatabaseConnection();
      eventEditService = new EventEditService(testConnectionProvider.getDatabaseConnection(), new EventRepository(conn), new GroupPermissionService(conn));
      DbUtils.createTables(conn);

      event1=EventEditService.createEventObject(EVENT_NAME_1, LOCATION_1, SUMMARY_1, URL_1, START_TIME_1, END_TIME_1);
      event2=EventEditService.createEventObject(EVENT_NAME_2, LOCATION_2, SUMMARY_2, URL_2, START_TIME_2, END_TIME_2);

      location1 = new EventLocation();
      location1.setZipCode(2202);
      location1.setStreetAddress("1750 Crystal Drive");
      location1.setState("VA");
      location1.setCity("Arlington");
      event1.setEventLocation(location1);

      location2 = new EventLocation();
      location2.setZipCode(20006);
      location2.setStreetAddress("1667 K St NW");
      location2.setState("DC");
      location2.setCity("Washington");
      event2.setEventLocation(location2);


      UserService createUserService
          = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()));

      admin = createUserService.createAdmin(ADMIN_USERNAME);
      groupAdmin = createUserService.createStandardUser(GROUP_ADMIN_USERNAME);
      standardUser = createUserService.createStandardUser(STANDARD_USER_USERNAME);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testCreateOneEvent() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    Event event = eventEditService.addEvent(event1, group.getId(), admin);
    Event eventFromDb = eventEditService.getEvent(event.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDb.getName());
  }

  @Test
  public void testCreateMultipleEvents() throws Exception{

    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    Event eventA = eventEditService.addEvent(event1, group.getId(), admin);
    Event eventB = eventEditService.addEvent(event2, group.getId(), admin);

    Event eventFromDbA = eventEditService.getEvent(eventA.getId()).get();
    Event eventFromDbB = eventEditService.getEvent(eventB.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDbA.getName());
    assertEquals(EVENT_NAME_2, eventFromDbB.getName());

    assertEquals(SUMMARY_1, eventFromDbA.getDescription());
    assertEquals(SUMMARY_2, eventFromDbB.getDescription());

    assertEquals(eventA.getDay(), eventFromDbA.getDay());
    assertEquals(eventB.getDay(), eventFromDbB.getDay());

    assertEquals(URL_1, eventFromDbA.getUrl());
    assertEquals(URL_2, eventFromDbB.getUrl());

    assertEquals(START_TIME_1.truncatedTo(ChronoUnit.MINUTES), eventFromDbA.getStartTime());
    assertEquals(END_TIME_1.truncatedTo(ChronoUnit.MINUTES), eventFromDbA.getEndTime());

    assertEquals(START_TIME_2.truncatedTo(ChronoUnit.MINUTES), eventFromDbB.getStartTime());
    assertEquals(END_TIME_2.truncatedTo(ChronoUnit.MINUTES), eventFromDbB.getEndTime());
  }

  @Test
  public void testCreateEvent_GroupDoesNotExist() throws Exception {
    Exception exception = assertThrows(
      Exception.class,
      () -> {
        eventEditService.addEvent(event1, -1, admin);
      }
    );
    assertTrue(exception.getMessage().contains("User does not have permission"), exception.getMessage());
  }

  @Test
  public void testCreateOneEvent_AndUpdateEvent() throws Exception{

    Group group1 = CreateGroupUtils.createGroup(groupAdmin, conn);
    Event eventA = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group1.getId(), admin);

    Event updated = EventEditService.createEventObjectWithTestData();
    updated.setId(eventA.getId());

    eventEditService.updateEvent(updated, group1.getId(), admin);
    Event eventFromDbA = eventEditService.getEvent(eventA.getId()).get();
    assertEquals(updated.getName(),eventFromDbA.getName());
  }

  @Test
  public void testCreateMultipleEventsToDifferentGroups_AndFirstOneIsChanged() throws Exception{

    Group group1 = CreateGroupUtils.createGroup(groupAdmin, conn);
    Group group2 = CreateGroupUtils.createGroup(groupAdmin, conn);

    Event eventA = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group1.getId(), admin);
    Event eventB = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group2.getId(), admin);

    Event updated = EventEditService.createEventObjectWithTestData();
    updated.setId(eventA.getId());
    updated.setName("Test");


    eventEditService.updateEvent(updated, group1.getId(), admin);
    Event eventFromDbA = eventEditService.getEvent(eventA.getId()).get();
    Event eventFromDbB = eventEditService.getEvent(eventB.getId()).get();

    assertEquals(updated.getName(),eventFromDbA.getName());
    assertEquals(eventB.getName(), eventFromDbB.getName());
  }

  @Test
  public void testCreateMultipleEvents_GroupPageData_HasCorrectEvents() throws Exception {

    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    var eventA = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group.getId(), admin);
    var eventB = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group.getId(), admin);
    var eventC = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group.getId(), admin);

    ReadGroupService readGroupService = new ReadGroupService(ReadGroupDataProvider.create(conn), conn);
    Group groupFromDb = readGroupService.getGroup(group.getId()).get();

    assertEquals(groupFromDb.events.length, 3);

    Set<Integer> ids = new HashSet<>();
    for(int i =0; i<groupFromDb.events.length; i++){
      ids.add(groupFromDb.events[i].getId());
    }

    var expected = new HashSet<>(Arrays.asList(eventA.getId(), eventB.getId(), eventC.getId()));
    assertEquals(ids, expected);
  }
  @Test
  public void testGroupAdmin_CanEditEvent_ForTheirGroup() throws Exception{

    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);
    Event event = eventEditService.addEvent(EventEditService.createEventObjectWithTestData(), group.getId(), admin);

    Event updatedEventData = EventEditService.createEventObjectWithTestData();
    updatedEventData.setId(event.getId());

    eventEditService.updateEvent(updatedEventData, group.getId(),groupAdmin);

    Event eventFromDb = eventEditService.getEvent(event.getId()).get();
    assertEquals(eventFromDb.getId(), event.getId());
  }

  @Test
  public void testGroupAdmin_CannotEditEvent_ForOtherGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    Group group2 = CreateGroupUtils.createGroup(admin, conn);
    eventEditService.addEvent(event2, group2.getId(), admin);

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        Event updated = EventEditService.createEventObjectWithTestData();
        eventEditService.updateEvent(updated, group2.getId(), groupAdmin);
      }
    );
    assertTrue(exception.getMessage().contains("does not have permission"),exception.getMessage());
  }

  @Test
  public void testStandardUser_CannotEditEvent() throws Exception {
    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    Event event = eventEditService.addEvent(event1, group.getId(), admin);
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          Event updated = EventEditService.createEventObjectWithTestData();
          eventEditService.updateEvent(updated, event.getId(), standardUser);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"),exception.getMessage());
  }

  @Test
  public void testGroupAdmin_CanDeleteEvent_ForTheirGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    Event event = eventEditService.addEvent(event1, group.getId(), admin);

    eventEditService.deleteEvent(event, group.getId(), groupAdmin);
    Optional<Event> groupEvent = eventEditService.getEvent(event1.getId());
    assertTrue(groupEvent.isEmpty());
  }

  @Test
  public void testGroupAdmin_CannotDeleteEvent_ForOtherGroup() throws Exception{

    CreateGroupUtils.createGroup(groupAdmin, conn);
    Group group2 = CreateGroupUtils.createGroup(admin, conn);

    Event event = eventEditService.addEvent(event1, group2.getId(), admin);
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          eventEditService.deleteEvent(event, group2.getId(), groupAdmin);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testStandardUser_CannotDeleteEvent() throws Exception {
    Group group = CreateGroupUtils.createGroup(groupAdmin, conn);

    Event event = eventEditService.addEvent(event1, group.getId(), admin);
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          eventEditService.deleteEvent(event, group.getId(), standardUser);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @AfterEach
  public void deleteAllEvents() throws Exception{
    String deleteEventQuery =
        "TRUNCATE TABLE events CASCADE";
    String deleteEventGroupMapQuery =
        "TRUNCATE TABLE events CASCADE";
    String deleteGroupQuery =
        "TRUNCATE TABLE groups CASCADE";

    PreparedStatement query1 = conn.prepareStatement(deleteEventQuery);
    PreparedStatement query2 = conn.prepareStatement(deleteEventGroupMapQuery);
    PreparedStatement query3 = conn.prepareStatement(deleteGroupQuery);

    query1.execute();
    query2.execute();
    query3.execute();
  }
}
