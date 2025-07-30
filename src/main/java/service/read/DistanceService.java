package service.read;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

class LatLong {
  private final String latitude;
  private final String longtitude;

  public LatLong(String latitude, String longtitude){
    this.latitude = latitude;
    this.longtitude = longtitude;
  }

  //TODO: Use Haversine distance
  public double getDistance(LatLong other){
    return 1;
  }
}

public class DistanceService {

  private static HashMap<Integer, LatLong> zipCodeLocation = new HashMap<>();

  //This will store the cityLocation as an average LatLong of all the city zip codes.
  private static HashMap<String, LatLong> cityLocations = new HashMap<>();

  public static void loadData(){
    /*
      Create in memory string that only consists of lat long, and city name or each zip code.
      Save location of zip codes to zipCodeLocation.
      Save cityZipCodes to location

      -Add unit tests
      -Download file with zip code ->city mapping.
     */
  }

  public static Optional<Double> getDistance(String city, String city2){
    if(!cityLocations.containsKey(city)){
      return Optional.empty();
    }
    if(!cityLocations.containsKey(city2)){
      return Optional.empty();
    }

    Double distance = cityLocations.get(city).getDistance(cityLocations.get(city2));
    return Optional.of(distance);
  }
}
