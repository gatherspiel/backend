package database;

import app.data.Convention;
import app.data.GameStore;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GameStoreRepository {
    public void insertGameStores(GameStore[] gameStores, Connection conn) throws Exception{

        LocationsRepository locationRepository = new LocationsRepository();

        for (GameStore gameStore: gameStores) {

            int location_id = locationRepository.insertLocation(gameStore.getLocation(), conn);
            System.out.println(gameStore.getName());
            String query = "INSERT INTO game_stores (url, name, location_id) VALUES(?, ?, ?)";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.clearBatch();
            insert.setString(1, gameStore.getLink());
            insert.setString(2, gameStore.getName());
            insert.setInt(3, location_id);
            insert.executeUpdate();
        }
    }
}
