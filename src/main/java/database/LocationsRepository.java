package database;

import app.data.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LocationsRepository {
    public int getLocationIdForCity (String location, Connection conn) throws Exception{

        String query = "SELECT id FROM locations where city = ? AND state IS NULL AND street_address IS NULL AND zip_code IS NULL";
        PreparedStatement select = conn.prepareStatement(query);
        select.setString(1, location);
        ResultSet rs = select.executeQuery();

        if(rs.next()) {
            return rs.getInt(1);
        }

        String insertQuery = "INSERT INTO locations (city) VALUES(?) RETURNING id";
        PreparedStatement insert = conn.prepareStatement(insertQuery);
        insert.setString(1, location);

        ResultSet insertRs = insert.executeQuery();
        return rs.getInt(1);
    }

    public int insertLocation(String address, Connection conn) throws Exception{


        String[] data = address.split(",");
        if(data.length !=3){
            System.out.println("Invalid address:"+address);
            throw new Exception();
        }

        String streetAddress = data[0];
        String city = data[1];
        String state = data[2].split(" ")[0];
        String zipCode = data[2].split(" ")[1];



        String query = "INSERT INTO locations (city,state, street_address,zip_code) VALUES(?, ?, ?, ?) returning id";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.setString(1, city);
        insert.setString(2, state);
        insert.setString(3, streetAddress);
        insert.setString(4, zipCode);
        ResultSet rs = insert.executeQuery();
        rs.next();
        return rs.getInt(1);
    }
}
