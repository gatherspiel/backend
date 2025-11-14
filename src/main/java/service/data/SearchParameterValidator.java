package service.data;

import app.result.error.SearchParameterException;

import java.time.DayOfWeek;

public class SearchParameterValidator {

  public static boolean isValidAddress(String address) {
    String[] data = address.split(",");
    return data.length == 3;
  }

  public static double validateAndRetrieveDistanceParameter(String distanceParam) throws SearchParameterException{
    double distance;
    try {
      distance = Double.parseDouble(distanceParam);
    } catch(Exception e){
      throw new SearchParameterException("Invalid value for distance");
    }

    if(distance < 0){
      throw new SearchParameterException("Distance cannot be negative");
    }
    return distance;
  }
  public static void validateDaysParameter(String dayParam) throws SearchParameterException {
    String[] days = dayParam.split(",");
    for (String s : days) {

      boolean isValidDay = false;
      for (DayOfWeek day : DayOfWeek.values()) {
        if (s.equalsIgnoreCase(day.name())) {
          isValidDay = true;
          break;
        }
      }
      if (!isValidDay) {
        throw new SearchParameterException("Invalid day");
      }
    }
  }
}
