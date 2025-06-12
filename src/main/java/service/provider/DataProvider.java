package service.provider;

import app.data.auth.User;
import database.utils.ConnectionProvider;

import java.sql.Connection;

public abstract class DataProvider {
  protected Connection connection;

  protected DataProvider(Connection connection){
    this.connection = connection;
  }

}
