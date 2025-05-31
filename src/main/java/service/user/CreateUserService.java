package service.user;


import app.data.auth.User;
import database.user.CreateUserRepository;
import database.utils.ConnectionProvider;

public class CreateUserService {

  CreateUserRepository createUserRepository;

  public CreateUserService(){
    this.createUserRepository = new CreateUserRepository();
  }

  /*
    Creates a new admin user. This function will only be used for testing, and will not be associated with an
    endpoint
   */
  public User createAdmin(String email, ConnectionProvider connectionProvider) throws Exception{
    return createUserRepository.createAdmin(email, connectionProvider.getDatabaseConnection());
  }

  public User createStandardUser(String email, ConnectionProvider connectionProvider) throws Exception{
    return createUserRepository.createStandardUser(email, connectionProvider.getDatabaseConnection());
  }



}
