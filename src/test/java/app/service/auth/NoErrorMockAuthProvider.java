package app.service.auth;

import service.auth.AuthProvider;

import java.util.Optional;

public class NoErrorMockAuthProvider implements AuthProvider {

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    return Optional.of(token);
  }

  @Override
  public void registerUser(String username, String password) throws Exception{
    return;
  }
}
