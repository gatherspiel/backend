package database.search;

import app.users.User;
import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import app.result.error.SearchParameterException;
import service.data.SearchParameterValidator;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SearchParams {
  public static final String DAYS_OF_WEEK = "days";
  public static final String CITY = "city";
  public static final String AREA = "area";
  public static final String NAME = "name";
  public static final String DISTANCE = "distance";
  public static final String USER_GROUP_EVENTS = "userGroupEvents";
  //Query parameters for filtering results by a specific field value.
  private final LinkedHashMap<String, String> params;
  private static final HashMap<String, String> paramQueryMap;

  private String locationGroupFilter = "";

  private final Logger logger;

  private String getUserGroupEventsParam = null;

  static {
    paramQueryMap = new HashMap<>();
    paramQueryMap.put(CITY, "COALESCE(locations.city,locs.city) = ?");
    paramQueryMap.put(NAME, "groups.name = ?");
  }

  public SearchParams(LinkedHashMap<String, String> params) throws SearchParameterException {

    this.logger = LogUtils.getLogger();
    this.params = new LinkedHashMap<>();

    params.keySet().forEach(param->{
      if(param.equals(DAYS_OF_WEEK)){
        SearchParameterValidator.validateDaysParameter(params.get(param));
        this.params.put(param, params.get(param));
      } else if(param.equals(CITY)) {
        this.params.put(param, SameLocationData.getDatabaseCityName(params.get(param)));
      } else if (param.equals(AREA)) {
        locationGroupFilter = params.get(param);
      } else if (param.equals(NAME)){
        this.params.put(param, params.get(param).replace("_", " "));
      } else if (param.equals(USER_GROUP_EVENTS)) {
        this.getUserGroupEventsParam =  params.get(USER_GROUP_EVENTS);
      } else {
        if (!param.equals(DISTANCE)) {
          logger.warn("Invalid parameter {} submitted. It will not be used in the search query", param);
        }
      }
    });
  }

  public PreparedStatement generateGroupSearchQuery(Connection conn, boolean isHomepage) throws Exception {
    String query = isHomepage ? getQueryForHomepageSearchResult() : getQueryForAllResultsWithRecurringEvents();
    return generatePreparedStatement(query, conn, paramQueryMap.get(CITY), null);
  }

  public PreparedStatement generateEventSearchQuery(Connection conn, User user) throws Exception {
    String query = """
        SELECT
        DISTINCT ON (events.id, groups.id, groups.name)
          groups.id as groupId,
          groups.name as groupName,
          groups.game_type_tags,
          events.name as eventName,
          events.id as eventId,
          COALESCE(event_time.start_time::time,weekly_event_time.start_time) as start_time,
          weekly_event_time.day_of_week as day_of_week,
          locations.state,
          locations.street_address,
          locations.zip_code,
          locations.city as city
        FROM events
        LEFT JOIN event_group_map on events.id = event_group_map.event_id
        LEFT JOIN groups on groups.id = event_group_map.group_id
        LEFT JOIN event_time on event_time.event_id = events.id
        LEFT JOIN weekly_event_time on weekly_event_time.event_id = events.id
        LEFT JOIN locations on events.location_id = locations.id
      """;

    User searchWithUser = null;
    String userGroupEvents = getUserGroupEventsParam;
    if(user.isLoggedInUser() && userGroupEvents != null && Boolean.valueOf(userGroupEvents)){
      searchWithUser = user;
    }

    return generatePreparedStatement(
        query,
        conn,
        "locations.city=?",
        searchWithUser);
  }

  public PreparedStatement generateQueryForOneTimeEvents(Connection conn) throws Exception {
    String query = """
      SELECT
        DISTINCT ON (events.id, groups.id, groups.name)
          events.id as eventId,
          groups.id as groupId,
          groups.name,
          groups.url,
          groups.image_path,
          groups.description,
          groups.game_type_tags,
          events.name as eventName,
          events.description as eventDescription,
          event_time.start_time,
          event_time.end_time,
          event_time.day_of_week,
          locations.state,
          locations.street_address,
          locations.zip_code,
          locations.city as city,
          locs.city as groupCity
        FROM groups
        LEFT JOIN event_group_map on groups.id = event_group_map.group_id
        LEFT JOIN events on event_group_map.event_id = events.id
        LEFT JOIN event_time on event_time.event_id = events.id
        LEFT JOIN locations on events.location_id = locations.id
        LEFT JOIN location_group_map on groups.id = location_group_map.group_id
        LEFT JOIN locations as locs on location_group_map.location_id = locs.id
      """;
    ArrayList<String> whereClauses = new ArrayList<>();

    for(String param: params.keySet()){
      whereClauses.add(paramQueryMap.get(param));
    }

    whereClauses.add("start_time IS NOT NULL");
    query = query + " WHERE ";
    query = query + String.join( " AND ", whereClauses.toArray(new String[0]));

    PreparedStatement select = conn.prepareStatement(query);
    int i = 1;
    for(String param: params.keySet()){
      select.setString(i, params.get(param));
      i++;
    }
    return select;
  }

  private PreparedStatement generatePreparedStatement(String query, Connection conn,String cityQuery,User user) throws Exception{
    ArrayList<String> whereClauses = new ArrayList<>();

    for(String param: params.keySet()){
      if(param.equals(DAYS_OF_WEEK)){
        String[] days = params.get(param).split(",");
        String[] paramData = new String[days.length];

        for(int i=0; i<days.length;i++){
          paramData[i] = "CAST(? as dayofweek)";
        }
        whereClauses.add("weekly_event_time.day_of_week IN("+String.join(",",paramData)+")");
      } else if (param.equals(CITY)) {
        whereClauses.add(cityQuery);
      }else {
        whereClauses.add(paramQueryMap.get(param));
      }
    }
    whereClauses.add("is_hidden IS NOT TRUE");
    whereClauses.add("groups.name IS NOT NULL");

    //This query is necessary to filter by user.
    if(user != null){
      query = query + "LEFT JOIN group_admin_data on group_admin_data.group_id = groups.id";
      whereClauses.add("group_admin_data.user_id = ?");
    }

    query += " WHERE ";
    query += String.join( " AND ", whereClauses.toArray(new String[0]));

    PreparedStatement select = conn.prepareStatement(query);
    int i = 1;



    for(String param: params.keySet()){

      if(param.equals(DAYS_OF_WEEK)){
        String[] days = params.get(param).split(",");
        for (String day : days) {
          select.setString(i, day.toLowerCase());
          i++;
        }
      } else {
        select.setString(i, params.get(param));
        i++;
      }
    }

    //Add a query parameter for the user.
    if(user != null){
      select.setInt(i, user.getId());
    }

    return select;
  }

  private static String getQueryForHomepageSearchResult(){
    return """
      SELECT
        DISTINCT ON (events.id, groups.id, groups.name)
          groups.id as groupId,
          groups.name,
          groups.url,
          groups.description,
          groups.game_type_tags,
          locations.city as city,
          locs.city as groupCity,
          weekly_event_time.day_of_week
        FROM groups
       LEFT JOIN event_group_map on groups.id = event_group_map.group_id
       LEFT JOIN events on event_group_map.event_id = events.id
       LEFT JOIN weekly_event_time on weekly_event_time.event_id = events.id
       LEFT JOIN locations on events.location_id = locations.id
       LEFT JOIN location_group_map on groups.id = location_group_map.group_id
       LEFT JOIN locations as locs on location_group_map.location_id = locs.id
      """;
  }

  private static String getQueryForAllResultsWithRecurringEvents() {
    return """
        SELECT
          DISTINCT ON (events.id, groups.id, groups.name)
            events.id as eventId,
            groups.id as groupId,
            groups.name,
            groups.url,
            groups.description,
            groups.game_type_tags,
            groups.image_path,
            events.name as eventName,
            events.description as eventDescription,
            weekly_event_time.start_time as start_time,
            weekly_event_time.end_time as end_time,
            weekly_event_time.day_of_week as day_of_week,
            locations.state,
            locations.street_address,
            locations.zip_code,
            locations.city as city,
            locs.city as groupCity
          FROM groups
            LEFT JOIN event_group_map on groups.id = event_group_map.group_id
            LEFT JOIN events on event_group_map.event_id = events.id
            LEFT JOIN weekly_event_time on weekly_event_time.event_id = events.id
            LEFT JOIN locations on events.location_id = locations.id
            LEFT JOIN location_group_map on groups.id = location_group_map.group_id
            LEFT JOIN locations as locs on location_group_map.location_id = locs.id
           
        """;
  }

  public boolean hasLocationGroupParam(){
    return !locationGroupFilter.isEmpty();
  }

  //TODO: Rename method to indicate that only cities can be part of location groups
  public PreparedStatement getQueryForLocationGroups(Connection conn) throws Exception {

    String query = """
      SELECT city, name from locations
      LEFT JOIN location_tag_mapping on locations.id = location_tag_mapping.location_id
      LEFT JOIN location_tag on location_tag.id = location_tag_mapping.location_tag_id
      WHERE name = ?
    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, locationGroupFilter.toLowerCase());
    return select;
  }

  public static LinkedHashMap<String, String> generateParameterMapFromQueryString(Context ctx) {
    LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();

    String userGroupEvents = ctx.queryParam(SearchParams.USER_GROUP_EVENTS);
    if(userGroupEvents != null && !userGroupEvents.isEmpty()){
      paramMap.put(SearchParams.USER_GROUP_EVENTS, userGroupEvents);
    }

    String day = ctx.queryParam(SearchParams.DAYS_OF_WEEK);
    if(day != null && !day.isEmpty()){
      paramMap.put(SearchParams.DAYS_OF_WEEK, day);
    }

    String location = ctx.queryParam(SearchParams.CITY);
    if(location != null && !location.isEmpty()){
      paramMap.put(SearchParams.CITY, location);
    }

    String area = ctx.queryParam(SearchParams.AREA);
    if(area != null && !area.isEmpty()){
      paramMap.put(SearchParams.AREA, area);
    }

    String name = ctx.queryParam(SearchParams.NAME);
    if(name != null && !name.isEmpty()){
      paramMap.put(SearchParams.NAME, name);
    }

    String distance = ctx.queryParam(SearchParams.DISTANCE);
    if(distance != null && !distance.isEmpty()){
      paramMap.put(SearchParams.DISTANCE, distance);
    }

    return paramMap;
  }
}
