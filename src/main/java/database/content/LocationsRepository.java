package database.content;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import app.groups.data.EventLocation;
import app.result.error.StackTraceShortener;
import database.search.SameLocationData;
import org.apache.logging.log4j.Logger;
import service.data.SearchParameterException;
import service.data.SearchParameterValidator;
import utils.LogUtils;

public class LocationsRepository {

  Logger logger;
  Connection conn;
  public LocationsRepository(Connection conn){
    this.conn = conn;
    logger = LogUtils.getLogger();
  }

  /*
    Retrieves a location id for the city. The city will be saved in the database if it is not already there.
     */
  public int getLocationIdForCity(String city)
    throws Exception {

    city = SameLocationData.getDatabaseCityName(city);

    String query = "SELECT * from locations where city = ? AND state IS null AND street_address IS null AND zip_code IS null";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, city);
    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      return rs.getInt(1);
    }

    String query2 = "INSERT INTO locations (city) VALUES(?) returning id";
    PreparedStatement insert = conn.prepareStatement(query2);
    insert.setString(1, city);
    ResultSet insertRs = insert.executeQuery();
    if (insertRs.next()) {
      return insertRs.getInt(1);
    }
    logger.error("Insert did not succeed");
    throw new Exception();
  }

  public int insertLocation(String address) throws Exception{
    String[] data = address.split(",");


    if (!SearchParameterValidator.isValidAddress(address)) {
      logger.error("Invalid address:" + address);
      throw new SearchParameterException("Invalid address");
    }

    EventLocation location = new EventLocation();
    location.setStreetAddress(data[0].trim());
    location.setCity(data[1].trim());
    location.setState(data[2].trim().split(" ")[0]);
    location.setZipCode(Integer.parseInt(data[2].trim().split(" ")[1]));

    return insertLocation(location);
  }

  public int insertLocation(EventLocation location) throws Exception {

    int locationId = getLocation(location, conn);
    if (locationId == -1) {
      String query =
        "INSERT INTO locations (city,state, street_address,zip_code) VALUES(?, ?, ?, ?) returning id";
      PreparedStatement insert = conn.prepareStatement(query);
      insert.setString(1, location.getCity());
      insert.setString(2, location.getState());
      insert.setString(3, location.getStreetAddress());
      insert.setString(4, ""+location.getZipCode());
      ResultSet rs = insert.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);

      }
      logger.error("Insert did not succeed");
      throw new Exception();
    }
    return locationId;
  }

  public int getLocation(
    EventLocation location,
    Connection conn
  )
    throws Exception
  {

    var city = SameLocationData.getDatabaseCityName(location.getCity());
    String query =
      "SELECT * from locations where city = ? and state = ? and street_address=? and zip_code=?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, city);
    select.setString(2, location.getState());
    select.setString(3, location.getStreetAddress());
    select.setString(4, ""+location.getZipCode());

    ResultSet rs = select.executeQuery();
    if (rs.next()) {
      return rs.getInt(1);
    }
    return -1;
  }

  public ArrayList<String> listALlLocationCities(String location, Connection conn) throws Exception {


    ResultSet rs;

    if(location != null) {
      String query = """
          SELECT city, name from locations
          LEFT JOIN location_tag_mapping on locations.id = location_tag_mapping.location_id
          LEFT JOIN location_tag on location_tag.id = location_tag_mapping.location_tag_id
          WHERE name = ?
          """;
      PreparedStatement select = conn.prepareStatement(query);
      select.setString(1, location.toLowerCase());
      rs = select.executeQuery();
    }
    else{
      String query = "SELECT DISTINCT city from locations";
      PreparedStatement select = conn.prepareStatement(query);
      rs = select.executeQuery();
    }

    ArrayList<String> data = new ArrayList<>();
    while(rs.next()){
      data.add(rs.getString("city"));
    }
    return data;
  }

  public int countLocations()throws Exception{

    try {
      String query = "SELECT COUNT(*) from locations";
      PreparedStatement select = conn.prepareStatement(query);

      ResultSet rs = select.executeQuery();
      rs.next();
      return rs.getInt(1);
    } catch (Exception e){
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw(e);
    }

  }
}
