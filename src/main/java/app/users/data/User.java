package app.users.data;

public class User {
  private String email;
  private UserType userType;
  private int id;
  private UserData userData = new UserData();

  public User(){
    userType = UserType.USER;
  }

  public User(String email, UserType userType, int id){
    this.email = email;
    this.userType = userType;
    this.id = id;
  }

  public boolean isLoggedInUser(){
    return email != null && !email.isBlank() && userType != UserType.READONLY;
  }

  public void setId(int id){
    this.id = id;
  }

  public int getId(){
    return id;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public String getEmail(){
    return email;
  }

  public String getAdminLevel(){
    return userType.name();
  }

  public boolean isSiteAdmin(){
    return userType.equals(UserType.SITE_ADMIN);
  }

  public UserData getUserData() {
    return userData;
  }

  public void setUserData(UserData userData){
    this.userData = userData;
  }

  @Override
  public boolean equals(Object obj) {
    User other = (User)obj;
    return other.getId() == this.getId() || other.getEmail().equals(this.getEmail());
  }

  @Override
  public int hashCode() {
    return (this.id + " "+this.email).hashCode();
  }
}
