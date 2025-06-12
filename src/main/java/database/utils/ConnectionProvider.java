package database.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;
import utils.Params;

public class ConnectionProvider {
  private static Logger logger = LogUtils.getLogger();

  public Connection getDatabaseConnection() throws Exception {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    var dbPassword = Params.getDatabasePassword();

    if(!dbPassword.isPresent()){
      logger.info("Using local database connection provider");
      var connectionProvider = new LocalDevConnectionProvider();
      return connectionProvider.getDatabaseConnection();
    }
    else {
      logger.info("Loaded database Driver");
      String url =
          "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?" +
              "user=postgres.karqyskuudnvfxohwkok&" +
              "password=" +
              dbPassword.get();
      Connection connection = DriverManager.getConnection(url);
      return connection;
    }
  }

  public Connection getConnectionWithManualCommit() throws Exception {
    Connection connection = getDatabaseConnection();
    connection.setAutoCommit(false);
    return connection;
  }
}
