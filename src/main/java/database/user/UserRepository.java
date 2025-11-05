package database.user;

import app.result.error.UnauthorizedError;
import app.users.data.User;
import app.users.data.UserType;
import app.users.data.UserData;
import database.BaseRepository;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class UserRepository extends BaseRepository {

  Logger logger;
  public UserRepository(Connection conn){
    super(conn);
    logger = LogUtils.getLogger();
  }

  public User createAdmin(String email) throws Exception{
      String query =
          "INSERT INTO users(email, user_role_level) VALUES(?,cast(? as user_role_level)) returning id";

    PreparedStatement insert = connection.prepareStatement(query);
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

  public User createStandardUser(String email) throws Exception{
    String query =
        "INSERT INTO users(email, user_role_level, is_active) VALUES(?,cast(? as user_role_level), true) returning id";

    PreparedStatement insert = connection.prepareStatement(query);
    insert.setString(1, email);
    insert.setString(2, "user");

    ResultSet rs = insert.executeQuery();
    if(!rs.next()) {
      var message = "Failed to create standard user";
      logger.error(message);
      throw new Exception(message);
    }

    int userId = rs.getInt(1);
    logger.info("Created user with id:"+userId);
    return new User(email, UserType.USER, userId);
  }


  public User createTester(String email) throws Exception{
    String query =
        "INSERT INTO users(email, user_role_level) VALUES(?,cast(? as user_role_level)) returning id";

    PreparedStatement insert = connection.prepareStatement(query);
    insert.setString(1, email);
    insert.setString(2, "tester");

    ResultSet rs = insert.executeQuery();
    if(!rs.next()) {
      var message = "Failed to create admin user";
      logger.error(message);
      throw new Exception(message);
    }

    int userId = rs.getInt(1);
    logger.info("Created user with id:"+userId);
    return new User(email, UserType.TESTER, userId);
  }

  public User getUserFromEmail(String email) throws Exception {

    String query = "SELECT * from users where email = ?";
    PreparedStatement select = connection.prepareStatement(query);
    select.setString(1, email);

    ResultSet rs = select.executeQuery();

    if(!rs.next()){
      logger.info("Did not find user with email:"+email);
      return null;
    }

    User user = new User(
        email,
        UserType.fromDatabaseString(rs.getString("user_role_level")),
        rs.getInt("id")
    );
    return user;
  }

  public User getActiveUserFromEmail(String email) throws Exception {

    String query = "SELECT * from users where email = ? and is_active = TRUE";
    PreparedStatement select = connection.prepareStatement(query);
    select.setString(1, email);

    ResultSet rs = select.executeQuery();

    if(!rs.next()){
      logger.info("Did not find user with email:"+email);
      return null;
    }

    User user = new User(
        email,
        UserType.fromDatabaseString(rs.getString("user_role_level")),
        rs.getInt("id")
    );
    return user;
  }


  public void deleteAllUsers() throws Exception {
    logger.info("Deleting all users");

    String query = "TRUNCATE table users CASCADE";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.executeUpdate();
  }

  public void activateUser(String email) throws Exception {
    String query = """
        UPDATE users
        SET is_active = TRUE
        WHERE email = ?
        """;

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, email);
    statement.executeUpdate();
  }

  public void deactivateUser(String email) throws Exception {
    String query = """
        UPDATE users
        SET is_active = FALSE
        WHERE email = ?
        """;

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, email);
    statement.executeUpdate();
  }

  public void updateUserData(UserData userData, String email) throws Exception{
    String query = """
      UPDATE users
      set image_path = ?,
      username = ?
      WHERE email = ?
    """;

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, userData.getImageFilePath());
    statement.setString(2, userData.getUsername());
    statement.setString(3,email);

    statement.executeUpdate();
  }

  public UserData getUserData(String email) throws Exception {
    String query = """
      SELECT 
        image_path,
        username
      FROM users
      WHERE email = ?    
    """;

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, email);

    ResultSet rs = statement.executeQuery();
    if(rs.next()){
      UserData userData = new UserData();
      userData.setImageFilePath(rs.getString("image_path"));
      userData.setUsername(rs.getString("username"));
      return userData;
    }
    throw new UnauthorizedError("Cannot access user data");
  }

  public int countUsers() throws Exception{
    String query = """
         SELECT COUNT(id)
         FROM users
         """;
    PreparedStatement statement = connection.prepareStatement(query);

    ResultSet rs = statement.executeQuery();
    rs.next();

    return rs.getInt(1);
  }
}
