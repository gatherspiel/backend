package app.database.utils;

import app.request.BulkUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

import database.utils.LocalConnectionProvider;
import service.BulkUpdateService;
import service.user.UserService;

public class DbUtils {

  public static final String TEST_USER_EMAIL = "test@freegather.org";

  public static void createTables(Connection conn) throws Exception {

    System.out.println("Hi");
    Statement stat = conn.createStatement();
    try {
      System.out.println("Hi");

      Scanner scanner = new Scanner(
        new File("src/test/fixtures/createTables.sql")
      );
      StringBuilder stringBuilder = new StringBuilder();
      while (scanner.hasNextLine()) {
        stringBuilder.append(scanner.nextLine() + " ");
      }
      String query = stringBuilder.toString();
      System.out.println("Hi");

      stat.execute(query);
      System.out.println("Done");
      System.out.println("Created tables for integration tests");
    } catch (Exception e) {
      System.out.println("Error creating tables for integration tests:" + e.getMessage());
      throw e;
    }
  }

  public static void initializeData(
    LocalConnectionProvider testConnectionProvider
  )
    throws Exception {
    try {
      File file = new File("src/test/fixtures/listingData.json");
      ObjectMapper mapper = new ObjectMapper();
      BulkUpdateRequest data = mapper.readValue(file, BulkUpdateRequest.class);

      BulkUpdateService bulkUpdateService = new BulkUpdateService();
      bulkUpdateService.deleteUsers(testConnectionProvider);
      bulkUpdateService.bulkUpdate(data, testConnectionProvider);

      UserService createUserService = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()));
      createUserService.createAdmin(TEST_USER_EMAIL);
    } catch (Exception e) {
      System.out.println("Error initializing data:" + e.getMessage());
      throw e;
    }
  }
}
