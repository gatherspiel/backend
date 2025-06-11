package app.service.auth;

import app.user.data.RegisterUserResponse;
import service.auth.AuthProvider;

import java.util.Optional;

public class NoErrorMockAuthProvider implements AuthProvider {

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    return Optional.of(token);
  }

  @Override
  public RegisterUserResponse registerUser(String username, String password) throws Exception{
    return new RegisterUserResponse(username, "1-1-2025");
  }
}
