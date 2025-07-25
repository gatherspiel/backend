package database.search;

import app.result.GroupSearchResult;
import app.result.HomeResult;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
      String groupCity = rs.getString("groupCity");

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
        boolean isRecurring = false;

        LocalDateTime startTime = LocalDateTime.now();

        if(dayOfWeek != null){
          startTime = startTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(dayOfWeek.toUpperCase())));;
        }
        LocalDateTime endTime = startTime.plusHours(1);

        Timestamp start = rs.getTimestamp("start_time");
        Timestamp end = rs.getTimestamp("end_time");
        if(start != null) {
          startTime = start.toLocalDateTime();
        }
        if(end != null){
          endTime = end.toLocalDateTime();
        }
        if (start == null){
          logger.warn("A null value for the event day is being used as a temporary placeholder to represent recurring events ");
          isRecurring = true;
        }

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
              city,
              startTime,
              endTime,
              isRecurring
          );
        }
      }
    }
    return searchResult;
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
