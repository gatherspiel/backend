package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.HomepageGroup;
import app.result.HomeResult;
import database.search.GroupSearchParams;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import service.read.SearchService;

public class SearchServiceIntegrationTest {
  private static SearchService searchService;
  private static IntegrationTestConnectionProvider testConnectionProvider;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);
      searchService = new SearchService(conn);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testAllGroupsAreReturned_NoSearchParams() throws Exception {
    HomeResult result = searchService.getGroupsForHomepage(
      new LinkedHashMap<>()
    );
    assertEquals(39, result.countGroups());
  }

  @Test
  public void testAllEventsAreReturned_NoSearchParams() throws Exception {
    HomeResult result = searchService.getGroupsForHomepage(
      new LinkedHashMap<String, String>()
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
  public void testEventsAreReturned_WithDayAsSearchParam(
    String day,
    int expected
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAY_OF_WEEK, day);

    HomeResult result = searchService.getGroupsForHomepage(
      params
    );
    assertEquals(expected, result.countGroups());
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 2", "Falls Church, 2" })
  public void testEventsAreReturned_WithLocationAsSearchParam(
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
  public void testEventsAreReturned_WithLocationAndDayAsSearchParam(
    String location,
    String day,
    int expectedGroups
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DAY_OF_WEEK, day);

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
  public void testDuplicateAndNull_LocationResultsAreNotShown(String city) throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, city);
    HomeResult result = searchService.getGroupsForHomepage(
        params
    );

    Map<Integer, HomepageGroup> groupData = result.getGroupData();

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
  public void testInvalidDayReturnsValidationError() {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAY_OF_WEEK, "test");

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        HomeResult result = searchService.getGroupsForHomepage(
          params
        );
      }
    );
    assertTrue(exception.getMessage().contains("Invalid day"));
  }

  @Test
  public void testInvalidLocationReturnsEmptyResult() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, "test");

    HomeResult searchResult = searchService.getGroupsForHomepage(
      params
    );
    assertEquals(0, searchResult.countGroups());
  }

  @Test
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
  public void testGroupsAreOrderedAlphabetically() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    HomeResult result = searchService.getGroupsForHomepage(params);

    HomepageGroup[] previous = new HomepageGroup[1];

    result.getGroupData().forEach((id, current) -> {
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
  }

  @Test
  public void testSearchResultResponse_DMV_location_parameter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "DMV");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
  }

  @Test
  public void testSearchResultResponse_dmv_location_parameter() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(39, result.countGroups());
  }

  @Test
  public void testSearchResultResponse_unknownLocation_noResults() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "test");
    HomeResult result = searchService.getGroupsForHomepage(params);
    assertEquals(0, result.countGroups());
  }
}
