package app.result.error;

public class InvalidGroupParameterError extends RuntimeException {
  public InvalidGroupParameterError(String message) {
    super(message);
  }
}
