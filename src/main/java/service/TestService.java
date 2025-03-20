package service;

import database.TestRepository;

//Utility service for running manual checks to verify deployments
public class TestService {
    public int countLocations() {
        TestRepository repository = new TestRepository();

        return repository.countLocations();
    }
}
