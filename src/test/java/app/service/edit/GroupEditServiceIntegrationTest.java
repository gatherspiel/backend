package app.service.edit;

import app.users.data.SessionContext;
import app.groups.data.*;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.result.group.GroupPageData;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.GroupSearchParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.ReadGroupService;
import service.update.EventService;
import service.update.GroupEditService;


import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GroupEditServiceIntegrationTest {

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String USERNAME_2 = "user";
  private static final String USERNAME_3 = "user2@test";
  private static final String USERNAME_4 = "user3@test";

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static SessionContext adminContext;
  private static SessionContext standardUserContext;
  private static SessionContext standardUserContext2;
  private static SessionContext standardUserContext3;
  private static SessionContext readOnlyUserContext;

  private static Event event1;
  private static Event event2;

  private static final String EVENT_NAME_1="Catan Event";
  private static final String EVENT_NAME_2="Power Grid";

  private static final String LOCATION_1 = "1234 Crystal Drive, Arlington,VA 22222";
  private static final String LOCATION_2 = "1234 Main Street, Arlington,VA 22222";

  private static final String SUMMARY_1="Resource management and trading Euro";
  private static final String SUMMARY_2="Come play Power Grid";

  private static final String URL_1="http://localhost:8000";
  private static final String URL_2="http://localhost:8001";

  private static LocalDateTime START_TIME_1 = LocalDateTime.now().plusHours(1);
  private static LocalDateTime END_TIME_1 = LocalDateTime.now().plusHours(5);

  private static LocalDateTime START_TIME_2 = LocalDateTime.now().plusHours(1).plusDays(1);
  private static LocalDateTime END_TIME_2 = LocalDateTime.now().plusHours(5).plusDays(1);

  private static void assertGroupsAreEqual(Group group1, Group group2){
    assertEquals(group1.getId(), group2.getId());
    assertEquals(group1.getUrl(), group2.getUrl());
    assertEquals(group1.getName(), group2.getName());
    assertEquals(group1.getDescription(), group2.getDescription());
  }

  @BeforeAll
  static void setup() throws Exception{
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {
      conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);

      adminContext = CreateUserUtils.createContextWithNewAdminUser( ADMIN_USERNAME,testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser( USERNAME_2,testConnectionProvider);
      standardUserContext2 = CreateUserUtils.createContextWithNewStandardUser( USERNAME_3,testConnectionProvider);
      standardUserContext3 = CreateUserUtils.createContextWithNewStandardUser( USERNAME_4,testConnectionProvider);
      readOnlyUserContext = SessionContext.createContextWithoutUser(testConnectionProvider);

      event1 = EventService.createEventObject(EVENT_NAME_1, LOCATION_1, SUMMARY_1, URL_1, START_TIME_1, END_TIME_1);
      event2 = EventService.createEventObject(EVENT_NAME_2, LOCATION_2, SUMMARY_2, URL_2, START_TIME_2, END_TIME_2);

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testUserCannotCreateGroup_whenNotLoggedIn() throws Exception {

    Exception exception = assertThrows(
      Exception.class,
      ()->{
        CreateGroupUtils.createGroup(readOnlyUserContext.getUser(), conn);
      }
    );
    assertTrue(exception.getMessage().contains("Cannot insert"));
  }

  @Test
  public void testCannotCreateTwoGroupsWithSameName()  throws Exception {
    Group group = new Group();
    Group group2 = new Group();

    group.setName("Test");
    group.setUrl("localhost:8080");

    group2.setName("Test");
    group2.setUrl("localhost:8080");

    GroupEditService groupEditService = adminContext.createGroupEditService();

    Exception exception = assertThrows(
      Exception.class,
      ()->{
        groupEditService.insertGroup(group);
        groupEditService.insertGroup(group2);
      }
    );
    assertTrue(exception.getMessage().contains("Cannot create multiple groups with the same name"));
  }

  @Test
  public void testUserCannotEditGroup_whenNotLoggedIn() throws Exception {
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          readOnlyUserContext.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testUserCannotEditGroup_whenTheyAreStandardUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testSiteAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    adminContext.createGroupEditService().editGroup(updated);

    Optional<Group> updatedFromDb = adminContext.createReadGroupService().getGroupWithOneTimeEvents(group.getId());

    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }


  @Test
  public void testGroupAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    standardUserContext.createGroupEditService().editGroup(updated);

    Optional<Group> updatedFromDb = standardUserContext.createReadGroupService().getGroupWithOneTimeEvents(group.getId());
    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }


  @Test
  public void testUserCannotEditGroupThatDoesNotExist() throws Exception{
    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId((int)(Math.random()*999999));
    Exception exception = assertThrows(
        Exception.class,
        ()->{
          adminContext.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("Invalid group parameters for update"),exception.getMessage());

  }

  @Test
  public void testGroupAdminCannotEditGroup_whenTheyAreNotAdminOfThatGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    CreateGroupUtils.createGroup(standardUserContext2.getUser(),conn);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());


    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext2.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModeratorCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    standardUserContext.createGroupPermissionService().addGroupModerator(standardUserContext2.getUser(), group.getId());

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    standardUserContext2.createGroupEditService().editGroup(updated);

    Optional<Group> updatedFromDb  = standardUserContext2.createReadGroupService().getGroupWithOneTimeEvents(group.getId());
    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }

  @Test
  public void testGroupModeratorCannotEditGroup_whenTheyAreNotAdminOfThatGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(standardUserContext2.getUser(), conn);

    standardUserContext.createGroupPermissionService().addGroupModerator(standardUserContext3.getUser(), group.getId());

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group2.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext3.createGroupEditService().editGroup(updated);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupAdminCanDeleteGroup() throws Exception {

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    standardUserContext.createGroupEditService().deleteGroup(group.getId());

    Optional<Group> groupInDb = standardUserContext.createReadGroupService().getGroupWithOneTimeEvents(group.getId());
    assertTrue(groupInDb.isEmpty());
  }

  @Test
  public void testStandardUserCannotDeleteGroup() throws Exception {

    Group group = CreateGroupUtils.createGroup(standardUserContext.getUser(), conn);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          standardUserContext2.createGroupEditService().deleteGroup(group.getId());
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testDeleteGroupWithLocationsAndRecurringEvents() throws Exception {
    adminContext.createGroupEditService().deleteGroup(22);

    Optional<Group> groupFromDb = adminContext.createReadGroupService().getGroupWithOneTimeEvents(22);
    assertFalse(groupFromDb.isPresent());
  }

  @Test
  public void testDeleteGroupWithEvents() throws Exception {
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    EventService eventService = adminContext.createEventService();


    Event recurringEvent = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);
    eventService.createEvent(recurringEvent, group.getId());
    eventService.createEvent(event1, group.getId());

    adminContext.createGroupEditService().deleteGroup(group.getId());

    Optional<Group> groupFromDb = adminContext.createReadGroupService().getGroupWithOneTimeEvents(group.getId());
    assertFalse(groupFromDb.isPresent());

    Optional<Event> eventFromDb = eventService.getEvent(event1.getId());
    Optional<Event> recurringEventFromDb = eventService.getEvent(recurringEvent.getId());

    assertFalse(recurringEventFromDb.isPresent());
    assertFalse(eventFromDb.isPresent());

  }

  @Test
  public void testDeleteGroupWithEvents_otherGroupEventsRemain() throws Exception {
    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    Group group2 = CreateGroupUtils.createGroup(adminContext.getUser(), conn);

    EventService oneTimeEventService = adminContext.createEventService();
    oneTimeEventService.createEvent(event1, group.getId());
    oneTimeEventService.createEvent(event2, group2.getId());

    adminContext.createGroupEditService().deleteGroup(group.getId());

    Optional<Group> groupFromDb = adminContext.createReadGroupService().getGroupWithOneTimeEvents(group.getId());
    assertFalse(groupFromDb.isPresent());

    Optional<Event> event = oneTimeEventService.getEvent(event1.getId());
    assertFalse(event.isPresent());


    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group2.getName());

    GroupPageData groupData = adminContext.createReadGroupService().getGroupPageData(
        params
    );

    Optional<Event> eventFromDbOptional = groupData.getOneTimeEventData().stream().findFirst();
    assertTrue(eventFromDbOptional.isPresent());

    Event eventFromDb = eventFromDbOptional.get();

    assertEquals(eventFromDb.getName(),event2.getName());
    assertEquals(eventFromDb.getDescription(),event2.getDescription());
    assertEquals(eventFromDb.getStartTime(),event2.getStartTime());
  }

  @Test
  public void testAddGroup_AddTags_AndDeleteTags() throws Exception{

    GroupEditService groupEditService = adminContext.createGroupEditService();
    ReadGroupService readGroupService = adminContext.createReadGroupService();

    Group group = CreateGroupUtils.createGroup(adminContext.getUser(), conn);
    assertArrayEquals(new GameTypeTag[0],group.getGameTypeTags());

    GameTypeTag[] tags = new GameTypeTag[]{GameTypeTag.SOCIAL_GAMES, GameTypeTag.HIDDEN_IDENTITY_GAMES};
    group.setGameTypeTags(tags);
    groupEditService.editGroup(group);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.NAME, group.getName());
    GroupPageData groupData  = readGroupService.getGroupPageData(params);

    assertEquals(group.getId(), groupData.getId());
    assertArrayEquals(new GameTypeTag[]{GameTypeTag.SOCIAL_GAMES, GameTypeTag.HIDDEN_IDENTITY_GAMES},groupData.getGameTypeTags());


    group.setGameTypeTags(new GameTypeTag[0]);
    groupEditService.editGroup(group);

    GroupPageData groupData2  = readGroupService.getGroupPageData(params);
    assertEquals(group.getId(), groupData2.getId());
    assertArrayEquals(new GameTypeTag[0],groupData2.getGameTypeTags());
  }
}
