package app.users.data;

public enum EventAdminType {
  EVENT_MODERATOR("event_moderator"),
  EVENT_RSVP("event_rsvp");

  private String name;

  EventAdminType(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }

  public static EventAdminType fromDatabaseString(String dbString) throws Exception{
    for(EventAdminType eventAdminType: EventAdminType.values()){
      if(eventAdminType.toString().equals(dbString)){
        return eventAdminType;
      }
    }
    throw new Exception("Invalid string:"+dbString +" for EventAdminType enum");
  }
}