package app.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.read.DistanceService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DistanceServiceTest {

  @BeforeAll
  static void setup(){
    DistanceService.loadData();
  }

  @Test
  public void testDistanceCalculationNearbyLocations(){
    Optional<Double> distance = DistanceService.getDistance("Fairfax","Burke");
    assertTrue(distance.isPresent());
    assertAll(
        ()->assertTrue(distance.get()<10, ""+distance.get()),
        ()->assertTrue(distance.get()>0, ""+distance.get())
    );
  }


  @Test
  public void testDistanceCalculations_LocationsInDifferentStates(){
    Optional<Double> distance = DistanceService.getDistance("Rockville","Burke");
    assertTrue(distance.isPresent());
    assertAll(
        ()->assertTrue(distance.get()<25, ""+distance.get()),
        ()->assertTrue(distance.get()>20, ""+distance.get())
    );
  }

  @Test
  public void testDistanceCalculations_SameCity(){
    Optional<Double> distance = DistanceService.getDistance("Rockville","Rockville");
    assertTrue(distance.isPresent());
    assertAll(
      ()->assertEquals(0, distance.get(), ""+distance.get())
    );
  }
}
