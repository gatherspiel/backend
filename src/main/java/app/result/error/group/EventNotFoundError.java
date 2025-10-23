package app.result.error.group;

import app.result.error.StackTraceShortener;

public class EventNotFoundError extends RuntimeException {
  public EventNotFoundError(String message) {
    super(message);
    this.setStackTrace(StackTraceShortener.generateDisplayStackTrace(this.getStackTrace()));
  }
}
