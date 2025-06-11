package service.auth;



import app.user.data.RegisterUserRequest;
import app.user.data.RegisterUserResponse;

import java.util.Optional;

public interface AuthProvider {
  public Optional<String> getUsernameFromToken(String token) throws Exception;

  public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception;

}
