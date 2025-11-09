package app.users;

public enum UserType {
  SITE_ADMIN("site_admin"),
  TESTER("tester"),
  READONLY("read_ony"),
  EVENT_ADMIN("event_admin"),
  EVENT_RSVP("event_rsvp"),
  USER("user");

  private String name;

  UserType(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }

  public static UserType fromDatabaseString(String dbString) throws Exception{
    for(UserType userType: UserType.values()){
      if(userType.toString().equals(dbString)){
        return userType;
      }
    }
    throw new Exception("Invalid string:"+dbString +" for UserType enum");
  }


}
