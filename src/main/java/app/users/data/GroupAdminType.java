package app.users.data;

public enum GroupAdminType {
  GROUP_ADMIN("group_admin"),
  GROUP_MEMBER("group_member"),
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