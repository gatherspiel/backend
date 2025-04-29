package database.search;

import io.javalin.http.Context;
import org.apache.logging.log4j.Logger;
import service.SearchParameterException;
import service.SearchParameterValidator;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GroupSearchParams {
  public static final String DAY_OF_WEEK = "day";
  public static final String CITY = "city";

  private final LinkedHashMap<String, String> params;
  private static final HashMap<String, String> paramQueryMap;

  private static final String SORT_ORDER = " ORDER BY groups.name, groups.id, events.id ASC ";
  private Logger logger;

  static {
    paramQueryMap = new HashMap<String,String>();
    paramQueryMap.put(DAY_OF_WEEK,"day_of_week = cast(? AS dayofweek)");
    paramQueryMap.put(CITY, "COALESCE(locations.city,locs.city) = ?");
  }

  public GroupSearchParams(LinkedHashMap<String, String> params) throws SearchParameterException {

    this.logger = LogUtils.getLogger();
    this.params = new LinkedHashMap<String, String>();
    params.keySet().forEach(param->{
      if(param == DAY_OF_WEEK){

        try {
          SearchParameterValidator.validateDay(params.get(param));
        } catch (SearchParameterException e) {
          throw new RuntimeException(e);
        }

        this.params.put(param, params.get(param).toLowerCase());
      } else if(param == CITY) {
        this.params.put(param, params.get(param));
      } else {
        logger.warn("Invalid parameter submitted. It will not be used in the search query");
      }
    });
  }

  public PreparedStatement generateSearchQuery(Connection conn) throws Exception {
    String query = getQueryForAllResults();

    ArrayList<String> whereClauses = new ArrayList<>();

    for(String param: params.keySet()){
      whereClauses.add(paramQueryMap.get(param));
    }

    if (!whereClauses.isEmpty()) {
      query = query + " WHERE ";
      query = query + String.join( " AND ", whereClauses.toArray(new String[0]));
      query = query + SORT_ORDER;
      PreparedStatement select = conn.prepareStatement(query);
      int i = 1;
      for(String param: params.keySet()){
        select.setString(i, params.get(param));
        i++;
      }
      return select;

    } else {
      query = query + SORT_ORDER;
      return conn.prepareStatement(query);
    }
  }

  private static String getQueryForAllResults() {
      String query = """
           SELECT
                   DISTINCT ON (events.id, groups.id, groups.name)
                    events.id as eventId,
                    groups.id as groupId,
                    groups.name,
                    groups.url,
                    groups.summary,
                    events.name as eventName,
                    events.description,
                    event_time.day_of_week,
                    locations.state,
                    locations.street_address,
                    locations.zip_code,
                    locations.city as city,
                    locs.city as groupCity
                  FROM groups
                  LEFT JOIN event_group_map on groups.id = event_group_map.group_id
                  LEFT JOIN  events on event_group_map.event_id = events.id
                  LEFT JOIN  event_time on event_time.event_id = events.id
                  LEFT JOIN  locations on events.location_id = locations.id
                  LEFT JOIN location_group_map on groups.id = location_group_map.group_id
                  LEFT JOIN locations as locs on location_group_map.location_id = locs.id
          
        """;
    return query;
  }

  public static LinkedHashMap<String, String> generateParameterMapFromQueryString(Context ctx) {
    LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();

    String day = ctx.queryParam(GroupSearchParams.DAY_OF_WEEK);
    if(day != null && !day.isEmpty()){
      paramMap.put(GroupSearchParams.DAY_OF_WEEK, day);
    }

    String location = ctx.queryParam(GroupSearchParams.CITY);
    if(location!=null && !location.isEmpty()){
      paramMap.put(GroupSearchParams.CITY, location);
    }
    return paramMap;
  }
}


