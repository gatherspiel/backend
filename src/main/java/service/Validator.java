package service;

public class Validator {

  public static boolean isValidAddress(String address) {
    String[] data = address.split(",");
    return data.length == 3;
  }

  public static void validateDay(String day) throws SearchParameterException {

  }
}

