package app.database.utils;

import database.utils.LocalConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;

public class IntegrationTestConnectionProvider extends LocalConnectionProvider {

  private Connection connection;
  public Connection getDatabaseConnection() throws Exception {

    if(connection != null){
      return connection;
    }

    Class.forName("org.postgresql.Driver");
    System.out.println("Retrieving connection to test database");
    String url =
      "jdbc:postgresql://localhost:5432/postgres?" +
      "user=postgres&" +
      "password=postgres";

    connection = DriverManager.getConnection(url);
    return connection;
  }
}
