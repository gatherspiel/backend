package service;

import app.data.Convention;
import app.data.GameRestaurant;
import app.data.GameStore;
import app.result.GameLocationData;
import database.ConventionsRepository;
import database.GameRestaurantRepository;
import database.GameStoreRepository;
import database.LocationsRepository;
import database.utils.ConnectionProvider;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GameLocationsService {

  Logger logger;

  public GameLocationsService(){
    logger = LogUtils.getLogger();
  }

  public GameLocationData getGameLocations(ConnectionProvider connectionProvider, LocalDate date) throws Exception{

    logger.info("Retrieving game locations");
    Connection connection = connectionProvider.getDatabaseConnection();
    ConventionsRepository conventionsRepository = new ConventionsRepository();
    HashMap<Integer, Convention> conventions = conventionsRepository.getConventions(connection, date);

    logger.info("Retrieving game restaurants");

    GameRestaurantRepository restaurantRepository = new GameRestaurantRepository();
    HashMap<Integer, GameRestaurant> gameRestaurants = restaurantRepository.getGameRestauarants(connection);

    logger.info("Retrieving game stores");

    GameStoreRepository gameStoreRepository = new GameStoreRepository();
    HashMap<Integer, GameStore> gameStores= gameStoreRepository.getGameStores(connection);

    logger.info("Done retrieving data");

    GameLocationData locationData = new GameLocationData();
    locationData.setConventions(conventions);
    locationData.setGameRestaurants(gameRestaurants);
    locationData.setGameStores(gameStores);

    return locationData;
  }

  public void insertAddress(ConnectionProvider connectionProvider, String address) throws Exception{
    LocationsRepository locationsRepository = new LocationsRepository();
    locationsRepository.insertLocation(address, connectionProvider.getDatabaseConnection());
  }

  public int countLocations(ConnectionProvider connectionProvider) throws  Exception{
    LocationsRepository locationsRepository = new LocationsRepository();
    return locationsRepository.countLocations(connectionProvider.getDatabaseConnection());
  }

  public TreeSet<String> getAllEventLocations(
      ConnectionProvider connectionProvider,
      String areaFilter) throws Exception{
    LocationsRepository locationsRepository = new LocationsRepository();
    Connection connection = connectionProvider.getDatabaseConnection();

    ArrayList<String> cities = locationsRepository.listALlLocationCities(areaFilter, connection);

    return new TreeSet<>(cities);
  }

  public int addCity(ConnectionProvider connectionProvider, String cityName) throws Exception {
    LocationsRepository locationsRepository = new LocationsRepository();
    return locationsRepository.getLocationIdForCity(cityName, connectionProvider.getDatabaseConnection());
  }
}
