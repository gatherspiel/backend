package service.user;


import app.data.auth.User;

public class CreateUserService {

  /*
    Creates a new admin user. This function will only be used for testing, and will not be associated with an
    endpoint
   */
  public User createAdmin(String email){
    //TODO: Implement logic and make sure endpoint cannot run in prod.
    return null;
  }

  public User createStandardUser(String email) {
    return null;
  }



}
