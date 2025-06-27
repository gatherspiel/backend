package app.service;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.GameLocationsService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class LocationUpdateIntegrationTest {

  private static GameLocationsService gameLocationsService;
  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;
  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      conn = testConnectionProvider.getDatabaseConnection();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
      gameLocationsService = new GameLocationsService(conn);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }


  @Test
  public void testLocationInsertSameAddress_NoDuplicatesListCities() throws Exception {

    int locationsCountA = gameLocationsService.countLocations();

    String address = "123 Main Street, Test, Maryland 12345";
    gameLocationsService.insertAddress(address);
    assertEquals(gameLocationsService.countLocations(), locationsCountA+1);

    gameLocationsService.insertAddress(address);
    assertEquals(gameLocationsService.countLocations(), locationsCountA+1);
  }

  @Test
  public void testGetLocationInsert_SameCity_OneHasFullAddress_NoDuplicatesListCities() throws Exception {

    int locationsCountA = gameLocationsService.countLocations();

    String address = "123 Main Street, Alexandria, Virginia 12345";
    gameLocationsService.insertAddress(address);
    assertEquals(gameLocationsService.countLocations(), locationsCountA+1);

    int eventLocationsCountA = gameLocationsService.getAllEventLocations("dmv").size();
    gameLocationsService.addCity("Alexandria");
    assertEquals(gameLocationsService.countLocations(), locationsCountA+1);

    int eventLocationsCountB = gameLocationsService.getAllEventLocations( "dmv").size();
    assertEquals(eventLocationsCountA, eventLocationsCountB);
  }

  @Test
  public void testGetLocationInsert_SameCity_TwoDifferentFullAddressesNoDuplicates_ListCities() throws Exception {

    int locationsCountA = gameLocationsService.countLocations();

    gameLocationsService.insertAddress("123 Main Street, Arlington, Virginia 12345");

    assertEquals(gameLocationsService.countLocations(), locationsCountA+1);

    int eventLocationsCountA = gameLocationsService.getAllEventLocations( "dmv").size();
    gameLocationsService.addCity("456 Main Street, Arlington, Virginia 12345");
    assertEquals(gameLocationsService.countLocations(), locationsCountA+2);

    int eventLocationsCountB = gameLocationsService.getAllEventLocations( "dmv").size();
    assertEquals(eventLocationsCountA, eventLocationsCountB);
  }
}
