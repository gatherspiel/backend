package database.search;

import java.util.HashMap;

public class SameLocationData {
  private static final HashMap<String, String> sameCityData = new HashMap<String, String>();
  static {
    sameCityData.put("DC", "Washington");
    sameCityData.put("Washington, DC", "Washington");
  }

  public static String getDatabaseCityName(String cityName){
    if(sameCityData.containsKey(cityName)){
      return sameCityData.get(cityName);
    }
    return cityName;
  }
}
