package service.provider;

import java.sql.Connection;

public abstract class DataProvider {
  protected Connection connection;

  protected DataProvider(Connection connection){
    this.connection = connection;
  }

}
