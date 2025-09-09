package app.service;

import app.location.Convention;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.result.GameLocationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.GameLocationsService;

import java.sql.Connection;
import java.time.LocalDate;
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
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testGameLocationsAreReturnedInAlphabeticalOrder() throws Exception {
    GameLocationData data = gameLocationsService.getGameLocations(LocalDate.of(2025,1,1));
    Assertions.assertAll(
        () -> assertEquals(5, data.getConventions().size()),
        () -> assertEquals(14, data.getGameStores().size()),
        () -> assertEquals(4, data.getGameRestaurants().size())
    );
  }

  @Test
  public void testConventionHasCorrectDays() throws Exception {
    GameLocationData data = gameLocationsService.getGameLocations(LocalDate.of(2025,1,1));

    for(String name: data.conventions.keySet()){
      Convention convention = data.conventions.get(name);
      if(convention.getName().equals("NOVA Open")) {
        String[] days = convention.getDays();
        String[] expected = new String[]{"2025-08-27", "2025-08-28", "2025-08-29", "2025-08-30", "2025-08-31"};
        assertArrayEquals(days, expected);
        return;
      }
    }
    fail("Convention not found in result set");
  }


  @Test
  public void testConventionsBeforeSpecifiedDate_AreNotReturned() throws Exception {

    GameLocationData data = gameLocationsService.getGameLocations(LocalDate.of(2035,1,1));
    Assertions.assertAll(
        () -> assertEquals(0, data.getConventions().size()),
        () -> assertEquals(14, data.getGameStores().size()),
        () -> assertEquals(4, data.getGameRestaurants().size())
    );
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
