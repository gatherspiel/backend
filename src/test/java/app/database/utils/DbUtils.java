package app.database.utils;

import app.request.BulkUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

import database.utils.LocalConnectionProvider;
import service.BulkUpdateService;

public class DbUtils {

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
      System.out.println("Created tables for integration ftests");
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
      bulkUpdateService.bulkUpdate(data, testConnectionProvider);
    } catch (Exception e) {
      System.out.println("Error initializing data:" + e.getMessage());
      throw e;
    }
  }
}
