package app.result.error.group;

public class DuplicateEventError extends RuntimeException {
  public DuplicateEventError(String message) {
    super(message);
  }
}
