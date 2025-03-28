package database;

import app.data.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GroupsRepository {

    public void insertGroups(Group[] groups, Connection conn) throws Exception{

        LocationsRepository locationsRepository = new LocationsRepository();
        Set<String> ulrsInDb = getGroupsInDatabase(groups, conn);
        for (Group group: groups) {

            if (!ulrsInDb.contains(group.link)){
                String query = "INSERT INTO groups (name, url, summary) VALUES(?, ?, ?) returning id";
                PreparedStatement insert = conn.prepareStatement(query);
                insert.setString(1, group.title);
                insert.setString(2, group.link);
                insert.setString(3, group.summary);
                ResultSet rs = insert.executeQuery();
                if(rs.next()){
                    int groupId = rs.getInt(1);
                    for(String location: group.getLocations().split(",")){
                        int locationId = locationsRepository.getLocationIdForCity(location.trim(),conn);

                        String groupLocationQuery = "INSERT INTO location_group_map(location_id, group_id) VALUES(?, ?)";
                        PreparedStatement groupLocationInsert = conn.prepareStatement(groupLocationQuery);
                        groupLocationInsert.setInt(1, locationId);
                        groupLocationInsert.setInt(2, groupId);
                        groupLocationInsert.executeUpdate();
                    }
                }else {
                    throw new Exception();
                }

            }
        }
    }

    // This is a workaround for an issue related to using prepared statements with duplicates.
    public Set<String> getGroupsInDatabase(Group[] groups, Connection conn) throws Exception {
        String query = "SELECT url from groups WHERE url IN (?)";

        String[] urls = Arrays.stream(groups).map(group->group.link).toArray(String[]::new);
        PreparedStatement select = conn.prepareStatement(query);
        select.setString(1, String.join(",", urls));

        ResultSet rs = select.executeQuery();

        Set<String> urlsInDb = new HashSet<String>();
        while(rs.next()){
            urlsInDb.add(rs.getString(1));
        }
        return urlsInDb;
    }

    public int getGroupId(Group group, Connection conn) throws Exception {

        String query = "SELECT * from groups where url = ?";
        PreparedStatement select = conn.prepareStatement(query);
        select.setString(1, group.link);

        ResultSet rs = select.executeQuery();
        if(!rs.next()) {
            return -1;
        }
        return rs.getInt(1);
    }
}
