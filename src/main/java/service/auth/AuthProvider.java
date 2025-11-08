package service.auth;



import app.users.RegisterUserRequest;
import app.users.RegisterUserResponse;

import java.util.Optional;

public interface AuthProvider {
  public Optional<String> getUsernameFromToken(String token) throws Exception;

  public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception;

}
