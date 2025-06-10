package app.service.auth;

import service.auth.AuthProvider;

import java.util.Optional;

public class NoErrorMockAuthProvider implements AuthProvider {

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    return Optional.of(token);
  }
}
