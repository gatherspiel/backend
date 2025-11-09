package app.database.utils;

import database.content.*;
import database.user.UserRepository;
import database.utils.ConnectionProvider;
import java.sql.Connection;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class BulkUpdateService {
  Logger logger;

  public BulkUpdateService() {
    logger = LogUtils.getLogger();
  }

  public void deleteUsers(ConnectionProvider connectionProvider) throws Exception{
    UserRepository userRepository = new UserRepository(connectionProvider.getDatabaseConnection());
    userRepository.deleteAllUsers();
  }

  public void bulkUpdate(BulkUpdateRequest data, ConnectionProvider connectionProvider)
    throws Exception {
    Connection conn = connectionProvider.getDatabaseConnection();
    conn.setAutoCommit(false);

    try {
      GroupsRepository groupsRepository = new GroupsRepository(conn);
      groupsRepository.insertGroups(data.getGroups());
      logger.info("Number of groups to insert:" + data.getGroups().length);

    } catch (Exception e) {
      logger.error("Error inserting groups");
      throw e;
    }

    try {
      ConventionsRepository conventionsRepository = new ConventionsRepository(conn);
      conventionsRepository.insertConventions(data.getConventions());
    } catch (Exception e) {
      logger.error("Error inserting conventions");
      throw e;
    }

    try {
      GameStoreRepository gameStoreRepository = new GameStoreRepository(conn);
      gameStoreRepository.insertGameStores(data.getGameStores());
    } catch (Exception e) {
      logger.error("Error inserting game stores");
      throw e;
    }

    try {
      GameRestaurantRepository gameRestaurantRepository = new GameRestaurantRepository(conn);
      gameRestaurantRepository.insertGameRestaurants(
        data.getGameRestaurants()
      );
    } catch (Exception e) {
      logger.error("Error inserting game restaurants");
      throw e;
    }

    try {
      EventRepository eventRepository = new EventRepository(conn);
      eventRepository.addEvents(data.getGroups());
    } catch (Exception e) {
      logger.error("Error inserting events");
      throw e;
    }

    try {
      LocationTagRepository locationTagRepository = new LocationTagRepository(conn);
      locationTagRepository.insertLocationTags(data.getLocationTags());
    } catch (Exception e) {
      logger.error("Error inserting locationTags");
      throw e;
    }

    conn.commit();
    conn.close();

    logger.info("Done with bulk update");
  }
}
