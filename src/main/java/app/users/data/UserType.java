package app.users.data;

public enum UserType {
  SITE_ADMIN("site_admin"),
  TESTER("tester"),
  READONLY("read_ony"),
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
