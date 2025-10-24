package app.users.data;

public enum EventAdminType {
  EVENT_MODERATOR("event_moderator");

  private String name;

  EventAdminType(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }
}