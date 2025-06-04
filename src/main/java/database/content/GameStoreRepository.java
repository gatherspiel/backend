package database.content;

import app.data.GameStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GameStoreRepository {
  Logger logger;

  public GameStoreRepository() {
    logger = LogUtils.getLogger();
  }

  public HashMap<Integer, GameStore> getGameStores(Connection conn) throws Exception{
    String query =
        "SELECT * from game_stores JOIN locations on game_stores.location_id = locations.id";
    PreparedStatement select = conn.prepareStatement(query);

    ResultSet rs = select.executeQuery();
    HashMap<Integer, GameStore> gameStores = new HashMap<Integer,GameStore>();
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

      gameStores.put(gameStore.getId(), gameStore);
    }
    return gameStores;

  }

  public void insertGameStores(GameStore[] gameStores, Connection conn)
    throws Exception {
    LocationsRepository locationRepository = new LocationsRepository();

    for (GameStore gameStore : gameStores) {
      if (!gameStoreExists(gameStore, conn)) {
        int location_id = locationRepository.insertLocation(
          gameStore.getLocation(),
          conn
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

  public boolean gameStoreExists(GameStore gameStore, Connection conn)
    throws Exception {
    String query = "SELECT * from game_stores where url = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, gameStore.getUrl());

    ResultSet rs = select.executeQuery();
    return rs.next();
  }
}
