package service.read;

import database.search.SameLocationData;
import service.data.ZipCodeAndCityData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class LatLong {
  private final double latitude;
  private final double longtitude;

  private static final double RADIUS = 3957.976;

  public LatLong(String latitude, String longtitude){
    this.latitude = Double.parseDouble(latitude);
    this.longtitude = Double.parseDouble(longtitude);
  }

  public LatLong(Double latitude, Double longtitude){
    this.latitude = latitude;
    this.longtitude = longtitude;
  }

  double getLatitude(){
    return latitude;
  }

  double getLongitude(){
    return longtitude;
  }

  public double getDistance(LatLong other){
    double latDistance = Math.toRadians(other.getLatitude() - latitude);
    double lonDistance = Math.toRadians(other.getLongitude() - longtitude);

    double latRadiansA = Math.toRadians(latitude);
    double latRadiansB = Math.toRadians(longtitude);

    double a = Math.pow(Math.sin(latDistance/2),2) +
               Math.pow(Math.sin(lonDistance/2),2) * Math.cos(latRadiansA) * Math.cos(latRadiansB);

    return RADIUS * 2 * Math.asin(Math.sqrt(a));
  }

  public String toString(){
    return latitude + ":"+longtitude;
  }
}

public class DistanceService {

  private static HashMap<Integer, LatLong> zipCodeLocations = new HashMap<>();

  //This will store the cityLocation as an average LatLong of all the city zip codes.
  private static HashMap<String, LatLong> cityLocations = new HashMap<>();

  public static void loadData(){

    String[] rows = ZipCodeAndCityData.data.split("\n");
    HashMap<String, Set<LatLong>> cityZipCodes = new HashMap<>();

    for(String row: rows){

      String[] data = row.split(",");

      int zipCode = Integer.parseInt(data[0].trim());
      String city = data[3];
      LatLong location = new LatLong(data[1], data[2]);

      zipCodeLocations.put(zipCode, location);

      if(!cityZipCodes.containsKey(city)){
        cityZipCodes.put(city,new HashSet<>());
      }
      cityZipCodes.get(city).add(location);
    }

    for(String city: cityZipCodes.keySet()){
      double latitude = 0;
      double longtitude = 0;

      Set<LatLong> locations = cityZipCodes.get(city);

      for(LatLong location: locations){
        latitude += location.getLatitude();
        longtitude += location.getLongitude();
      }

      double locationCount = locations.size();
      latitude = latitude/locationCount;
      longtitude = longtitude/locationCount;

      cityLocations.put(city, new LatLong(latitude, longtitude));
    }
  }

  public static Optional<Double> getDistance(String city, String city2){

    String dbCity = SameLocationData.getDatabaseCityName(city);
    String dbCity2 = SameLocationData.getDatabaseCityName(city2);

    if(!cityLocations.containsKey(dbCity)){
      return Optional.empty();
    }
    if(!cityLocations.containsKey(dbCity2)){
      return Optional.empty();
    }

    Double distance = cityLocations.get(dbCity).getDistance(cityLocations.get(dbCity2));
    return Optional.of(distance);
  }

}
