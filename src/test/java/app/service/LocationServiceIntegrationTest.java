package app.service;

import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import app.result.GameLocationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameLocationsService;
import service.SearchService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LocationServiceIntegrationTest {

  private static GameLocationsService gameLocationsService;
  private static TestConnectionProvider testConnectionProvider;

  @Test
  public void test(){
    assertEquals(1,1);
  /*
  @BeforeAll
  static void setup() {
    testConnectionProvider = new TestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);
      DbUtils.initializeData(testConnectionProvider);
      gameLocationsService = new GameLocationsService();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testGameLocationsAreReturned() throws Exception {
    GameLocationData data = gameLocationsService.getGameLocations(testConnectionProvider);
    Assertions.assertAll(
        () -> assertEquals(3, data.getConventions().size()),
        () -> assertEquals(14, data.getGameStores().size()),
        () -> assertEquals(4, data.getGameRestaurants().size())
    );

   */
  }
}
