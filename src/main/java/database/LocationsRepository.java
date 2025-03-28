package database;

import app.data.Group;
import service.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LocationsRepository {


    public int insertLocation(String address, Connection conn) throws Exception{

        if(!Validator.isValidAddress(address)){
            System.out.println("Invalid address:"+address);
            throw new Exception();
        }

        String[] data = address.split(",");

        String streetAddress = data[0].trim();
        String city = data[1].trim();
        String state = data[2].trim().split(" ")[0];
        String zipCode = data[2].trim().split(" ")[1];

        if(state == null || state.length()==0){
            System.out.println("Inserting:"+address);
        }

        int locationId = getLocation(streetAddress, city, state, zipCode, conn);
        if(locationId == -1){
            String query = "INSERT INTO locations (city,state, street_address,zip_code) VALUES(?, ?, ?, ?) returning id";
            PreparedStatement insert = conn.prepareStatement(query);
            insert.setString(1, city);
            insert.setString(2, state);
            insert.setString(3, streetAddress);
            insert.setString(4, zipCode);
            ResultSet rs = insert.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            System.out.println("Insert did not succeed");
            throw new Exception();
        }
        return locationId;

    }

    public int getLocation(String streetAddress, String city, String state, String zipCode, Connection conn) throws Exception {
        String query = "SELECT * from locations where city = ? and state = ? and street_address=? and zip_code=?";
        PreparedStatement select = conn.prepareStatement(query);
        select.setString(1, city);
        select.setString(2, state);
        select.setString(3, streetAddress);
        select.setString(4, zipCode);

        ResultSet rs = select.executeQuery();
        if(rs.next()){
            return rs.getInt(1);
        }
        return -1;

    }
}
