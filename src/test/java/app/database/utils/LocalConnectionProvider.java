package app.database.utils;
import database.utils.ConnectionProvider;

import java.sql.Connection;

public abstract class LocalConnectionProvider extends ConnectionProvider {
  abstract public Connection getDatabaseConnection() throws Exception;
}
