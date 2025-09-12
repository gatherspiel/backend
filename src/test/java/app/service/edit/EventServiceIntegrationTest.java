package app.service.edit;

import app.SessionContext;
import app.groups.data.*;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.result.group.GroupPageData;
import app.users.data.PermissionName;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.GroupSearchParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.EventService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceIntegrationTest {

  private static Event event1;
  private static Event event2;

  private static final String EVENT_NAME_1="Catan Event";
  private static final String EVENT_NAME_2="Power Grid";

  private static final String LOCATION_1 ="Crystal City";
  private static final String LOCATION_2="Ballston";

  private static final String SUMMARY_1="Resource management and trading Euro";
  private static final String SUMMARY_2="Come play Power Grid";

  private static final String URL_1="http://localhost:8000";
  private static final String URL_2="http://localhost:8001";


  private static final String ADMIN_USERNAME = "unitTest";
  private static final String GROUP_ADMIN_USERNAME = "groupAdmin";
  private static final String STANDARD_USER_USERNAME = "testUser";

  private static final LocalDateTime START_TIME_1 = LocalDateTime.now().plusHours(1);
  private static final LocalDateTime END_TIME_1 = LocalDateTime.now().plusHours(5);

  private static final LocalDateTime START_TIME_2 = LocalDateTime.now().plusHours(1).plusDays(1);
  private static final LocalDateTime END_TIME_2 = LocalDateTime.now().plusHours(5).plusDays(1);



  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static SessionContext adminContext;
  private static SessionContext groupAdminContext;
  private static SessionContext standardUserContext;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    EventLocation location1;
    EventLocation location2;
    try {

      conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);

      event1= EventService.createEventObject(EVENT_NAME_1, LOCATION_1, SUMMARY_1, URL_1, START_TIME_1, END_TIME_1);
      event2= EventService.createEventObject(EVENT_NAME_2, LOCATION_2, SUMMARY_2, URL_2, START_TIME_2, END_TIME_2);

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

      adminContext = CreateUserUtils.createContextWithNewAdminUser(ADMIN_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      groupAdminContext = CreateUserUtils.createContextWithNewStandardUser(GROUP_ADMIN_USERNAME+ UUID.randomUUID(), testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(STANDARD_USER_USERNAME+ UUID.randomUUID(), testConnectionProvider);


    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testCreateOneEvent() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    EventService oneTimeEventService = adminContext.createEventService();
    Event event = oneTimeEventService.createEvent(event1, group.getId());
    Event eventFromDb = oneTimeEventService.getEvent(event.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDb.getName());
  }


  @Test
  public void testCreateMultipleOneTimeEvents_EventsHaveCorrectData() throws Exception{

    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    EventService oneTimeEventService = groupAdminContext.createEventService();

    Event eventA = oneTimeEventService.createEvent(event1, group.getId());
    Event eventB = oneTimeEventService.createEvent(event2, group.getId());

    Event eventFromDbA = oneTimeEventService.getEvent(eventA.getId()).get();
    Event eventFromDbB = oneTimeEventService.getEvent(eventB.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDbA.getName());
    assertEquals(EVENT_NAME_2, eventFromDbB.getName());

    assertEquals(SUMMARY_1, eventFromDbA.getDescription());
    assertEquals(SUMMARY_2, eventFromDbB.getDescription());

    assertEquals(eventA.getDay(), eventFromDbA.getDay());
    assertEquals(eventB.getDay(), eventFromDbB.getDay());

    assertEquals(URL_1, eventFromDbA.getUrl());
    assertEquals(URL_2, eventFromDbB.getUrl());

    assertEquals(START_TIME_1.truncatedTo(ChronoUnit.MINUTES).toLocalTime(), eventFromDbA.getStartTime());
    assertEquals(START_TIME_2.truncatedTo(ChronoUnit.MINUTES).toLocalTime(), eventFromDbB.getStartTime());

    assertEquals(group.getId(), eventFromDbA.getGroupId());
    assertEquals(group.getId(), eventFromDbB.getGroupId());

    assertEquals(group.getName(), eventFromDbA.getGroupName());
    assertEquals(group.getName(), eventFromDbB.getGroupName());

    assertTrue(eventFromDbA.getPermissions().get(PermissionName.USER_CAN_EDIT));
    assertTrue(eventFromDbB.getPermissions().get(PermissionName.USER_CAN_EDIT));
  }

  @Test
  public void testCreateOneTimeEventAndRecurringEvent_EventsHaveCorrectData() throws Exception {
    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    EventService eventService = groupAdminContext.createEventService();

    final LocalTime startTime = LocalTime.NOON.plusHours(2);
    final LocalTime endTime = LocalTime.NOON.plusHours(4).plusMinutes(30);
    EventLocation eventLocation = new EventLocation();
    eventLocation.setZipCode(20006);
    eventLocation.setStreetAddress("1667 K St NW");
    eventLocation.setState("DC");
    eventLocation.setCity("Washington");

    Event recurringEvent = EventService.createRecurringEventObjectWithData(startTime, endTime);
    recurringEvent.setEventLocation(eventLocation);

    eventService.createEvent(event1, group.getId());
    eventService.createEvent(recurringEvent, group.getId());

    Event eventFromDbA = eventService.getEvent(event1.getId()).get();
    Event recurringEventFromDb = eventService.getEvent(recurringEvent.getId()).get();

    assertEquals(EVENT_NAME_1, eventFromDbA.getName());
    assertEquals(recurringEvent.getName(), recurringEventFromDb.getName());

    assertEquals(SUMMARY_1, eventFromDbA.getDescription());
    assertEquals(recurringEvent.getDescription(), recurringEventFromDb.getDescription());

    assertEquals(event1.getDay(), eventFromDbA.getDay());
    assertEquals(recurringEvent.getDay(), recurringEventFromDb.getDay());

    assertEquals(URL_1, eventFromDbA.getUrl());
    assertEquals(recurringEvent.getUrl(), recurringEventFromDb.getUrl());

    assertEquals(START_TIME_1.truncatedTo(ChronoUnit.MINUTES).toLocalTime(), eventFromDbA.getStartTime());
    assertEquals(recurringEvent.getStartTime(), recurringEventFromDb.getStartTime());

    assertEquals(START_TIME_1.truncatedTo(ChronoUnit.MINUTES).toLocalDate(), eventFromDbA.getStartDate());
    assertNull(recurringEventFromDb.getStartDate());

    assertEquals(END_TIME_1.truncatedTo(ChronoUnit.MINUTES).toLocalDate(), eventFromDbA.getEndDate());
    assertEquals(recurringEvent.getEndDate(), recurringEventFromDb.getEndDate());

    assertEquals(END_TIME_1.truncatedTo(ChronoUnit.MINUTES).toLocalTime(), eventFromDbA.getEndTime());
    assertEquals(recurringEvent.getEndTime(), recurringEventFromDb.getEndTime());

    assertEquals(group.getId(), eventFromDbA.getGroupId());
    assertEquals(group.getId(), recurringEventFromDb.getGroupId());

    assertEquals(group.getName(), eventFromDbA.getGroupName());
    assertEquals(group.getName(), recurringEventFromDb.getGroupName());

    assertTrue(eventFromDbA.getPermissions().get(PermissionName.USER_CAN_EDIT));
    assertTrue(recurringEventFromDb.getPermissions().get(PermissionName.USER_CAN_EDIT));

    assertFalse(eventFromDbA.getIsRecurring());
    assertTrue(recurringEventFromDb.getIsRecurring());
  }

  @Test
  public void testCreateEvent_GroupDoesNotExist() throws Exception {
    Exception exception = assertThrows(
      Exception.class,
      () -> {
        EventService oneTimeEventService = groupAdminContext.createEventService();
        oneTimeEventService.createEvent(event1, -1);
      }
    );
    assertTrue(exception.getMessage().contains("not found"), exception.getMessage());
  }

  @Test
  public void testCreateOneTimeEvent_AndEditEvent() throws Exception{

    Group group1 = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    EventService oneTimeEventService = groupAdminContext.createEventService();
    Event eventA = oneTimeEventService.createEvent(EventService.createOneTimeEventObjectWithData(), group1.getId());

    Event updated = EventService.createOneTimeEventObjectWithData();
    updated.setId(eventA.getId());

    oneTimeEventService.updateEvent(updated, group1.getId());
    Event eventFromDbA = oneTimeEventService.getEvent(eventA.getId()).get();

    assertEquals(updated.getName(),eventFromDbA.getName());
    assertEquals(updated.getStartTime(), eventFromDbA.getStartTime());
    assertEquals(updated.getEndTime(), eventFromDbA.getEndTime());
  }

  @Test
  public void testCreateRecurringEvent_AndEditEvent() throws Exception{

    Group group1 = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    EventService eventService = groupAdminContext.createEventService();
    Event recurring = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);
    recurring.setDay("Monday");

    Event created = eventService.createEvent(recurring, group1.getId());

    Event updated = EventService.createRecurringEventObjectWithData(
        LocalTime.NOON.plusHours(6),
        LocalTime.NOON.plusHours(9));
    updated.setDay("Friday");
    updated.setId(created.getId());

    System.out.println("Updating event with id:"+updated.getId());

    eventService.updateEvent(updated, group1.getId());
    Event eventFromDbA = eventService.getEvent(updated.getId()).get();

    assertEquals(updated.getName(),eventFromDbA.getName());
    assertEquals(LocalTime.NOON.plusHours(6), eventFromDbA.getStartTime());
    assertEquals(LocalTime.NOON.plusHours(9), eventFromDbA.getEndTime());
    assertEquals(DayOfWeek.FRIDAY, eventFromDbA.getDay());

  }

  @Test
  public void testCreateMultipleEventsToDifferentGroups_AndFirstOneIsChanged() throws Exception{

    Group group1 = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    EventService oneTimeEventService = groupAdminContext.createEventService();

    Event eventA = oneTimeEventService.createEvent(EventService.createOneTimeEventObjectWithData(), group1.getId());
    Event eventB = oneTimeEventService.createEvent(EventService.createOneTimeEventObjectWithData(), group2.getId());

    Event updated = EventService.createOneTimeEventObjectWithData();
    updated.setId(eventA.getId());
    updated.setName("Test");


    oneTimeEventService.updateEvent(updated, group1.getId());
    Event eventFromDbA = oneTimeEventService.getEvent(eventA.getId()).get();
    Event eventFromDbB = oneTimeEventService.getEvent(eventB.getId()).get();

    assertEquals(updated.getName(),eventFromDbA.getName());
    assertEquals(eventB.getName(), eventFromDbB.getName());
  }

  @Test
  public void testCreateMultipleEvents_GroupPageData_HasCorrectEvents() throws Exception {

    var eventEditService = adminContext.createEventService();

    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    ArrayList<Event> eventsToAdd = new ArrayList<>();
    for(int i =0; i< 3;i++){
      var eventData= EventService.createOneTimeEventObjectWithData();
      var startTime = LocalDateTime.now().plusHours(i);;
      var endTime  = LocalDateTime.now().plusHours(2+i);
      eventData.setStartTime(startTime.toLocalTime());
      eventData.setStartDate(startTime.toLocalDate());

      eventData.setEndDate(endTime.toLocalDate());
      eventData.setEndTime(endTime.toLocalTime());
      eventsToAdd.add(eventData);
    }

    HashMap<Integer,Event> createdEvents = new HashMap<>();
    for(Event event: eventsToAdd){
      var createdEvent = eventEditService.createEvent(event, group.getId());
      createdEvents.put(createdEvent.getId(), createdEvent);
    }

    var groupService = adminContext.createReadGroupService();

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.getName());

    GroupPageData data = groupService.getGroupPageData(params);

    var groupEvents = data.getOneTimeEventData();
    assertEquals(groupEvents.size(), 3);

    Set<Integer> ids = new HashSet<>();
    for(Event eventData: data.getOneTimeEventData()){
      var eventId = eventData.getId();
      ids.add(eventId);

      var startTime = eventData.getStartTime();
      var expectedStartTime = createdEvents.get(eventId).getStartTime();
      assertEquals(startTime, expectedStartTime);

      var endTime = eventData.getEndTime();
      var expectedEndTime = createdEvents.get(eventId).getEndTime();
      assertEquals(endTime, expectedEndTime);
    }

    assertEquals(ids, createdEvents.keySet());
  }

  @Test
  public void testGroupAdmin_CanEditEvent_ForTheirGroup() throws Exception{

    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    var eventEditService = groupAdminContext.createEventService();
    Event event = eventEditService.createEvent(EventService.createOneTimeEventObjectWithData(), group.getId());

    Event updatedEventData = EventService.createOneTimeEventObjectWithData();
    updatedEventData.setId(event.getId());
    eventEditService.updateEvent(updatedEventData, group.getId());

    Event eventFromDb = eventEditService.getEvent(event.getId()).get();
    assertEquals(eventFromDb.getId(), event.getId());
  }

  @Test
  public void testAdminCanCreateAndEditEvent_UndefinedStartTime() throws Exception{

    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    var eventEditService = groupAdminContext.createEventService();

    var eventToCreate = EventService.createOneTimeEventObjectWithData();
    eventToCreate.setStartTime(null);
    eventToCreate.setEndTime(null);
    eventToCreate.setDay("monday");

    Event event = eventEditService.createEvent(eventToCreate, group.getId());
    Optional<Event> originalEventFromDb = eventEditService.getEvent(event.getId());
    assertTrue(originalEventFromDb.isPresent());

    Event updatedEventData = EventService.createOneTimeEventObjectWithData();
    updatedEventData.setId(event.getId());
    eventEditService.updateEvent(updatedEventData, group.getId());

    Event eventFromDb = eventEditService.getEvent(event.getId()).get();
    assertEquals(eventFromDb.getId(), event.getId());
  }

  @Test
  public void testGroupAdmin_CannotEditEvent_ForOtherGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    adminContext.createEventService().createEvent(event2, group2.getId());

    Exception exception = assertThrows(
      Exception.class,
      () -> {
        EventService editService = groupAdminContext.createEventService();

        Event updated = EventService.createOneTimeEventObjectWithData();
        editService.updateEvent(updated, group2.getId());
      }
    );
    assertTrue(exception.getMessage().contains("does not have permission"),exception.getMessage());
  }

  @Test
  public void testStandardUser_CannotEditEvent() throws Exception {
    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);

    Event event = adminContext.createEventService().createEvent(event1, group.getId());
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          Event updated = EventService.createOneTimeEventObjectWithData();
          standardUserContext.createEventService().updateEvent(updated, event.getId());
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"),exception.getMessage());
  }

  @Test
  public void testDeleteRecurringEvent_oneTimeEventRemains() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    EventService eventService = groupAdminContext.createEventService();

    Event oneTimeEvent = EventService.createOneTimeEventObjectWithData();
    Event recurringEvent = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);

    eventService.createEvent(oneTimeEvent, group.getId());
    eventService.createEvent(recurringEvent, group.getId());
    eventService.deleteEvent(recurringEvent.getId(),group.getId());

    Optional<Event> oneTimeEventFromDb = eventService.getEvent(oneTimeEvent.getId());
    Optional<Event> recurringEventFromDb = eventService.getEvent(recurringEvent.getId());

    assertTrue(oneTimeEventFromDb.isPresent());
    assertFalse(recurringEventFromDb.isPresent());
  }

  @Test
  public void testDeleteOneTimeEvent_recurringEventRemains() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    EventService eventService = groupAdminContext.createEventService();

    Event oneTimeEvent = EventService.createOneTimeEventObjectWithData();
    Event recurringEvent = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);

    eventService.createEvent(oneTimeEvent, group.getId());
    eventService.createEvent(recurringEvent, group.getId());
    eventService.deleteEvent(oneTimeEvent.getId(),group.getId());

    Optional<Event> oneTimeEventFromDb = eventService.getEvent(oneTimeEvent.getId());
    Optional<Event> recurringEventFromDb = eventService.getEvent(recurringEvent.getId());

    assertTrue(recurringEventFromDb.isPresent());
    assertFalse(oneTimeEventFromDb.isPresent());
  }

  @Test
  public void testGroupAdmin_CanDeleteEvent_ForTheirGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    EventService oneTimeEventService = groupAdminContext.createEventService();

    Event event = oneTimeEventService.createEvent(event1, group.getId());
    oneTimeEventService.deleteEvent(event.getId(), group.getId());
    Optional<Event> groupEvent = oneTimeEventService.getEvent(event1.getId());
    assertTrue(groupEvent.isEmpty());
  }

  @Test
  public void testGroupAdmin_CannotDeleteEvent_ForOtherGroup() throws Exception{

    CreateGroupUtils.createGroup(groupAdminContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    EventService oneTimeEventService = adminContext.createEventService();

    Event event = oneTimeEventService.createEvent(event1, group2.getId());
    Exception exception = assertThrows(
        Exception.class,
        () -> {

          EventService editServiceB = groupAdminContext.createEventService();
          editServiceB.deleteEvent(event.getId(), group2.getId());
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testStandardUser_CannotDeleteEvent() throws Exception {
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Event event = adminContext.createEventService().createEvent(event1, group.getId());
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          standardUserContext.createEventService().deleteEvent(event.getId(), group.getId());
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
