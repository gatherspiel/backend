package app.service;

import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import app.result.GroupSearchResult;
import database.search.GroupSearchParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import service.SearchParameterException;
import service.SearchService;

import static org.junit.jupiter.api.Assertions.*;

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
  public void testAllGroupsAreReturned_NoSearchParams() throws Exception{
    GroupSearchResult searchResult = searchService.getGroups(new LinkedHashMap<>(), testConnectionProvider);
    assertEquals(38,searchResult.countGroups());
  }

  @Test
  public void testAllEventsAreReturned_NoSearchParams() throws Exception{
    GroupSearchResult searchResult = searchService.getGroups(new LinkedHashMap<String,String>(), testConnectionProvider);

    assertEquals(37,searchResult.countEvents());
  }

  @ParameterizedTest
  @CsvSource({
      "sunday, 4, 4",
      "monday, 6, 6",
      "tuesday, 3, 3",
      "wednesday, 6, 6",
      "thursday, 6, 6",
      "friday, 3, 3",
      "saturday, 9, 9"
  })
  public void testEventsAreReturned_WithDayAsSearchParam(String day, int expected) throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAY_OF_WEEK, day);

    GroupSearchResult searchResult = searchService.getGroups(params, testConnectionProvider);
    assertEquals(expected, searchResult.countEvents());
  }


  @ParameterizedTest
  @CsvSource({
      "Fairfax, 1, 2",
      "Falls Church, 0, 2"
  })
  public void testEventsAreReturned_WithLocationAsSearchParam(String location, int expectedEvents, int expectedGroups) throws Exception {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.LOCATION, location);

    GroupSearchResult searchResult = searchService.getGroups(params, testConnectionProvider);
    Assertions.assertAll(
        ()->    assertEquals(expectedEvents, searchResult.countEvents()),
       () ->     assertEquals(expectedGroups, searchResult.countGroups())
    );
  }

  @ParameterizedTest
  @CsvSource({
      "Alexandria, Sunday, 0,0",
      "Alexandria, Monday, 2,2",
      "Manassas, Sunday, 1, 1",
      "Manassas, Saturday, 0, 0"

  })

  public void testEventsAreReturned_WithLocationAndDayAsSearchParam(
      String location,
      String day,
      int expectedEvents,
      int expectedGroups) throws Exception
  {
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.LOCATION, location);
    params.put(GroupSearchParams.DAY_OF_WEEK, day);

    GroupSearchResult searchResult = searchService.getGroups(params, testConnectionProvider);
    Assertions.assertAll(
        ()->    assertEquals(expectedEvents, searchResult.countEvents()),
        () ->     assertEquals(expectedGroups, searchResult.countGroups())
    );
  }

  public void testInvalidDayReturnsValidationError() {
    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.DAY_OF_WEEK, "test");

    Exception exception = assertThrows(SearchParameterException.class, ()->{
      GroupSearchResult searchResult = searchService.getGroups(params, testConnectionProvider);

    });
    assertTrue(exception.getMessage().contains("Invalid day"));
  }

  @Test
  public void testInvalidLocationReturnsEmptyResult() throws Exception{

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.LOCATION, "test");

    GroupSearchResult searchResult = searchService.getGroups(params, testConnectionProvider);
    assertEquals(0,searchResult.countEvents());
  }

  @Test
  public void testSearchResultReturnsValidResultWithExtraParameter() throws Exception{

    LinkedHashMap<String,String> params = new LinkedHashMap<>();
    params.put("Test parameter", "test");
    params.put(GroupSearchParams.LOCATION,"College Park");

    GroupSearchResult searchResult = searchService.getGroups(params, testConnectionProvider);
    assertEquals(2,searchResult.countEvents());
    assertEquals(1,searchResult.countGroups());

  }


    /*
        TODO:


        Make sure search works with filters.
        Create API endpoint.

     */


}
