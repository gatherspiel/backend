package app.result.error.group;

public class InvalidGroupRequestError extends Exception{
  public InvalidGroupRequestError(String message){
    super(message);
  }
}
