package app.data.auth;

public enum UserType {
  SITE_ADMIN("site_admin"),
  TESTER("tester"),
  USER("user");

  private String name;

  UserType(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }
}
