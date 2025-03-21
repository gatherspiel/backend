package service;

import app.data.Data;

public class BulkUpdateService {
    public void bulkUpdate(Data data) {
        System.out.println(data.getGroups());
        System.out.println(data.getConventions());
        System.out.println(data.getGameStores());
        System.out.println(data.getGameRestaurants());
    }
}
