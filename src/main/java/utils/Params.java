package utils;

import java.util.Optional;

/**
 * This class contains utility functions for retrieving environment variable information
 */
public class Params {

  public static Optional<String> getDatabasePassword(){
    var password = System.getenv("DB_PASSWORD");
    if(password == null || password.isBlank()){
      return Optional.empty();
    }
    return Optional.of(password);
  }

  public static String getAuthUrl(){
    var url = System.getenv("AUTH_URL");
    if(url == null || url.isBlank()){
      return "https://karqyskuudnvfxohwkok.supabase.co/auth/v1/";
    }
    return url;
  }
}
