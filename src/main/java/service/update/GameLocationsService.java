package service.update;

import app.location.Convention;
import app.location.GameRestaurant;
import app.location.GameStore;
import database.content.ConventionsRepository;
import database.content.GameRestaurantRepository;
import database.content.GameStoreRepository;
import database.content.LocationsRepository;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class GameLocationsService {

  Logger logger;
  Connection conn;
  public GameLocationsService( Connection conn){
    this.conn= conn;
    logger = LogUtils.getLogger();
  }


  public LinkedHashMap<String, Convention> getConventions(LocalDate date) throws Exception{
    ConventionsRepository conventionsRepository = new ConventionsRepository(conn);
    return conventionsRepository.getConventions(date);
  }

  public TreeMap<String, GameRestaurant> getGameRestaurants() throws Exception{
    GameRestaurantRepository restaurantRepository = new GameRestaurantRepository(conn);
    return restaurantRepository.getGameRestauarants();
  }

  public TreeMap<String, GameStore> getGameStores() throws Exception{
    GameStoreRepository gameStoreRepository = new GameStoreRepository(conn);
    return gameStoreRepository.getGameStores();
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
