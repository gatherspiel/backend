package service.user;


import app.data.auth.User;
import database.user.UserRepository;
import database.utils.ConnectionProvider;

public class UserService {

  UserRepository userRepository;

  public UserService(){
    this.userRepository = new UserRepository();
  }

  /*
    Creates a new admin user. This function will only be used for testing, and will not be associated with an
    endpoint
   */
  public User createAdmin(String email, ConnectionProvider connectionProvider) throws Exception{
    return userRepository.createAdmin(email, connectionProvider.getDatabaseConnection());
  }

  public User createStandardUser(String email, ConnectionProvider connectionProvider) throws Exception{
    return userRepository.createStandardUser(email, connectionProvider.getDatabaseConnection());
  }

  public User getUser(String email, ConnectionProvider connectionProvider) throws Exception{
    return userRepository.getUserFromEmail(email, connectionProvider.getDatabaseConnection());
  }
}
