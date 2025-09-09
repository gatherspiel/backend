package database.content;

import app.location.GameStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GameStoreRepository {
  Logger logger;
  Connection conn;
  public GameStoreRepository(Connection conn) {
    this.conn = conn;
    logger = LogUtils.getLogger();

  }

  public TreeMap<String, GameStore> getGameStores() throws Exception{
    String query =
        "SELECT * from game_stores JOIN locations on game_stores.location_id = locations.id";
    PreparedStatement select = conn.prepareStatement(query);

    ResultSet rs = select.executeQuery();
    TreeMap<String, GameStore> gameStores = new TreeMap<String,GameStore>();
    while(rs.next()){
      GameStore gameStore = new GameStore();
      gameStore.setName(rs.getString("name"));
      gameStore.setId(rs.getInt("id"));
      gameStore.setUrl(rs.getString("url"));

      String city = rs.getString("city");
      String state = rs.getString("state");
      String streetAddress = rs.getString("street_address");
      String zipCode = rs.getString("zip_code");
      String addressStr = streetAddress +", "+ city + ", "+state+" "+zipCode;
      gameStore.setLocation(addressStr);

      gameStores.put(gameStore.getName(), gameStore);
    }
    return gameStores;

  }

  public void insertGameStores(GameStore[] gameStores)
    throws Exception {
    LocationsRepository locationRepository = new LocationsRepository(conn);

    for (GameStore gameStore : gameStores) {
      if (!gameStoreExists(gameStore)) {
        int location_id = locationRepository.insertLocation(
          gameStore.getLocation()
        );
        logger.debug("Inserting game store:" + gameStore.getName());
        String query =
          "INSERT INTO game_stores (url, name, location_id) VALUES(?, ?, ?)";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.clearBatch();
        insert.setString(1, gameStore.getUrl());
        insert.setString(2, gameStore.getName());
        insert.setInt(3, location_id);
        insert.executeUpdate();
      }
    }
  }

  public boolean gameStoreExists(GameStore gameStore)
    throws Exception {
    String query = "SELECT * from game_stores where url = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, gameStore.getUrl());

    ResultSet rs = select.executeQuery();
    return rs.next();
  }
}
