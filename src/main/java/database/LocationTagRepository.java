package database;

import app.data.LocationTag;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LocationTagRepository {

  Logger logger;

  public LocationTagRepository(){
    logger = LogUtils.getLogger();
  }

  public void insertLocationTags(LocationTag[] locationTags, Connection conn) throws Exception{
    for(LocationTag locationTag: locationTags) {

      for(String city: locationTag.getLocations()){
        insertLocationTag(city, locationTag.getName(), conn);

      }
    }
  }

  public void insertLocationTag(String city, String locationTag, Connection conn) throws Exception {
    LocationsRepository locationsRepository = new LocationsRepository();

    int locationId = locationsRepository.getLocationIdForCity(city, conn);

    //TODO: After creating unique index, use ON CONFLICT clause instead of a separate query.

    Integer locationTagId = getLocationTagId(locationTag, conn);

    if(locationTagId == null){
      String locationTagQuery = "INSERT into location_tag(name) VALUES(?) returning id";
      PreparedStatement locationTagInsert = conn.prepareStatement(locationTagQuery);
      locationTagInsert.setString(1, locationTag);

      ResultSet rs = locationTagInsert.executeQuery();
      if (rs.next()){
        locationTagId = rs.getInt(1);
      } else {
        logger.error("Insert did not succeed");
        throw new Exception();
      }
    }

    String mappingQuery = "INSERT into location_tag_mapping(location_tag_id, location_id) VALUES(?, ?)";
    PreparedStatement mappingInsert = conn.prepareStatement(mappingQuery);

    mappingInsert.setInt(1, locationTagId);
    mappingInsert.setInt(2, locationId);
    mappingInsert.execute();
  }

  public Integer getLocationTagId(String locationTag, Connection conn) throws Exception{

    String query = "SELECT * from location_tag where name = ?";
    PreparedStatement statement = conn.prepareStatement(query);

    statement.setString(1, locationTag);

    ResultSet rs = statement.executeQuery();
    if(rs.next()){
      return rs.getInt(1);
    }
    return null;
  }
}
