package app.data.event;

import java.util.Map;

public enum EventLocationState {
  DC("DC"),
  MD("MD"),
  VA("VA"),
  WV("WV");

  private String name;

  private static final Map<String, EventLocationState> NAME_MAP = Map.of(
      "Virginia", EventLocationState.VA,
      "Maryland", EventLocationState.MD
  );
  EventLocationState(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }

  public static EventLocationState fromString(String stateStr) throws Exception{
    if(NAME_MAP.containsKey(stateStr)){
      return NAME_MAP.get(stateStr);
    }
    for(EventLocationState eventLocationState: EventLocationState.values()){
      if(eventLocationState.toString().equals(stateStr)){
        return eventLocationState;
      }
    }
    throw new Exception("Invalid string:"+stateStr +" for UserType enum");
  }

}