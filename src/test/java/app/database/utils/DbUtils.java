package app.database.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import database.utils.LocalConnectionProvider;
import service.auth.AuthService;
import service.auth.UserService;

public class DbUtils {

  public static final String TEST_USER_EMAIL = "test@freegather.org";

  public static void createTables(Connection conn) throws Exception {

    Statement stat = conn.createStatement();
    try {

      Scanner scanner = new Scanner(
        new File("src/test/fixtures/createTables.sql")
      );
      StringBuilder stringBuilder = new StringBuilder();
      while (scanner.hasNextLine()) {
        stringBuilder.append(scanner.nextLine() + " ");
      }
      String query = stringBuilder.toString();
      stat.execute(query);
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
      mapper.registerModule(new JavaTimeModule());

      System.out.println("Reading data");
      BulkUpdateRequest data = mapper.readValue(file, BulkUpdateRequest.class);
      System.out.println("Done reading data");


      BulkUpdateService bulkUpdateService = new BulkUpdateService();
      bulkUpdateService.deleteUsers(testConnectionProvider);
      bulkUpdateService.bulkUpdate(data, testConnectionProvider);

      UserService createUserService = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()), AuthService.getReadOnlyUser());
      createUserService.createAdmin(TEST_USER_EMAIL);
    } catch (Exception e) {
      System.out.println("Error initializing data:" + e.getMessage());
      throw e;
    }
  }
}
