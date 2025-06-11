package service.auth;



import app.user.data.RegisterUserResponse;

import java.util.Optional;

public interface AuthProvider {
  public Optional<String> getUsernameFromToken(String token) throws Exception;

  public RegisterUserResponse registerUser(String username, String password) throws Exception;

}
