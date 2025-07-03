package app.users.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionName {

  USER_CAN_EDIT("userCanEdit");

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
