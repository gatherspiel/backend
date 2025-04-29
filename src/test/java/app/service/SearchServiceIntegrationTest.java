package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.data.Group;
import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import app.result.GroupSearchResult;
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
import service.SearchService;

public class SearchServiceIntegrationTest {
  private static SearchService searchService;
  private static TestConnectionProvider testConnectionProvider;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new TestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);
      searchService = new SearchService();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testAllGroupsAreReturned_NoSearchParams() throws Exception {
    GroupSearchResult searchResult = searchService.getGroups(
      new LinkedHashMap<>(),
      testConnectionProvider
    );
    assertEquals(38, searchResult.countGroups());
  }

  @Test
  public void testAllEventsAreReturned_NoSearchParams() throws Exception {
    GroupSearchResult searchResult = searchService.getGroups(
      new LinkedHashMap<String, String>(),
      testConnectionProvider
    );

    assertEquals(37, searchResult.countEvents());
  }

  @ParameterizedTest
  @CsvSource(
    {
      "sunday, 4, 4",
      "monday, 6, 6",
      "tuesday, 3, 3",
      "wednesday, 6, 6",
      "thursday, 6, 6",
      "friday, 3, 3",
      "saturday, 9, 9"
    }
  )
  public void testEventsAreReturned_WithDayAsSearchParam(
    String day,
    int expected
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAY_OF_WEEK, day);

    GroupSearchResult searchResult = searchService.getGroups(
      params,
      testConnectionProvider
    );
    assertEquals(expected, searchResult.countEvents());
  }

  @ParameterizedTest
  @CsvSource({ "Fairfax, 1, 2", "Falls Church, 0, 2" })
  public void testEventsAreReturned_WithLocationAsSearchParam(
    String location,
    int expectedEvents,
    int expectedGroups
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);

    GroupSearchResult searchResult = searchService.getGroups(
      params,
      testConnectionProvider
    );
    Assertions.assertAll(
      () -> assertEquals(expectedEvents, searchResult.countEvents()),
      () -> assertEquals(expectedGroups, searchResult.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource(
    {
      "Alexandria, Sunday, 0,0",
      "Alexandria, Monday, 2,2",
      "Manassas, Sunday, 1, 1",
      "Manassas, Saturday, 0, 0"
    }
  )
  public void testEventsAreReturned_WithLocationAndDayAsSearchParam(
    String location,
    String day,
    int expectedEvents,
    int expectedGroups
  )
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, location);
    params.put(GroupSearchParams.DAY_OF_WEEK, day);

    GroupSearchResult searchResult = searchService.getGroups(
      params,
      testConnectionProvider
    );
    Assertions.assertAll(
      () -> assertEquals(expectedEvents, searchResult.countEvents()),
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
    GroupSearchResult searchResult = searchService.getGroups(
        params,
        testConnectionProvider
    );

    Map<Integer, Group> groupData = searchResult.getGroupData();

    for(Integer groupId: groupData.keySet()) {
      Group group = groupData.get(groupId);
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
        GroupSearchResult searchResult = searchService.getGroups(
          params,
          testConnectionProvider
        );
      }
    );
    assertTrue(exception.getMessage().contains("Invalid day"));
  }

  @Test
  public void testInvalidLocationReturnsEmptyResult() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.CITY, "test");

    GroupSearchResult searchResult = searchService.getGroups(
      params,
      testConnectionProvider
    );
    assertEquals(0, searchResult.countEvents());
  }

  @Test
  public void testSearchResultReturnsValidResultWithExtraParameter()
    throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put("Test parameter", "test");
    params.put(GroupSearchParams.CITY, "College Park");

    GroupSearchResult searchResult = searchService.getGroups(
      params,
      testConnectionProvider
    );
    assertEquals(2, searchResult.countEvents());
    assertEquals(1, searchResult.countGroups());
  }

  @Test
  public void testGroupsAreOrderedAlphabetically() throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    GroupSearchResult result = searchService.getGroups(params, testConnectionProvider);

    Group[] previous = new Group[1];

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
}
