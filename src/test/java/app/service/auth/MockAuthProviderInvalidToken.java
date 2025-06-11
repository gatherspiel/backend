package app.service.auth;

import app.user.data.RegisterUserResponse;
import service.auth.AuthProvider;

import java.util.Optional;

public class MockAuthProviderInvalidToken implements AuthProvider {

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    throw new Exception("Error");
  }

  @Override
  public RegisterUserResponse registerUser(String username, String password) throws Exception{
    throw new Exception("Error");
  }
}
