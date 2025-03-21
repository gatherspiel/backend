package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class RepositoryUtils {
    public static Connection getDatabaseConnection() throws Exception{
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
        Connection connection = DriverManager.getConnection(url);
        return connection;

    }
}
