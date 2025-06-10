package service.auth;



import java.util.Optional;

public interface AuthProvider {
  public Optional<String> getUsernameFromToken(String token) throws Exception;

  public void registerUser(String username, String password) throws Exception;

}
