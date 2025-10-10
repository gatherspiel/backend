package app.result.error.group;

import app.result.error.StackTraceShortener;

public class GroupNotFoundError extends Exception{
  public GroupNotFoundError(String message){
    super(message);
    this.setStackTrace(StackTraceShortener.generateDisplayStackTrace(this.getStackTrace()));

  }
}
