package service.data;

import java.time.DayOfWeek;

public class SearchParameterValidator {

  public static boolean isValidAddress(String address) {
    String[] data = address.split(",");
    return data.length == 3;
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
