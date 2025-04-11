package service;

import java.time.DayOfWeek;

public class SearchParameterValidator {

  public static boolean isValidAddress(String address) {
    String[] data = address.split(",");
    return data.length == 3;
  }

  public static void validateDay(String day) throws SearchParameterException {

    System.out.println(day);
    for(DayOfWeek validDay: DayOfWeek.values()){
       if(day.toLowerCase().equals(validDay.name().toLowerCase())){
         return;
       }
    }
    throw new SearchParameterException("Invalid day");
  }
}

