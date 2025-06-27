package app.users.data;

public enum PermissionName {

  USER_CAN_EDIT("userCanEdit");

  private String name;

  PermissionName(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }
}
