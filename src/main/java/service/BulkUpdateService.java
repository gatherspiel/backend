package service;

import app.data.Data;
import database.*;
import database.utils.RepositoryUtils;

import java.sql.Connection;

public class BulkUpdateService {
    public void bulkUpdate(Data data) throws Exception{


        System.out.println(data.getGroups());
        System.out.println(data.getConventions());
        System.out.println(data.getGameStores());
        System.out.println(data.getGameRestaurants());

        Connection conn =  RepositoryUtils.getDatabaseConnection();
        conn.setAutoCommit(false);

        try {
            GroupsRepository groupsRepository = new GroupsRepository();
            groupsRepository.insertGroups(data.getGroups(), conn);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting groups");
            System.exit(1);

        }


        try {
            ConventionsRepository conventionsRepository = new ConventionsRepository();
            conventionsRepository.insertConventions(data.getConventions(), conn);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting conventions");
            System.exit(1);

        }


        try {
            GameStoreRepository gameStoreRepository = new GameStoreRepository();
            gameStoreRepository.insertGameStores(data.getGameStores(), conn);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting game stores");
            System.exit(1);

        }

        try {
            GameRestaurantRepository gameRestaurantRepository = new GameRestaurantRepository();
            gameRestaurantRepository.insertGameRestaurants(data.getGameRestaurants(), conn);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting game restaurants");
            System.exit(1);

        }

        try {
            EventRepository eventRepository = new EventRepository();
            eventRepository.addEvents(data.getGroups(), conn);
            System.out.println("Inserted events");
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting events");
            System.exit(1);

        }


        conn.commit();
        conn.close();

        System.out.println("Done");
        /*
        -Update locations group_map table
        -Update convention times
        -Update event times
         */

    }
}
