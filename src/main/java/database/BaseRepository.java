package database;

import java.sql.Connection;

public class BaseRepository {
  protected Connection connection;

  protected BaseRepository(Connection connection){
    this.connection = connection;
  }

  public void beginTransaction() throws Exception {
    connection.beginRequest();
  }
  public void rollbackChanges() throws Exception{
    connection.rollback();
  }

  public void commitChanges() throws Exception {
    connection.commit();
  }
}
