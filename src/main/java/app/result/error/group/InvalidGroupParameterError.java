package app.result.error.group;

public class InvalidGroupParameterError extends RuntimeException {
  public InvalidGroupParameterError(String message) {
    super(message);
  }
}
