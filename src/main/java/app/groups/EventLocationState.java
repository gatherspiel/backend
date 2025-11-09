package app.groups;

import java.util.Map;

public enum EventLocationState {
  DC("DC"),
  MD("MD"),
  VA("VA"),
  TBD("TBD"),
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

    return EventLocationState.TBD;
  }

}