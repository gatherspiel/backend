package service;

import app.data.Data;
import app.data.Group;
import database.ConventionsRepository;
import database.GameStoreRepository;
import database.GroupsRepository;
import database.RepositoryUtils;

import java.sql.Connection;

public class BulkUpdateService {
    public void bulkUpdate(Data data) throws Exception{


        System.out.println(data.getGroups());
        System.out.println(data.getConventions());
        System.out.println(data.getGameStores());
        System.out.println(data.getGameRestaurants());

        Connection conn =  RepositoryUtils.getDatabaseConnection();
        conn.setAutoCommit(false);

        /*
        try {
            GroupsRepository groupsRepository = new GroupsRepository();
            groupsRepository.insertGroups(data.getGroups(), conn);
        } catch(Exception e){
            System.out.println("Error inserting groups");
        }


        try {
            ConventionsRepository conventionsRepository = new ConventionsRepository();
            conventionsRepository.insertConventions(data.getConventions(), conn);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting conventions");
        }
        */

        try {
            GameStoreRepository gameStoreRepository = new GameStoreRepository();
            gameStoreRepository.insertGameStores(data.getGameStores(), conn);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting game stores");
        }

        conn.commit();
        conn.close();

        /*

        -Make sure game restaurant data is saved
        -Add group event information.
        -Update locations group_map table
        -Add event_group_map table
        -Update convention days table
         */

    }
}
