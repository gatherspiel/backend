package database;

import app.data.Group;
import app.result.groupPage.GroupPageData;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class GroupsRepository {

  private static Logger logger = LogUtils.getLogger();
  public void insertGroups(Group[] groups, Connection conn) throws Exception {
    LocationsRepository locationsRepository = new LocationsRepository();
    Set<String> urlsInDb = getGroupsInDatabase(groups, conn);
    for (Group group : groups) {
      if (!urlsInDb.contains(group.url)) {
        urlsInDb.add(group.url);

        String query =
          "INSERT INTO groups (name, url, summary) VALUES(?,?,?) returning id";

        PreparedStatement insert = conn.prepareStatement(query);
        insert.setString(1, group.name);
        insert.setString(2, group.url);
        insert.setString(3, group.summary);

        ResultSet rs = insert.executeQuery();
        if (rs.next()) {
          int groupId = rs.getInt(1);
          for (String location : group.getCities()) {
            int locationId = locationsRepository.getLocationIdForCity(
              location.trim(),
              conn
            );

            String groupLocationQuery =
              "INSERT INTO location_group_map(location_id, group_id) VALUES(?, ?)";
            PreparedStatement groupLocationInsert = conn.prepareStatement(
              groupLocationQuery
            );
            groupLocationInsert.setInt(1, locationId);
            groupLocationInsert.setInt(2, groupId);
            groupLocationInsert.executeUpdate();
          }
        } else {
          logger.error("Error inserting groups");
          throw new Exception();
        }
      }
    }
  }

  // This is a workaround for an issue related to using prepared statements with duplicates.
  // TODO: Use SQL in clause to optimize performance.
  public Set<String> getGroupsInDatabase(Group[] groups, Connection conn)
    throws Exception {
    String query = "SELECT url from groups";

    PreparedStatement select = conn.prepareStatement(query);
    ResultSet rs = select.executeQuery();

    Set<String> urlsInDb = new HashSet<String>();
    while (rs.next()) {
      urlsInDb.add(rs.getString(1));
    }
    return urlsInDb;
  }

  public int getGroupId(Group group, Connection conn) throws Exception {
    String query = "SELECT * from groups where url = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, group.url);

    ResultSet rs = select.executeQuery();
    if (!rs.next()) {
      return -1;
    }
    return rs.getInt(1);
  }

  // TODO: Consider deleting this function.
  public GroupPageData getGroupPageData(String groupName, String locationTag, Connection conn) throws Exception{

    String query = """
       SELECT
        
         groups.id,
         groups.name,
         groups.url,
         groups.summary
        
        
        from groups
        LEFT JOIN location_group_map on location_group_map.group_id = groups.id
        LEFT JOIN locations on locations.id = location_group_map.location_id
        LEFT JOIN location_tag_mapping on location_tag_mapping.location_id = locations.id
        LEFT JOIN location_tag on location_tag.id = location_tag_mapping.location_tag_id
        WHERE groups.name = ?
        AND location_tag.name = ?
        
      """;
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, groupName.replace('_', ' '));
    select.setString(2, locationTag.toLowerCase());

    ResultSet rs = select.executeQuery();
    if(!rs.next()){
      throw new Exception("Group with submitted name and tag does not exist");
    }
    GroupPageData groupPageData = new GroupPageData(
        rs.getInt("id"),
        rs.getString("name"),
        rs.getString("url"),
        rs.getString("summary")
    );
    return groupPageData;

  }
}
