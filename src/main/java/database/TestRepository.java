package database;

import java.sql.*;

public class TestRepository {
    public int countLocations() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Loaded Driver");
        System.out.println(System.getenv("DB_PASSWORD"));
        String url =
                "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?" +
                "user=postgres.karqyskuudnvfxohwkok&"+
                "password="+System.getenv("DB_PASSWORD");
        System.out.println(url);
        int count = 0;

        Connection conn;
        Statement st;
        try {
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM locations");

            while(rs.next()) {
                count = rs.getInt(1);
                System.out.println("Count:"+count);
            }
            conn.close();
            st.close();;

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
        return count;
    }
}
