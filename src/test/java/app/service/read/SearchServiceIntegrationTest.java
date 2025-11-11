package app.service.read;

import static org.junit.jupiter.api.Assertions.*;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.Event;
import app.groups.EventLocation;
import app.groups.GameTypeTag;
import app.groups.Group;
import app.result.error.SearchParameterException;
import app.result.listing.EventSearchResult;
import app.result.listing.EventSearchResultItem;
import app.result.listing.HomepageGroup;
import app.result.listing.HomeResult;
import app.utils.CreateGroupUtils;
import app.utils.CreateUserUtils;
import database.search.SearchParams;
import java.sql.Connection;
import java.time.LocalDate;
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
    params.put(SearchParams.DAYS_OF_WEEK, day);

    HomeResult result = searchService.getGroupsForHomepage(
      params
    );
    assertEquals(expected, result.countGroups());
  }

  @Test
  @Order(1)
  public void testGroupsAreReturned_WithTwoDaysAsSearchParam() throws Exception{

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(SearchParams.DAYS_OF_WEEK, "Sunday,Monday");

    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    assertEquals(8, result.countGroups());
  }

  @Test
  @Order(1)
  public void testGroupsAreReturned_WithAllDaysAsSearchParam() throws Exception{

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(SearchParams.DAYS_OF_WEEK, "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");

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
    params.put(SearchParams.CITY, location);

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
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DAYS_OF_WEEK, day);

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
    params.put(SearchParams.CITY, city);
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
    params.put(SearchParams.DAYS_OF_WEEK, "test");

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
    params.put(SearchParams.CITY, "test");

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
    params.put(SearchParams.CITY, "College Park");

    HomeResult result = searchService.getGroupsForHomepage(
      params);
    assertEquals(2, result.countGroups());
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
    params.put(SearchParams.AREA, "DMV");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
  }

  @Test
  @Order(1)
  public void testSearchResultResponse_dmv_location_parameter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.AREA, "dmv");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
  }

  @Test
  @Order(1)
  public void testSearchResultResponse_unknownLocation_noResults() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.AREA, "test");
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
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DISTANCE, "0");
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
        params.put(SearchParams.DISTANCE, "0");
        searchService.getGroupsForHomepage(params);
      }
    );
    assertTrue(exception.getMessage().contains("City not specified for distance filter"));
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 3", "Falls Church, 6" })
  @Order(1)
  public void testSearchDistanceNearby_returnsCorrectNumberOfResults_Virginia(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DISTANCE, "2");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({ "Silver Spring, 8"})
  @Order(1)
  public void testSearchDistanceNearby_returnsCorrectNumberOfResults_Maryland(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DISTANCE, "4");
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );
    Assertions.assertAll(
        () -> assertEquals(expectedGroups, result.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({ "Greenbelt, 10"})
  @Order(1)
  public void testSearchDistanceMediumDistance_returnsCorrectNumberOfResults_Maryland(
      String location,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DISTANCE, "10");
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
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DISTANCE, "10");
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
    params.put(SearchParams.CITY, location);
    params.put(SearchParams.DISTANCE, "999");
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
    params.put(SearchParams.CITY, "Arlington");
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
    params.put(SearchParams.CITY, "Arlington");
    params.put(SearchParams.DISTANCE, "0");
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
    params.put(SearchParams.CITY, "DC");
    params.put(SearchParams.DISTANCE, "0");
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

  @Test
  @Order(4)
  public void testCorrectNumberOfEventsVisibleInSearchResults_noLocationOrDayFilter() throws Exception{

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(37, eventSearchResult.getEventData().size());
  }

  @Test
  @Order(4)
  public void testCorrectDataForEventsVisibleInSearchResults_noLocationOrDayFilter() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();

    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);

    HashSet<String> nameData = new HashSet<>();
    for(EventSearchResultItem eventItem: eventSearchResult.getEventData()){
      EventLocation location = eventItem.getEventLocation();
      System.out.println("Event name:"+eventItem.getEventName());
      assertFalse(location.getCity().isEmpty(),eventItem.getEventName());
      assertFalse(location.getState().isEmpty());
      assertFalse(location.getStreetAddress().isEmpty());
      assertNotEquals(0, location.getZipCode());

      assertTrue(eventItem.getNextEventDate().isAfter(LocalDate.MIN));
      assertNotNull(eventItem.getNextEventDate());

      assertFalse(eventItem.getGroupName().isEmpty());
      assertFalse(eventItem.getEventName().isEmpty());

      nameData.add(eventItem.getGroupName()+"_"+eventItem.getEventName());
    }

    assertEquals(eventSearchResult.getEventData().size(), nameData.size());

  }

  @Test
  @Order(4)
  public void testCorrectEventsVisibleInSearchResults_onlyDayFilter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.DAYS_OF_WEEK, "Sunday,Tuesday,Monday,Wednesday");
    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(19, eventSearchResult.getEventData().size());
  }

  @Test
  @Order(4)
  public void testCorrectDataForEventsVisibleInSearchResults_onlyLocationFilter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, "Arlington");
    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(3, eventSearchResult.getEventData().size());

  }

  @Test
  @Order(4)
  public void testCorrectDateForEventsVisibleInSearchResults_locationAndDayFilter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, "Arlington");
    params.put(SearchParams.DAYS_OF_WEEK,"Monday,Wednesday");
    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(2, eventSearchResult.getEventData().size());


  }

  @ParameterizedTest
  @CsvSource({ "Arlington", "College Park", "Silver Spring" })
  @Order(4)
  public void testEventsSortedByDistance_withLocationFilterAndNoDistance(
    String city
  ) throws Exception{

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, city);

    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);

    assertTrue(eventSearchResult.getEventData().size() > 1);
    verifyDistancesAreSorted(eventSearchResult.getEventData(), city);
  }

  @ParameterizedTest
  @CsvSource({ "Arlington,5", "Fairfax,2", "Falls Church,3" })
  @Order(4)
  public void testEventsSortedByDistance_withLocationFilterAndShortDistance(String city, int expectedResults) throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, city);
    params.put(SearchParams.DISTANCE, "5");

    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(expectedResults,eventSearchResult.getEventData().size());
    verifyDistancesAreSorted(eventSearchResult.getEventData(), city);
  }

  @ParameterizedTest
  @CsvSource({ "Arlington, 18", "Fairfax, 11", "Falls Church, 15" })
  @Order(4)
  public void testEventsSortedByDistance_withLocationFilterAndMediumDistance(String city,int expectedResults) throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, city);
    params.put(SearchParams.DISTANCE, "10");

    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(expectedResults,eventSearchResult.getEventData().size());
    verifyDistancesAreSorted(eventSearchResult.getEventData(), city);
  }

  @ParameterizedTest
  @CsvSource({ "Arlington, 31", "Fairfax, 29", "Falls Church, 31" })
  @Order(4)
  public void testEventsSortedByDistance_withLocationFilterAndLongDistance(String city, int expectedResults) throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, city);
    params.put(SearchParams.DISTANCE, "20");

    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(expectedResults,eventSearchResult.getEventData().size());
    verifyDistancesAreSorted(eventSearchResult.getEventData(), city);
  }

  @ParameterizedTest
  @CsvSource({ "Arlington, 28", "Fairfax,26", "Falls Church,28" })
  @Order(4)
  public void testEventsSortedByDistance_withLocationFilterAndDayFilter_AndLongDistance(String city, int expected) throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, city);
    params.put(SearchParams.DAYS_OF_WEEK, "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");
    params.put(SearchParams.DISTANCE, "20");

    EventSearchResult eventSearchResult = searchService.getEventsForHomepage(params);
    assertEquals(expected, eventSearchResult.getEventData().size());
    verifyDistancesAreSorted(eventSearchResult.getEventData(), city);
  }

  @Test
  public void testSearchEvents_invalidDistanceFilter_throwsBadRequestError(){
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.DISTANCE, "-4");

    Exception exception = assertThrows(
      SearchParameterException.class,
      () -> {
        searchService.getEventsForHomepage(
          params
        );
      }
    );
    assertTrue(exception.getMessage().contains("Distance cannot be negative"),exception.getMessage());
  }

  @Test
  public void testSearchEvents_invalidDistanceFilter2_throwsBadRequestError(){
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.DISTANCE, "Nearby");

    Exception exception = assertThrows(
      SearchParameterException.class,
      () -> {
        searchService.getEventsForHomepage(
          params
        );
      }
    );
    assertTrue(exception.getMessage().contains("Invalid value for distance"),exception.getMessage());
  }

  @Test
  public void testSearchEvents_invalidDayFilter_throwsBadRequestError(){
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.DAYS_OF_WEEK, "Today, Monday");

    Exception exception = assertThrows(
      SearchParameterException.class,
      () -> {
        searchService.getEventsForHomepage(
          params
        );
      }
    );
    assertTrue(exception.getMessage().contains("Invalid day"),exception.getMessage());

  }

  @Test
  public void testSearchEvents_invalidLocationFilter_noResults() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(SearchParams.CITY, "Montana");
    params.put(SearchParams.DISTANCE, "99999");

    EventSearchResult result = searchService.getEventsForHomepage(
      params);

    assertEquals(0, result.getEventData().size());

  }

  private void verifyDistancesAreSorted(List<EventSearchResultItem> data, String city){

    double distance = 0.0;

    for(EventSearchResultItem item: data){
      String eventCity = item.getEventLocation().getCity();
      Optional<Double> eventDistance = DistanceService.getDistance(city, eventCity);

      assertTrue(eventDistance.isPresent());
      assertTrue(eventDistance.get() >= distance);
      distance = eventDistance.get();
    }

    assertTrue(distance < 200);
  }
 }
