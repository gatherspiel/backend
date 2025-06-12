package app.result.error;

public class InvalidGroupRequestError extends Exception{
  public InvalidGroupRequestError(String message){
    super(message);
  }
}
