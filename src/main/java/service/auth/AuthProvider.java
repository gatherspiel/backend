package service.auth;

import app.data.auth.User;
import database.utils.ConnectionProvider;

import java.util.Optional;

public interface AuthProvider {
  public Optional<String> getUsernameFromToken(String token) throws Exception;


}
