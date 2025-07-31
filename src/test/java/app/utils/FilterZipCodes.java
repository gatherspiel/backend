package app.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FilterZipCodes {

  private static final Set<String> validStateCodes = new HashSet<>(Arrays.asList("DC","MD","VA", "WY"));
  public static void main(String[] args) throws Exception{


    List<List<String>> records = Files.readAllLines(Paths.get("src/test/fixtures/ZipCodeData.csv"))
      .stream()
      .map(line -> {
        List<String> data = Arrays.asList(line.split(","));
        if(data.size()==1){
          return data;
        }
        String zipCode = data.get(1);
        String cityName = data.get(2);
        String stateCode = data.get(4);
        String latitude = data.get(7);
        String longtitude = data.get(8);

        return Arrays.asList(zipCode, cityName, stateCode, latitude, longtitude);
      })
      .filter(list->{
        String stateCode = list.get(2);
        return validStateCodes.contains((stateCode));
      })
      .toList();

    records.forEach((dataRow)->{
      System.out.println(String.join(",", dataRow));
    });
  }
}
