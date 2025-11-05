package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.Event;
import app.groups.data.EventLocation;
import app.groups.data.GameTypeTag;
import app.groups.data.Group;
import app.result.listing.HomepageGroup;
import app.result.listing.HomeResult;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.GroupSearchParams;
import java.sql.Connection;
import java.time.LocalTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.opentest4j.AssertionFailedError;
import service.read.DistanceService;
import service.read.SearchService;
import service.update.EventService;
import service.update.GroupEditService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchServiceIntegrationTest {
  private static SearchService searchService;
  private static IntegrationTestConnectionProvider testConnectionProvider;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    DistanceService.loadData();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);
      searchService = new SearchService(conn);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
    }
  }

  @Test
  @Order(1)
  public void testAllGroupsAreReturned_NoSearchParams() throws Exception {
    HomeResult result = searchService.getGroupsForHomepage(
      new LinkedHashMap<>()
    );
    assertEquals(39, result.countGroups());
  }

  @ParameterizedTest
  @CsvSource(
    {
      "sunday, 4",
      "monday, 6",
      "tuesday, 3",
      "wednesday, 5",
      "thursday, 5",
      "friday, 3",
      "saturday, 9"
    }
  )
  @Order(1)
  public void testGroupsAreReturned_WithOneDayAsSearchParam(
    String day,
    int expected
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAYS_OF_WEEK, day);

    HomeResult result = searchService.getGroupsForHomepage(
      params
    );
    assertEquals(expected, result.countGroups());
  }

  @Test
  @Order(1)
  public void testGroupsAreReturned_WithTwoDaysAsSearchParam() throws Exception{

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAYS_OF_WEEK, "Sunday,Monday");

    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    assertEquals(8, result.countGroups());
  }

  @Test
  @Order(1)
  public void testGroupsAreReturned_WithAllDaysAsSearchParam() throws Exception{

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAYS_OF_WEEK, "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");

    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    assertEquals(20, result.countGroups());
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 2", "Falls Church, 2" })
  @Order(1)
  public void testGroupsAreReturned_WithLocationAsSearchParam(
    String location,
    int expectedGroups
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);

    HomeResult result = searchService.getGroupsForHomepage(
      params
    );
    Assertions.assertAll(
      () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource(
    {
      "Alexandria, Sunday,0",
      "Alexandria, Monday,2",
      "Manassas, Sunday, 1",
      "Manassas, Saturday, 0"
    }
  )
  @Order(1)
  public void testGroupsAreReturned_WithLocationAndDayAsSearchParam(
    String location,
    String day,
    int expectedGroups
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DAYS_OF_WEEK, day);

    HomeResult searchResult = searchService.getGroupsForHomepage(
      params
    );
    Assertions.assertAll(
      () -> assertEquals(expectedGroups, searchResult.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource(
      {
          "Arlington",
          "Alexandria"
      }
  )
  @Order(1)
  public void testDuplicateAndNull_LocationResultsAreNotShown(String city) throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, city);
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );

    Map<Integer, HomepageGroup> groupData = result.getGroupDataMap();

    for(Integer groupId: groupData.keySet()) {
      HomepageGroup group = groupData.get(groupId);
      Set<String> cities = new HashSet<String>();
      for(String groupCity: group.getCities()){
        if(groupCity != null) {
          cities.add(groupCity);
        }
      }
      assertEquals(group.getCities().length,cities.size());
    }
  }

  @Test
  @Order(1)
  public void testInvalidDayReturnsValidationError() {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAYS_OF_WEEK, "test");

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        searchService.getGroupsForHomepage(
          params
        );
      }
    );
    assertTrue(exception.getMessage().contains("Invalid day"));
  }

  @Test
  @Order(1)
  public void testInvalidLocationReturnsEmptyResult() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, "test");

    HomeResult searchResult = searchService.getGroupsForHomepage(
      params
    );
    assertEquals(0, searchResult.countGroups());
  }

  @Test
  @Order(1)
  public void testSearchResultReturnsValidResultWithExtraParameter()
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put("Test parameter", "test");
    params.put(GroupSearchParams.CITY, "College Park");

    HomeResult result = searchService.getGroupsForHomepage(
      params);
    assertEquals(1, result.countGroups());
  }

  @Test
  @Order(1)
  public void testSearchAllGroups_correctRecurringEventsFlag() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
    int recurringEventGroups = 0;
    int nonRecurringEventGroups = 0;

    for(HomepageGroup group: result.getGroupData()){
      if(group.getRecurringEventDays().isEmpty()){
        recurringEventGroups++;
      } else {
        nonRecurringEventGroups++;
      }
    }
    assertEquals(19,recurringEventGroups);
    assertEquals(20,nonRecurringEventGroups);
  }

  @Test
  @Order(1)
  public void testGroupsAreOrderedAlphabeticallyWithUpdate() throws Exception {

    var adminContext = CreateUserUtils.createContextWithNewAdminUser("adminTestSearchAllGroups_correctRecurringEventsFlag", testConnectionProvider);
    GroupEditService groupEdit = adminContext.createGroupEditService();

    final Group GROUP_1 = CreateGroupUtils.createGroupObject();
    final Group GROUP_2 = CreateGroupUtils.createGroupObject();
    final Group GROUP_3 = CreateGroupUtils.createGroupObject();

    GROUP_1.setName("Castles of Burgundy Group");
    GROUP_2.setName("Agricola Group");
    GROUP_3.setName("Zombicide Group");

    groupEdit.insertGroup(GROUP_1);
    groupEdit.insertGroup(GROUP_2);
    groupEdit.insertGroup(GROUP_3);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(42, result.countGroups());

    HomepageGroup[] previous = new HomepageGroup[1];

    result.getGroupDataMap().forEach((id, current) -> {
      if (previous[0] != null) {
        String prevName = previous[0].getName();
        String currName = current.getName();

        if (prevName != null && currName != null) {
          assertTrue(
            prevName.compareTo(currName) <= 0,
            "Groups out of order: " + prevName + " after " + currName
          );
        }
      }
      previous[0] = current;
    });
    groupEdit.deleteGroup(GROUP_1.getId());
    groupEdit.deleteGroup(GROUP_2.getId());
    groupEdit.deleteGroup(GROUP_3.getId());
  }

  @Test
  @Order(1)
  public void testGroupWithMultipleEvents_LocationsAreInAlphabeticalOrder() throws Exception {

    var adminContext = CreateUserUtils.createContextWithNewAdminUser("admin_user", testConnectionProvider);

    EventService eventService = adminContext.createEventService();
    GroupEditService groupEditService = adminContext.createGroupEditService();

    Group group = CreateGroupUtils.createGroupObject();
    group.setName("Group1");
    Group createdGroup = groupEditService.insertGroup(group);

    Event event1 = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);
    Event event2 = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);
    Event event3 = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);
    Event event4 = EventService.createRecurringEventObjectWithData(LocalTime.NOON, LocalTime.MAX);

    EventLocation location = new EventLocation();
    location.setZipCode(22222);
    location.setStreetAddress("Street Address");
    location.setState("VA");
    location.setCity("A");

    event1.setEventLocation(location);
    eventService.createEvent(event1, createdGroup.getId());

    location.setCity("C");
    event2.setEventLocation(location);
    eventService.createEvent(event2, createdGroup.getId());

    location.setCity("B");
    event3.setEventLocation(location);
    eventService.createEvent(event3, createdGroup.getId());

    location.setCity("D");
    event4.setEventLocation(location);
    eventService.createEvent(event4, createdGroup.getId());

    LinkedHashMap<String, String> params = new LinkedHashMap<>();

    HomeResult result = searchService.getGroupsForHomepage(params);

    HomepageGroup addedGroup = null;
    for(HomepageGroup homepageGroup: result.getGroupData()){
      if(homepageGroup.getId().equals(createdGroup.getId())) {
        addedGroup = homepageGroup;
        break;
      }
    }
    assertNotNull(addedGroup);

    String[] sortedCities = {"A","B","C","D"};
    assertArrayEquals(sortedCities, addedGroup.cities);

    groupEditService.deleteGroup(createdGroup.getId());
  }

  @Test
  @Order(1)
  public void testSearchResultResponse_DMV_location_parameter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "DMV");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
  }

  @Test
  @Order(1)
  public void testSearchResultResponse_dmv_location_parameter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
  }

  @Test
  @Order(1)
  public void testSearchResultResponse_unknownLocation_noResults() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "test");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(0, result.countGroups());
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 2", "Ashburn, 3" })
  @Order(1)
  public void testSearchDistanceZero_returnsAllResultsInCity(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DISTANCE, "0");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @Test
  @Order(1)
  public void testSearchDistanceWithoutLocation_throwsError() throws Exception{

    Exception exception = assertThrows(
      Exception.class,
      ()->{
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(GroupSearchParams.DISTANCE, "0");
        searchService.getGroupsForHomepage(params);
      }
    );
    assertTrue(exception.getMessage().contains("City not specified for distance filter"));
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 3", "Falls Church, 7" })
  @Order(1)
  public void testSearchDistanceNearby_returnsCorrectNumberOfResults_Virginia(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DISTANCE, "2");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({ "Silver Spring, 7"})
  @Order(1)
  public void testSearchDistanceNearby_returnsCorrectNumberOfResults_Maryland(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DISTANCE, "4");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({ "Greenbelt, 9"})
  @Order(1)
  public void testSearchDistanceMediumDistance_returnsCorrectNumberOfResults_Maryland(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DISTANCE, "10");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 19", "Falls Church, 24" })
  @Order(1)
  public void testSearchDistanceMediumDistance_returnsCorrectNumberOfResults(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DISTANCE, "10");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 38", "Falls Church, 38" })
  @Order(1)
  public void testSearchDistanceLongDistance_returnsCorrectNumberOfResults(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DISTANCE, "999");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @Test
  @Order(1)
  public void testSearchWithOnlyCityAsParameter_returnsResultForGroupInMultipleCities() throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, "Arlington");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );

    boolean hasGroup = false;
    for(HomepageGroup group: result.getGroupDataMap().values()){
      if(group.getName().equals("Beer & Board Games")){
        hasGroup = true;
        break;
      }
    }
    assertTrue(hasGroup);
  }

  @Test
  @Order(1)
  public void testSearchDistanceZeroInArlington_returnsResultForGroupInMultipleCities() throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, "Arlington");
    params.put(GroupSearchParams.DISTANCE, "0");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    boolean hasGroup = false;
    for(HomepageGroup group: result.getGroupDataMap().values()){
      if(group.getName().equals("Beer & Board Games")){
        hasGroup = true;
        break;
      }
    }
    assertTrue(hasGroup);
  }

  @Test
  @Order(1)
  public void testSearchDistanceZeroInDC_returnsResultForGroupInMultipleCities() throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, "DC");
    params.put(GroupSearchParams.DISTANCE, "0");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );

    boolean hasGroup = false;
    for(HomepageGroup group: result.getGroupDataMap().values()){
      if(group.getName().equals("Beer & Board Games")){
        hasGroup = true;
        break;
      }
    }
    assertTrue(hasGroup);
  }

  @Test
  @Order(2)
  public void testAdminCreatesGroup_VisibleInSearchResults() throws Exception{
    var adminContext = CreateUserUtils.createContextWithNewAdminUser("admin_user_2", testConnectionProvider);
    var groupService = adminContext.createGroupEditService();

    final String GROUP_NAME = "Group" + UUID.randomUUID();
    Group group = CreateGroupUtils.createGroupObject();

    group.setName(GROUP_NAME);

    Group created = groupService.insertGroup(group);

    HomeResult result = searchService.getGroupsForHomepage(new LinkedHashMap<String,String>());
    HomepageGroup foundGroup = null;
    for(HomepageGroup homepageGroup: result.getGroupDataMap().values()) {
      if(homepageGroup.getName().equals(GROUP_NAME)) {
        foundGroup = homepageGroup;
        break;
      }
    }
    groupService.deleteGroup(created.getId());
    assertNotNull(foundGroup);
    assertEquals(created.getId(), foundGroup.getId());
  }

  @Test
  @Order(2)
  public void testStandardUserCreatesGroup_NotVisibleInSearchResults_untilItIsSetToVisible() throws Exception{

    var standardUserContext = CreateUserUtils.createContextWithNewStandardUser("standard_user", testConnectionProvider);
    var standardUserGroupService = standardUserContext.createGroupEditService();
    var adminUserContext = CreateUserUtils.createContextWithNewAdminUser("admin_user_3", testConnectionProvider);
    var adminUserGroupService = adminUserContext.createGroupEditService();

    final String GROUP_NAME = "Group" + UUID.randomUUID();
    Group group = CreateGroupUtils.createGroupObject();
    group.setName(GROUP_NAME);

    Group created = standardUserGroupService.insertGroup(group);

    try {

      HomeResult result = searchService.getGroupsForHomepage(new LinkedHashMap<String,String>());
      HomepageGroup foundGroup = null;
      for(HomepageGroup homepageGroup: result.getGroupDataMap().values()) {
        if(homepageGroup.getName().equals(GROUP_NAME)) {
          foundGroup = homepageGroup;
          break;
        }
      }

      assertNull(foundGroup);

      adminUserGroupService.setGroupToVisible(created.getId());
      HomeResult resultAfterUpdate = searchService.getGroupsForHomepage(new LinkedHashMap<String,String>());
      HomepageGroup foundGroupAfterUpdate = null;
      for(HomepageGroup homepageGroup: resultAfterUpdate.getGroupDataMap().values()) {
        if(homepageGroup.getName().equals(GROUP_NAME)) {
          foundGroupAfterUpdate = homepageGroup;
          break;
        }
      }

      assertNotNull(foundGroupAfterUpdate);
      assertEquals(created.getId(), foundGroupAfterUpdate.getId());
      standardUserGroupService.deleteGroup(created.getId());
    } catch(AssertionFailedError e) {
      standardUserGroupService.deleteGroup(created.getId());
      fail(e.getCause());
    }
  }

  @Test
  @Order(3)
  public void testCreateGroupWithTags_tagsAreVisibleInSearchResults() throws Exception{
    var adminUserContext = CreateUserUtils.createContextWithNewAdminUser("admin_user_4",testConnectionProvider);
    var adminUserGroupService = adminUserContext.createGroupEditService();

    final String GROUP_NAME = "Group" + UUID.randomUUID();
    Group group = CreateGroupUtils.createGroupObject();
    group.setName(GROUP_NAME);

    GameTypeTag[] gameTypeTags = new GameTypeTag[]{GameTypeTag.EUROGAMES, GameTypeTag.SOCIAL_GAMES};
    group.setGameTypeTags(gameTypeTags);

    Group inserted = adminUserGroupService.insertGroup(group);

    HomeResult result = searchService.getGroupsForHomepage(new LinkedHashMap<String,String>());
    HomepageGroup foundGroup = null;
    for(HomepageGroup homepageGroup: result.getGroupDataMap().values()) {
      if(homepageGroup.getName().equals(GROUP_NAME)) {
        foundGroup = homepageGroup;
        break;
      }
    }
    assertNotNull(foundGroup);
    assertEquals(inserted.getId(),foundGroup.getId());

    GameTypeTag[] expected = new GameTypeTag[]{GameTypeTag.EUROGAMES, GameTypeTag.SOCIAL_GAMES};
    assertArrayEquals(expected, inserted.getGameTypeTags());
  }
 }
