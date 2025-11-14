package app.result.error;

public class SearchParameterException extends RuntimeException {

  public SearchParameterException(String text) {
    super(text);
    this.setStackTrace(StackTraceShortener.generateDisplayStackTrace(this.getStackTrace()));
  }
}
