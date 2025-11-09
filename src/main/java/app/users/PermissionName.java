package app.users;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionName {

  USER_CAN_EDIT("userCanEdit"),
  USER_CAN_RSVP("userCanRsvp"),
  USER_IS_MEMBER("userIsMember");

  private String name;

  PermissionName(String name){
    this.name = name;
  }

  @JsonValue
  public String getValue(){
    return this.name;
  }

  @Override
  public String toString(){
    return name;
  }
}
