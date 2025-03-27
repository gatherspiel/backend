package database;

import app.data.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GroupsRepository {

    public void insertGroups(Group[] groups, Connection conn) throws Exception{

        for (Group group: groups) {
            String query = "INSERT INTO groups (name, url) VALUES(?, ?)";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setString(1, group.title);
            insert.setString(2, group.link);
            insert.executeUpdate();
        }

    }
}
