package database;

import app.data.Convention;
import app.data.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ConventionsRepository {
    public void insertConventions(Convention[] conventions, Connection conn) throws Exception{

        for (Convention convention: conventions) {
            System.out.println(convention.getTitle());
            String query = "INSERT INTO events (url, name, is_convention) VALUES(?, ?, true)";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.clearBatch();
            insert.setString(1, convention.getLink());
            insert.setString(2, convention.getTitle());
            insert.executeUpdate();
        }
    }
}
