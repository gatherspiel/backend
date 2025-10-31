package app.result.error.group;

import app.result.error.StackTraceShortener;

public class InvalidEventParameterError extends RuntimeException {
  public InvalidEventParameterError(String message) {
    super(message);
    this.setStackTrace(StackTraceShortener.generateDisplayStackTrace(this.getStackTrace()));
  }
}
