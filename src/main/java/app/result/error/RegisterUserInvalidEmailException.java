package app.result.error;

public class RegisterUserInvalidEmailException extends RuntimeException {
  public RegisterUserInvalidEmailException(String message) {
    super(message);
  }
}
