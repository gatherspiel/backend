package app.service;

import app.data.Convention;
import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import app.result.GameLocationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameLocationsService;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LocationServiceIntegrationTest {

  private static GameLocationsService gameLocationsService;
  private static TestConnectionProvider testConnectionProvider;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new TestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
      gameLocationsService = new GameLocationsService();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testGameLocationsAreReturned() throws Exception {

    GameLocationData data = gameLocationsService.getGameLocations(testConnectionProvider, LocalDate.of(2025,1,1));
    Assertions.assertAll(
        () -> assertEquals(5, data.getConventions().size()),
        () -> assertEquals(14, data.getGameStores().size()),
        () -> assertEquals(4, data.getGameRestaurants().size())
    );
  }

  @Test
  public void testConventionHasCorrectDays() throws Exception {
    GameLocationData data = gameLocationsService.getGameLocations(testConnectionProvider, LocalDate.of(2025,1,1));

    for(Integer id: data.conventions.keySet()){
      Convention convention = data.conventions.get(id);
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

    GameLocationData data = gameLocationsService.getGameLocations(testConnectionProvider, LocalDate.of(2035,1,1));
    Assertions.assertAll(
        () -> assertEquals(0, data.getConventions().size()),
        () -> assertEquals(14, data.getGameStores().size()),
        () -> assertEquals(4, data.getGameRestaurants().size())
    );
  }

  @Test
  public void testListAllEventCities() throws Exception{
    ArrayList<String> eventCities = gameLocationsService.getAllEventLocations(testConnectionProvider);
    assertEquals(32, eventCities.size());
  }
}
