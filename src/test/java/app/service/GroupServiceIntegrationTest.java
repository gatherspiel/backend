package app.service;

import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import org.junit.jupiter.api.BeforeAll;
import service.GameLocationsService;
import service.GroupService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.fail;

public class GroupServiceIntegrationTest {

  private static GroupService groupService;
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
      groupService = new GroupService();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  /**
   * TODO: Add tests
   *
   *  - Test invalid group name and invalid tag
   *  - Test invalid group name and valid tag
   *  - Test valid group name and invalid tag
   *  - Test cases where the group name and tag are both valid.
   */
}
