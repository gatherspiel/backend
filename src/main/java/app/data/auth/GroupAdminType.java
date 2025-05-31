package app.data.auth;

public enum GroupAdminType {
  GROUP_ADMIN("group_admin"),
  GROUP_MODERATOR("group_moderator");

  private String name;

  GroupAdminType(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }
}