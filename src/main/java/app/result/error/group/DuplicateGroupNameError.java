package app.result.error.group;

public class DuplicateGroupNameError extends RuntimeException {
  public DuplicateGroupNameError(String message) {
    super(message);
  }
}
