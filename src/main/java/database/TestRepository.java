package database;

import database.utils.ConnectionProvider;
import java.sql.*;

public class TestRepository {

  public int countLocations() throws Exception {
    ConnectionProvider connectionProvider = new ConnectionProvider();
    Connection conn = connectionProvider.getDatabaseConnection();
    Statement st;
    int count = 0;
    try {
      st = conn.createStatement();
      ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM locations");

      while (rs.next()) {
        count = rs.getInt(1);
      }
      conn.close();
      st.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return count;
  }
}
