package service;

import app.data.Convention;
import app.data.GameRestaurant;
import app.data.GameStore;
import app.result.GameLocationData;
import database.ConventionsRepository;
import database.GameRestaurantRepository;
import database.GameStoreRepository;
import database.utils.ConnectionProvider;

import java.sql.Connection;
import java.util.HashMap;

public class GameLocationsService {

  public GameLocationData getGameLocations(ConnectionProvider connectionProvider) throws Exception{

    Connection connection = connectionProvider.getDatabaseConnection();
    ConventionsRepository conventionsRepository = new ConventionsRepository();
    HashMap<Integer, Convention> conventions = conventionsRepository.getConventions(connection);

    GameRestaurantRepository restaurantRepository = new GameRestaurantRepository();
    HashMap<Integer, GameRestaurant> gameRestaurants = restaurantRepository.getGameRestauarants(connection);

    GameStoreRepository gameStoreRepository = new GameStoreRepository();
    HashMap<Integer, GameStore> gameStores= gameStoreRepository.getGameStores(connection);

    GameLocationData locationData = new GameLocationData();
    locationData.setConventions(conventions);
    locationData.setGameRestaurants(gameRestaurants);
    locationData.setGameStores(gameStores);

    return locationData;
  }
}
