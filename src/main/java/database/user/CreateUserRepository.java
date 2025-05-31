package database.user;

import app.data.auth.User;
import app.data.auth.UserType;
import database.utils.ConnectionProvider;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class CreateUserRepository {

  Logger logger;
  public CreateUserRepository(){
    logger = LogUtils.getLogger();
  }

  public User createAdmin(String email, Connection conn) throws Exception{
      String query =
          "INSERT INTO users(email, user_role_level) VALUES(?,cast(? as user_role_level)) returning id";

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setString(1, email);
    insert.setString(2, UserType.SITE_ADMIN.toString());

    ResultSet rs = insert.executeQuery();
    if(!rs.next()) {
      var message = "Failed to create admin user";
      logger.error(message);
      throw new Exception(message);
    }

    return new User(email, UserType.SITE_ADMIN, rs.getInt(1));
  }


  public User createStandardUser(String email, Connection conn) throws Exception{
    String query =
        "INSERT INTO users(email, user_role_level) VALUES(?,cast(? as user_role_level)) returning id";

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setString(1, email);
    insert.setString(2, "user");

    ResultSet rs = insert.executeQuery();
    if(!rs.next()) {
      var message = "Failed to create admin user";
      logger.error(message);
      throw new Exception(message);
    }

    return new User(email, UserType.USER, rs.getInt(1));
  }
}
