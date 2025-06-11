package app.result.error;

public class DuplicateUsernameException extends RuntimeException {
  public DuplicateUsernameException(String message) {
    super(message);
  }
}
