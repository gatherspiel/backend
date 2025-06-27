package database.search;

import app.result.GroupSearchResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class SearchRepository {

  public GroupSearchResult getGroups(
    GroupSearchParams searchParams,
    Connection conn
  )
    throws Exception {
    PreparedStatement statement = searchParams.generateSearchQuery(conn);
    ResultSet rs = statement.executeQuery();

    Set<String> locationsWithTag = getLocationsWithTag(searchParams, conn);

    GroupSearchResult searchResult = new GroupSearchResult();
    while (rs.next()) {
      Integer groupId = rs.getInt("groupId");

      String groupName = rs.getString("name");
      String url = rs.getString("url");
      String groupSummary = rs.getString("description");
      String groupCity = rs.getString("groupCity");

      if (!(searchParams.hasLocationGroupParam() && !locationsWithTag.contains(groupCity))) {
        searchResult.addGroup(groupId, groupName, url, groupSummary, groupCity);

        Integer eventId = rs.getInt("eventId");
        String eventName = rs.getString("eventname");
        String description = rs.getString("description");
        String dayOfWeek = rs.getString("day_of_week");

        String streetAddress = rs.getString("street_address");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zipCode = rs.getString("zip_code");

        if (eventId != 0) {
          String address =
              streetAddress + ", " + city + ", " + state + " " + zipCode;

          if (
              streetAddress == null ||
                  city == null ||
                  state == null ||
                  zipCode == null
          ) {
            address = "";
          }
          searchResult.addEvent(
              groupId,
              eventId,
              eventName,
              description,
              dayOfWeek,
              address,
              city
          );
        }
      }
    }
    return searchResult;
  }

  private Set<String> getLocationsWithTag(
      GroupSearchParams searchParams,
      Connection conn) throws Exception
  {

    if(!searchParams.hasLocationGroupParam()){
      return new HashSet<String>();
    }
    PreparedStatement statement = searchParams.getQueryForLocationGroups(conn);
    ResultSet rs = statement.executeQuery();

    Set<String> locationsWithTag = new HashSet<String>();
    while(rs.next()){
      locationsWithTag.add(rs.getString("city"));
    }
    return locationsWithTag;
  }
}
