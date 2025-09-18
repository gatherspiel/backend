package database.utils;

import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;

public class LocalDevConnectionProvider extends LocalConnectionProvider {

  public Connection getDatabaseConnection() throws Exception {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    String url =
        "jdbc:postgresql://127.0.0.1:54322/postgres?user=postgres&password=postgres";
    Connection conn = DriverManager.getConnection(url);
    return conn;
  }


}
