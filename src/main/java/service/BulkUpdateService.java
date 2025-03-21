package service;

import app.data.Data;
import app.data.Group;
import database.GroupsRepository;

public class BulkUpdateService {
    public void bulkUpdate(Data data) throws Exception{
        System.out.println(data.getGroups());
        System.out.println(data.getConventions());
        System.out.println(data.getGameStores());
        System.out.println(data.getGameRestaurants());


        GroupsRepository groupsRepository = new GroupsRepository();
        groupsRepository.insertGroups(data.getGroups());
    }
}
