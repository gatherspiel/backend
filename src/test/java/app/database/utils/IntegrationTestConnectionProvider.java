package app.database.utils;

import database.utils.LocalConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;

public class IntegrationTestConnectionProvider extends LocalConnectionProvider {

  public Connection getDatabaseConnection() throws Exception {
    Class.forName("org.postgresql.Driver");
    String url =
      "jdbc:postgresql://localhost:5432/postgres?" +
      "user=postgres&" +
      "password=postgres";
    return DriverManager.getConnection(url);
  }
}
