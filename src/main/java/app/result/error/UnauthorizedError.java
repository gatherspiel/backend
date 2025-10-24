package app.result.error;

public class UnauthorizedError extends RuntimeException {
  public UnauthorizedError(String message) {
    super(message);
    this.setStackTrace(StackTraceShortener.generateDisplayStackTrace(this.getStackTrace()));
  }
}
