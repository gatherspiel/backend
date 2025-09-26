package service.user;


import app.users.data.User;
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

  public UserService(DataProvider dataProvider){
    this.dataProvider = dataProvider;
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

  public void rollbackChanges() throws Exception{
    dataProvider.getRepository().rollbackChanges();
  }
  public void commitChanges() throws Exception {
    dataProvider.getRepository().commitChanges();
  }

}
