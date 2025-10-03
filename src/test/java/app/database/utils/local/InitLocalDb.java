package app.database.utils.local;

import app.database.utils.DbUtils;
import database.utils.LocalDevConnectionProvider;
import service.auth.UserService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.fail;

public class InitLocalDb {
  public static void main(String[] args){

    var localConnectionProvider = new LocalDevConnectionProvider();
    try {
      Connection conn = localConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);
      DbUtils.initializeData(localConnectionProvider);

      UserService userService = new UserService(UserService.DataProvider.createDataProvider(conn));
      userService.createAdmin("test2@freegather.org");
      userService.activateUser("test2@freegather.org");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }
}
