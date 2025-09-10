package database.search;

import app.result.listing.GroupSearchResult;
import app.result.listing.HomeResult;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

public class SearchRepository {

  private Connection conn;

  private static final Logger logger = LogUtils.getLogger();

  public SearchRepository(Connection conn){
    this.conn = conn;
  }

  public HomeResult getGroupsForHomepage(
    GroupSearchParams searchParams
  )
    throws Exception {
    PreparedStatement statement = searchParams.generateSearchQuery(conn, true);
    ResultSet rs = statement.executeQuery();

    Set<String> locationsWithTag = getLocationsWithTag(searchParams);

    HomeResult searchResult = new HomeResult();
    while (rs.next()) {

      Integer groupId = rs.getInt("groupId");

      String groupName = rs.getString("name");
      String url = rs.getString("url");

      String groupCity;
      if(rs.getString("city") != null){
        groupCity = rs.getString("city");
      } else {
        groupCity = rs.getString("groupCity");
      }

      if (!(searchParams.hasLocationGroupParam() && !locationsWithTag.contains(groupCity))) {
        searchResult.addGroup(groupId, groupName, url, groupCity);

      }
    }
    return searchResult;
  }

  public GroupSearchResult getGroupsWithDetails(
      GroupSearchParams searchParams
  )
      throws Exception {

    //TODO: Add query here for one time events.
    PreparedStatement statement = searchParams.generateSearchQuery(conn, false);
    ResultSet rs = statement.executeQuery();

    Set<String> locationsWithTag = getLocationsWithTag(searchParams);

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
        String description = rs.getString("eventDescription");
        String dayOfWeek = rs.getString("day_of_week");

        String streetAddress = rs.getString("street_address");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zipCode = rs.getString("zip_code");

        LocalTime startTime = LocalTime.ofInstant(rs.getTime("start_time").toInstant(), ZoneId.systemDefault());
        LocalTime endTime = LocalTime.ofInstant(rs.getTime("end_time").toInstant(), ZoneId.systemDefault());

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
          searchResult.addWeeklyEvent(
              groupId,
              eventId,
              eventName,
              description,
              dayOfWeek,
              address,
              startTime,
              endTime
          );
        }
      }
    }
    addOneTimeEvents(searchParams, searchResult, locationsWithTag);
    return searchResult;
  }

  private void addOneTimeEvents(GroupSearchParams searchParams, GroupSearchResult searchResult, Set<String> locationsWithTag) throws Exception {

    PreparedStatement statement = searchParams.generateQueryForOneTimeEvents(conn);
    ResultSet rs = statement.executeQuery();
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
        String description = rs.getString("eventDescription");
        String dayOfWeek = rs.getString("day_of_week");

        String streetAddress = rs.getString("street_address");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zipCode = rs.getString("zip_code");

        LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
        LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();

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
          searchResult.addOneTimeEvent(
              groupId,
              eventId,
              eventName,
              description,
              dayOfWeek,
              address,
              city,
              startTime,
              endTime
          );
        }
      }
    }
  }

  private Set<String> getLocationsWithTag(
      GroupSearchParams searchParams) throws Exception
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
