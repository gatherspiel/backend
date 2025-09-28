package database.search;

import app.groups.data.GameTypeTag;
import app.result.listing.GroupSearchResult;
import app.result.listing.HomeResult;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

public class SearchRepository {

  private Connection conn;


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

      String day = rs.getString("day_of_week");

      if (!(searchParams.hasLocationGroupParam() && !locationsWithTag.contains(groupCity))) {
        searchResult.addGroup(
            groupId,
            groupName,
            url,
            groupCity,
            day != null ? DayOfWeek.valueOf(day.toUpperCase()) : null,
            getTagsFromResultSet(rs)
        );
      }
    }
    return searchResult;
  }

  public GroupSearchResult getGroupsWithDetails(
      GroupSearchParams searchParams
  ) throws Exception {

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

        searchResult.addGroup(groupId, groupName, url, groupSummary, groupCity, getTagsFromResultSet(rs));

        Integer eventId = rs.getInt("eventId");
        String eventName = rs.getString("eventname");
        String description = rs.getString("eventDescription");
        String dayOfWeek = rs.getString("day_of_week");

        String streetAddress = rs.getString("street_address");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zipCode = rs.getString("zip_code");

        Timestamp startData = rs.getTimestamp("start_time");
        Timestamp endData = rs.getTimestamp("end_time");
        LocalTime startTime = LocalTime.MIN;
        LocalTime endTime = LocalTime.MAX;

        if(startData != null){
          var startDateTime = startData.toLocalDateTime();
          startTime = startTime.plusHours(startDateTime.getHour()).plusMinutes(startDateTime.getMinute());
        }

        if(endData != null){
          var endDateTime = endData.toLocalDateTime();
          endTime = LocalTime.MIN.plusHours(endDateTime.getHour()).plusMinutes(endDateTime.getMinute());
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

          /* Groups that have no events will be returned from the query with empty event data. If a group has at
          least one event, each result associated with the group will have event data.
           */
          if(dayOfWeek != null){
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
        searchResult.addGroup(groupId, groupName, url, groupSummary, groupCity,getTagsFromResultSet(rs));

        Integer eventId = rs.getInt("eventId");
        String eventName = rs.getString("eventname");
        String description = rs.getString("eventDescription");
        String dayOfWeek = rs.getString("day_of_week");


        String streetAddress = rs.getString("street_address");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zipCode = rs.getString("zip_code");

        var startData = rs.getTime("start_time");
        var endData = rs.getTime("end_time");
        LocalDateTime startTime = LocalDateTime.MIN;
        LocalDateTime endTime = LocalDateTime.MAX;

        if(startData != null){
          startTime = rs.getTimestamp("start_time").toLocalDateTime();
        }

        if(endData != null){
          endTime = rs.getTimestamp("end_time").toLocalDateTime();
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

  private GameTypeTag[] getTagsFromResultSet(ResultSet rs) throws Exception{
    var gameTypeTags = rs.getArray("game_type_tags");
    var gameTypeTagsToDisplay = new GameTypeTag[0];
    if(gameTypeTags != null){
      final var tagData = (String[])gameTypeTags.getArray();
      gameTypeTagsToDisplay = new GameTypeTag[tagData.length];
      for(int i=0; i<tagData.length; i++){
        gameTypeTagsToDisplay[i] = GameTypeTag.valueOf(tagData[i].replace(" ","_").toUpperCase());
      }
    }
    return gameTypeTagsToDisplay;
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
