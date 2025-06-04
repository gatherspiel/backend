package app.service;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.GameLocationsService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class LocationUpdateIntegrationTest {

  private static GameLocationsService gameLocationsService;
  private static IntegrationTestConnectionProvider testConnectionProvider;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
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
  public void testLocationInsertSameAddress_NoDuplicatesListCities() throws Exception {

    int locationsCountA = gameLocationsService.countLocations(testConnectionProvider);

    String address = "123 Main Street, Test, State 12345";
    gameLocationsService.insertAddress(testConnectionProvider,address);
    assertEquals(gameLocationsService.countLocations(testConnectionProvider), locationsCountA+1);

    gameLocationsService.insertAddress(testConnectionProvider,address);
    assertEquals(gameLocationsService.countLocations(testConnectionProvider), locationsCountA+1);
  }

  @Test
  public void testGetLocationInsert_SameCity_OneHasFullAddress_NoDuplicatesListCities() throws Exception {

    int locationsCountA = gameLocationsService.countLocations(testConnectionProvider);

    String address = "123 Main Street, Alexandria, Virginia 12345";
    gameLocationsService.insertAddress(testConnectionProvider,address);
    assertEquals(gameLocationsService.countLocations(testConnectionProvider), locationsCountA+1);

    int eventLocationsCountA = gameLocationsService.getAllEventLocations(testConnectionProvider, "dmv").size();
    gameLocationsService.addCity(testConnectionProvider,"Alexandria");
    assertEquals(gameLocationsService.countLocations(testConnectionProvider), locationsCountA+1);

    int eventLocationsCountB = gameLocationsService.getAllEventLocations(testConnectionProvider, "dmv").size();
    assertEquals(eventLocationsCountA, eventLocationsCountB);
  }

  @Test
  public void testGetLocationInsert_SameCity_TwoDifferentFullAddressesNoDuplicates_ListCities() throws Exception {

    int locationsCountA = gameLocationsService.countLocations(testConnectionProvider);

    gameLocationsService.insertAddress(testConnectionProvider,"123 Main Street, Arlington, Virginia 12345");

    assertEquals(gameLocationsService.countLocations(testConnectionProvider), locationsCountA+1);

    int eventLocationsCountA = gameLocationsService.getAllEventLocations(testConnectionProvider, "dmv").size();
    gameLocationsService.addCity(testConnectionProvider,"456 Main Street, Arlington, Virginia 12345");
    assertEquals(gameLocationsService.countLocations(testConnectionProvider), locationsCountA+2);

    int eventLocationsCountB = gameLocationsService.getAllEventLocations(testConnectionProvider, "dmv").size();
    assertEquals(eventLocationsCountA, eventLocationsCountB);
  }
}
