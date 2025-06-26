package app.data.event;

public enum EventLocationState {
  DC("DC"),
  MD("MD"),
  VA("VA"),
  WV("WV");

  private String name;

  EventLocationState(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }

  public static EventLocationState fromDatabaseString(String dbString) throws Exception{
    for(EventLocationState eventLocationState: EventLocationState.values()){
      if(eventLocationState.toString().equals(dbString)){
        return eventLocationState;
      }
    }
    throw new Exception("Invalid string:"+dbString +" for UserType enum");
  }
}