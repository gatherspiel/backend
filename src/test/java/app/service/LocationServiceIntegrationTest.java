package app.service;

import app.location.Convention;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.location.GameRestaurant;
import app.location.GameStore;
import app.result.listing.GameLocationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.GameLocationsService;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class LocationServiceIntegrationTest {

  private static GameLocationsService gameLocationsService;
  private static IntegrationTestConnectionProvider testConnectionProvider;

  private static Connection conn;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      conn = testConnectionProvider.getDatabaseConnection();

      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);
      gameLocationsService = new GameLocationsService(conn);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
    }
  }

  @Test
  public void testGameLocationsAreReturnedWithCorrectSize() throws Exception {
    LinkedHashMap<String,Convention> conventions = gameLocationsService.getConventions(LocalDate.of(2025,1,1));
    TreeMap<String, GameRestaurant> gameRestaurants = gameLocationsService.getGameRestaurants();
    TreeMap<String, GameStore> gameStores = gameLocationsService.getGameStores();

    Assertions.assertAll(
        () -> assertEquals(5, conventions.size()),
        () -> assertEquals(14, gameStores.size()),
        () -> assertEquals(4, gameRestaurants.size())
    );
  }

  @Test
  public void testConventionHasCorrectDays() throws Exception {
    var conventions = gameLocationsService.getConventions(LocalDate.of(2025,1,1));

    for(String name: conventions.keySet()){
      Convention convention = conventions.get(name);
      if(convention.getName().equals("NOVA Open")) {
        String[] days = convention.getDays();
        String[] expected = new String[]{"2026-08-27", "2026-08-28", "2026-08-29", "2026-08-30", "2026-08-31"};
        assertArrayEquals(days, expected);
        return;
      }
    }
    fail("Convention not found in result set");
  }


  @Test
  public void testConventionsBeforeSpecifiedDate_AreNotReturned() throws Exception {

    var conventions = gameLocationsService.getConventions(LocalDate.of(2035,1,1));
    Assertions.assertAll(
        () -> assertEquals(0, conventions.size())
   );
  }

  @Test
  public void testConventionsAreReturnedInChronologicalOrder() throws Exception {
    var conventions = gameLocationsService.getConventions(LocalDate.of(2015,1,1));


    LocalDate lastDate = null;
    for(String id: conventions.keySet()){
      Convention convention = conventions.get(id);

      String startDate = convention.getDays()[0];
      LocalDate date = LocalDate.parse(startDate);

      if(lastDate !=null){
        if(!date.isAfter(lastDate)){
          fail("Convention dates are not in chronological order");
        }
      }

      lastDate = date;
    }
  }

  @Test
  public void testListAllEventCities() throws Exception{
    TreeSet<String> eventCities = gameLocationsService.getAllEventLocations(null);
    assertEquals(33, eventCities.size());
  }

  @Test
  public void testList_DMV_Cities() throws Exception{
    TreeSet<String> eventCities = gameLocationsService.getAllEventLocations("DMV");
    assertEquals(32, eventCities.size());
  }

  @Test
  public void testList_dmv_Cities() throws Exception{
    TreeSet<String> eventCities = gameLocationsService.getAllEventLocations("dmv");
    assertEquals(32, eventCities.size());
  }

  @Test
  public void testList_Cities_invalidArea() throws Exception {
    TreeSet<String> eventCities = gameLocationsService.getAllEventLocations("Antarctica");
    assertEquals(0, eventCities.size());
  }
}
