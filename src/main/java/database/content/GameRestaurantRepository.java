package database.content;

import app.location.GameRestaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GameRestaurantRepository {
  Logger logger;

  Connection conn;
  public GameRestaurantRepository(Connection conn) {
    logger = LogUtils.getLogger();
    this.conn = conn;
  }

  public HashMap<Integer, GameRestaurant> getGameRestauarants() throws Exception{
    String query =
        "SELECT * from game_restaurants JOIN locations on game_restaurants.location_id = locations.id";
    PreparedStatement select = conn.prepareStatement(query);

    ResultSet rs = select.executeQuery();
    HashMap<Integer, GameRestaurant> restaurants = new HashMap<Integer,GameRestaurant>();
    while(rs.next()){
      GameRestaurant gameRestaurant = new GameRestaurant();
      gameRestaurant.setName(rs.getString("name"));
      gameRestaurant.setId(rs.getInt("id"));
      gameRestaurant.setUrl(rs.getString("url"));

      String city = rs.getString("city");
      String state = rs.getString("state");
      String streetAddress = rs.getString("street_address");
      String zipCode = rs.getString("zip_code");
      String addressStr = streetAddress +", "+ city + ", "+state+" "+zipCode;
      gameRestaurant.setLocation(addressStr);

      restaurants.put(gameRestaurant.getId(), gameRestaurant);
    }
    return restaurants;

  }
  public void insertGameRestaurants(
    GameRestaurant[] gameRestaurants
  )
    throws Exception {
    LocationsRepository locationRepository = new LocationsRepository(conn);

    for (GameRestaurant gameRestaurant : gameRestaurants) {
      int location_id = locationRepository.insertLocation(
        gameRestaurant.getLocation()
      );

      if (!hasGameRestaurant(gameRestaurant, location_id)) {
        logger.debug(gameRestaurant.getName());
        String query =
          "INSERT INTO game_restaurants (url, name, location_id) VALUES(?, ?, ?)";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.clearBatch();
        insert.setString(1, gameRestaurant.getUrl());
        insert.setString(2, gameRestaurant.getName());
        insert.setInt(3, location_id);
        insert.executeUpdate();
      }
    }
  }

  public boolean hasGameRestaurant(
    GameRestaurant gameRestaurant,
    int locationId
  )
    throws Exception {
    String query =
      "SELECT * from game_restaurants where name = ? and url=? and location_id=?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, gameRestaurant.getName());
    select.setString(2, gameRestaurant.getUrl());
    select.setInt(3, locationId);

    ResultSet rs = select.executeQuery();
    return rs.next();
  }
}
