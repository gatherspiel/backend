package database.user;

import app.groups.Event;
import app.groups.Group;
import app.result.error.UnauthorizedError;
import app.result.error.group.InvalidEventParameterError;
import app.users.*;
import database.BaseRepository;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

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

  public UserMemberData getUserMemberData(int userId) throws Exception{

    try {

      UserMemberData userMemberData = new UserMemberData();
      String userGroupQuery = """
        SELECT
          group_id,
          group_admin_level,
          name
        FROM group_admin_data
        LEFT JOIN groups on groups.id = group_admin_data.group_id
        WHERE user_id = ?
      """;

      PreparedStatement userGroupStatement = connection.prepareStatement(userGroupQuery);
      userGroupStatement.setInt(1, userId);

      ResultSet rs = userGroupStatement.executeQuery();
      while (rs.next()) {
        Group group = new Group();
        group.setId(rs.getInt("group_id"));
        group.setName(rs.getString("name"));

        if(rs.getString("group_admin_level").equals(GroupAdminType.GROUP_MEMBER.toString())){
          userMemberData.addGroupAsMember(group);
        } else {
          userMemberData.addGroupAsModerator(group);
        }
      }

      String userEventQuery = """
        SELECT
          event_admin_level,
          event_admin_data.event_id,
          event_group_map.group_id,
          name,
          rsvp_time,
          start_time,
          weekly_event_time.day_of_week
        FROM event_admin_data
        LEFT JOIN events on events.id = event_admin_data.event_id
        LEFT JOIN weekly_event_time on events.id = weekly_event_time.event_id
        LEFT JOIN event_group_map on events.id = event_group_map.event_id
        WHERE user_id = ?
      """;

      PreparedStatement userEventStatement = connection.prepareStatement(userEventQuery);
      userEventStatement.setInt(1, userId);

      ResultSet rs2 = userEventStatement.executeQuery();
      while (rs2.next()) {

        Event event = new Event();
        event.setId(rs2.getInt("event_id"));
        event.setName(rs2.getString("name"));

        String eventDay = rs2.getString("day_of_week");
        if(eventDay == null){
          logger.error("Event RSVPS are not fully supported for one time events");
        }

        LocalDate nextEventDate = LocalDate.now();
        DayOfWeek eventDayValue = DayOfWeek.valueOf(eventDay.toUpperCase());
        if(!eventDayValue.equals(nextEventDate.getDayOfWeek())){
          nextEventDate = nextEventDate.with(TemporalAdjusters.next(eventDayValue));
        }

        LocalDateTime pastEventDate =
          LocalDateTime.now()
            .with(TemporalAdjusters.previous(DayOfWeek.valueOf(eventDay.toUpperCase())));

        event.setStartDate(nextEventDate);
        event.setDay(eventDay);
        event.setGroupId(rs2.getInt("group_id"));

        LocalTime eventTime =
          rs2.getTimestamp("start_time")
              .toLocalDateTime().toLocalTime();
        event.setStartTime(eventTime);

        if(rs2.getString("event_admin_level").equals(EventAdminType.EVENT_RSVP.toString())){
          LocalDateTime rsvpTime = rs2.getTimestamp("rsvp_time").toLocalDateTime();
          if(rsvpTime.isAfter(pastEventDate)) {
            userMemberData.addEventAsMember(event);
          }
        } else {
          userMemberData.addEventAsModerator(event);
        }
      }
      return userMemberData;
    } catch (Exception e){
      logger.error(e.getMessage());
      throw e;
    }
  }

  public void joinGroup(int groupId,User user) throws Exception{
    try {
      String joinGroupQuery = """
        INSERT INTO group_admin_data(group_admin_level, group_id, user_id) VALUES(cast(? as group_admin_level), ?, ?)
      """;

      PreparedStatement statement = connection.prepareStatement(joinGroupQuery);
      statement.setString(1, GroupAdminType.GROUP_MEMBER.toString());
      statement.setInt(2, groupId);
      statement.setInt(3, user.getId());
      statement.executeUpdate();
    } catch(Exception e){
      if(e.getMessage().contains("Key (user_id, group_id)")){
        throw new InvalidEventParameterError("User is already a member of the group");
      }
      if(e.getMessage().contains("Key (group_id)")){
        throw new InvalidEventParameterError("Invalid group");
      }
      logger.error(e);
      throw e;
    }
  }

  public void leaveGroup(int groupId, User user) throws Exception{
    try {
      String leaveGroupQuery = """
      DELETE from group_admin_data
      WHERE group_id = ? and user_id = ?
      """;

      PreparedStatement statement = connection.prepareStatement(leaveGroupQuery);
      statement.setInt(1, groupId);
      statement.setInt(2, user.getId());
      int update = statement.executeUpdate();

      if(update == 0){
        throw new InvalidEventParameterError("User is not a member of the group");
      }
    } catch(Exception e){
      logger.error(e);
      throw e;
    }
  }

  public void deleteMemberDataForGroup(int groupId) throws Exception{
    try {
      String deleteQuery = """
      DELETE from group_admin_data
      WHERE group_id = ?
      """;

      PreparedStatement statement = connection.prepareStatement(deleteQuery);
      statement.setInt(1, groupId);
      int update = statement.executeUpdate();
    } catch(Exception e){
      logger.error(e);
      throw e;
    }
  }
}
