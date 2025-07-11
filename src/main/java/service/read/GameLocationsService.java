package service.read;

import app.location.Convention;
import app.location.GameRestaurant;
import app.location.GameStore;
import app.result.GameLocationData;
import database.content.ConventionsRepository;
import database.content.GameRestaurantRepository;
import database.content.GameStoreRepository;
import database.content.LocationsRepository;
import database.utils.ConnectionProvider;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class GameLocationsService {

  Logger logger;
  Connection conn;
  public GameLocationsService( Connection conn){
    this.conn= conn;
    logger = LogUtils.getLogger();
  }

  public GameLocationData getGameLocations(LocalDate date) throws Exception{

    logger.info("Retrieving game locations");
    ConventionsRepository conventionsRepository = new ConventionsRepository(conn);
    HashMap<Integer, Convention> conventions = conventionsRepository.getConventions(date);

    logger.info("Retrieving game restaurants");

    GameRestaurantRepository restaurantRepository = new GameRestaurantRepository(conn);
    HashMap<Integer, GameRestaurant> gameRestaurants = restaurantRepository.getGameRestauarants();

    logger.info("Retrieving game stores");

    GameStoreRepository gameStoreRepository = new GameStoreRepository(conn);
    HashMap<Integer, GameStore> gameStores= gameStoreRepository.getGameStores();

    logger.info("Done retrieving data");

    GameLocationData locationData = new GameLocationData();
    locationData.setConventions(conventions);
    locationData.setGameRestaurants(gameRestaurants);
    locationData.setGameStores(gameStores);

    return locationData;
  }

  public void insertAddress(String address) throws Exception{
    LocationsRepository locationsRepository = new LocationsRepository(conn);
    locationsRepository.insertLocation(address);
  }

  public int countLocations() throws  Exception{
    LocationsRepository locationsRepository = new LocationsRepository(conn);
    return locationsRepository.countLocations();
  }

  public TreeSet<String> getAllEventLocations(
      String areaFilter) throws Exception{
    LocationsRepository locationsRepository = new LocationsRepository(conn);

    ArrayList<String> cities = locationsRepository.listALlLocationCities(areaFilter, conn);

    return new TreeSet<>(cities);
  }

  public int addCity(String cityName) throws Exception {
    LocationsRepository locationsRepository = new LocationsRepository(conn);
    return locationsRepository.getLocationIdForCity(cityName);
  }
}
