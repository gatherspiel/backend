package service.provider;

import app.data.auth.User;
import database.utils.ConnectionProvider;

public abstract class DataProvider {
  protected User user;
  protected ConnectionProvider connectionProvider;

  protected DataProvider(User user, ConnectionProvider connectionProvider){
    this.user = user;
    this.connectionProvider = connectionProvider;
  }

  public User getUser(){
    return user;
  }
}
