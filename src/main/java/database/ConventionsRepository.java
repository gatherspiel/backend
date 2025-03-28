package database;

import app.data.Convention;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ConventionsRepository {
    public void insertConventions(Convention[] conventions, Connection conn) throws Exception{

        EventRepository eventRepository = new EventRepository();
        EventTimeRepository eventTimeRepository = new EventTimeRepository();
        for (Convention convention: conventions) {
            System.out.println(convention.getTitle());

            if (!eventRepository.hasEvent(convention.getTitle(), convention.getLink(), conn)) {
                String query = "INSERT INTO events (url, name, is_convention) VALUES(?, ?, true) returning id";
                PreparedStatement insert = conn.prepareStatement(query);
                insert.clearBatch();
                insert.setString(1, convention.getLink());
                insert.setString(2, convention.getTitle());
                ResultSet rs = insert.executeQuery();
                rs.next();
                convention.setId(rs.getInt(1));
            }

            // Date values should be formated similar to the following date: 3/22/2025
            for(String date: convention.getDays()){
                String[] data = date.split("/");

                LocalDate localDate = LocalDate.of(
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[0]),
                        Integer.parseInt(data[1]));
                eventTimeRepository.setEventDate(convention.getId(), localDate, conn);

            }
        }
    }
}
