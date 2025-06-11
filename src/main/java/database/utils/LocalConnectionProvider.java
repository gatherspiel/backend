package database.utils;

import java.sql.Connection;

public abstract class LocalConnectionProvider extends ConnectionProvider {
  abstract public Connection getDatabaseConnection() throws Exception;

}
