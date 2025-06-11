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

  public static String getSupabaseApiKey(){
    return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImthcnF5c2t1dWRudmZ4b2h3a29rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE5ODQ5NjgsImV4cCI6MjA1NzU2MDk2OH0.TR-Pn6dknOTtqS9y-gxK_S1-nw6TX-sL3gRH2kXJY_I";
  }

  public static String getSupabasePasswordCheckUrl(){
    return "https://karqyskuudnvfxohwkok.supabase.co/auth/v1/token?grant_type=password";
  }
}
