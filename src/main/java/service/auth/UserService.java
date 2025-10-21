package service.auth;


import app.result.error.StackTraceShortener;
import app.result.error.UnauthorizedError;
import app.users.data.User;
import app.users.data.UserData;
import database.user.UserRepository;

import java.sql.Connection;

public class UserService {

  public static class DataProvider {
    UserRepository userRepository;
    private DataProvider(UserRepository userRepository){
      this.userRepository = userRepository;
    }

    public UserRepository getRepository(){
      return userRepository;
    }

    public static DataProvider createDataProvider(Connection connection) {
      return new DataProvider(new UserRepository(connection));
    }
  }

  private DataProvider dataProvider;
  private User user;
  public UserService(DataProvider dataProvider,User user){
    this.dataProvider = dataProvider;
    this.user = user;
  }

  /*
    Creates a new admin user. This function will only be used for testing, and will not be associated with an
    endpoint
   */
  public User createAdmin(String email) throws Exception{
    return dataProvider.getRepository().createAdmin(email);
  }

  public User createStandardUser(String email) throws Exception{
    return dataProvider.getRepository().createStandardUser(email);
  }

  public User createTester(String email) throws Exception{
    return dataProvider.getRepository().createTester(email);
  }

  public User getUser(String email) throws Exception{
    return dataProvider.getRepository().getUserFromEmail(email);
  }

  public UserData getLoggedInUserData() throws Exception{
    System.out.println(user.getEmail());
    System.out.println(user.getAdminLevel());
    if(!user.isLoggedInUser()){
      throw new UnauthorizedError("Cannot access user data without logging in");
    }
    return dataProvider.getRepository().getUserData(user.getEmail());
  }
  public User getActiveUser(String email) throws Exception {
    return dataProvider.getRepository().getActiveUserFromEmail(email);
  }

  public void activateUser(String email) throws Exception{
    dataProvider.getRepository().activateUser(email);
  }

  public void deactivateUser(String email) throws Exception {
    dataProvider.getRepository().deactivateUser(email);
  }

  public boolean userExists(String email) throws Exception {
      User user = dataProvider.getRepository().getUserFromEmail(email);
      return !(user == null);
  }

  public int countUsers() throws Exception {
    return dataProvider.getRepository().countUsers();
  }

  public void updateUser(UserData userData) throws Exception{
    if(!user.isLoggedInUser()){
      Exception ex = new UnauthorizedError("User must log in to edit their user data");
      ex.setStackTrace(StackTraceShortener.generateDisplayStackTrace(ex.getStackTrace()));
      throw ex;
    }
    dataProvider.getRepository().updateUserData(userData, user.getEmail());
  }

  public void rollbackChanges() throws Exception{
    dataProvider.getRepository().rollbackChanges();
  }
  public void commitChanges() throws Exception {
    dataProvider.getRepository().commitChanges();
  }

}
