package database.search;

import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import service.data.SearchParameterException;
import service.data.SearchParameterValidator;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GroupSearchParams {
  public static final String DAY_OF_WEEK = "day";
  public static final String CITY = "city";
  public static final String AREA = "area";
  public static final String NAME = "name";
  public static final String DISTANCE = "distance";
  //Query parameters for filtering results by a specific field value
  private final LinkedHashMap<String, String> params;
  private static final HashMap<String, String> paramQueryMap;

  private String locationGroupFilter = "";

  private static final String SORT_ORDER = " ORDER BY groups.name, groups.id, events.id ASC ";
  private Logger logger;

  static {
    paramQueryMap = new HashMap<String,String>();
    paramQueryMap.put(DAY_OF_WEEK,"day_of_week = cast(? AS dayofweek)");
    paramQueryMap.put(CITY, "COALESCE(locations.city,locs.city) = ?");
    paramQueryMap.put(NAME, "groups.name = ?");
  }

  public GroupSearchParams(LinkedHashMap<String, String> params) throws SearchParameterException {

    this.logger = LogUtils.getLogger();
    this.params = new LinkedHashMap<String, String>();

    params.keySet().forEach(param->{
      if(param.equals(DAY_OF_WEEK)){

        try {
          SearchParameterValidator.validateDay(params.get(param));
        } catch (SearchParameterException e) {
          throw new RuntimeException(e);
        }

        this.params.put(param, params.get(param).toLowerCase());
      } else if(param.equals(CITY)) {
        this.params.put(param, params.get(param));
      } else if (param.equals(AREA)) {
        locationGroupFilter = params.get(param);
      } else if (param.equals(NAME)){
        this.params.put(param, params.get(param).replace("_", " "));
      }else {
        if (!param.equals(DISTANCE)) {
          logger.warn("Invalid parameter " + param + " submitted. It will not be used in the search query");

        }
      }


    });
  }

  public PreparedStatement generateSearchQuery(Connection conn, boolean isHomepage) throws Exception {
    String query = isHomepage ? getQueryForHomepageSearchResult() : getQueryForAllResultsWithDetails();

    ArrayList<String> whereClauses = new ArrayList<>();

    for(String param: params.keySet()){
      whereClauses.add(paramQueryMap.get(param));
    }

    if (!whereClauses.isEmpty()) {
      query = query + " WHERE ";
      query = query + String.join( " AND ", whereClauses.toArray(new String[0]));

      PreparedStatement select = conn.prepareStatement(query);
      int i = 1;
      for(String param: params.keySet()){
        select.setString(i, params.get(param));
        i++;
      }

      return select;

    } else {
      return conn.prepareStatement(query);
    }
  }

  private static String getQueryForHomepageSearchResult(){
    String query = """
      SELECT
        DISTINCT ON (events.id, groups.id, groups.name)
          groups.id as groupId,
          groups.name,
          groups.url,
          groups.description,
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
    return query;
  }

  private static String getQueryForAllResultsWithDetails() {
      String query = """
           SELECT
                   DISTINCT ON (events.id, groups.id, groups.name)
                    events.id as eventId,
                    groups.id as groupId,
                    groups.name,
                    groups.url,
                    groups.description,
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

    return query;
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

    String day = ctx.queryParam(GroupSearchParams.DAY_OF_WEEK);
    if(day != null && !day.isEmpty()){
      paramMap.put(GroupSearchParams.DAY_OF_WEEK, day);
    }

    String location = ctx.queryParam(GroupSearchParams.CITY);
    if(location != null && !location.isEmpty()){
      paramMap.put(GroupSearchParams.CITY, location);
    }

    String area = ctx.queryParam(GroupSearchParams.AREA);
    if(area != null && !area.isEmpty()){
      paramMap.put(GroupSearchParams.AREA, area);
    }

    String name = ctx.queryParam(GroupSearchParams.NAME);
    if(name != null && !name.isEmpty()){
      paramMap.put(GroupSearchParams.NAME, name);
    }

    String distance = ctx.queryParam(GroupSearchParams.DISTANCE);
    if(distance != null && !distance.isEmpty()){
      paramMap.put(GroupSearchParams.DISTANCE, distance);
    }

    return paramMap;
  }
}


