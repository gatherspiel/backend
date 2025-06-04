package database.user;

import app.data.auth.User;
import app.data.auth.UserType;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UserRepository {

  Logger logger;
  public UserRepository(){
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

    logger.info("Created admin with email:"+email);
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

    int userId = rs.getInt(1);
    logger.info("Created user with id:"+userId);
    return new User(email, UserType.USER, userId);
  }

  public User getUserFromEmail(String email, Connection conn) throws Exception {

    String query = "SELECT * from users where email = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, email);

    ResultSet rs = select.executeQuery();

    if(!rs.next()){
      logger.info("Did not find user with email:"+email);
      return null;
    }

    logger.info(rs.getString("email"));
    User user = new User(
        email,
        UserType.fromDatabaseString(rs.getString("user_role_level")),
        rs.getInt("id")
    );
    return user;
  }

  public void deleteAllUsers(Connection connection) throws Exception {
    logger.info("Deleting all users");

    String query = "TRUNCATE table users CASCADE";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.executeUpdate();
  }
}
