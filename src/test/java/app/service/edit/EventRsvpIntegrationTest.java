package app.service.edit;

import org.junit.jupiter.api.Test;

public class EventRsvpIntegrationTest {

  //TOOD: Make sure tests cover adding and removing RSVP
  @Test
  public void testRsvpToValidEvent_loggedInUser(){

  }

  @Test
  public void testRsvpToTwoEventsInSameGroup(){

  }

  @Test
  public void testRsvpToOneEvent_InGroupThatHasTwoEvents_rsvpIsForCorrectEvent(){

  }

  @Test
  public void testTwoGroupsWithOneEventEach_rsvpToOneEvent_rsvpIsForCorrectEvent(){

  }

  @Test
  public void testTwoUsersRsvpToEvent_bothRemoveRsvp(){

  }

  @Test
  public void testTwoUsersRsvpToEvent_oneRemovesRsvp(){

  }

  @Test
  public void testGroupWithTwoEvents_oneUserRsvpsToEachEvent_eachEventHasOneRsvp(){

  }
  
  @Test
  public void testRsvpToEventTwice_badRequestErrorOnSecondAttempt(){

  }

  @Test
  public void testRemoveRsvpToEventTwice_badRequestErrorOnSecondAttempt(){

  }

  @Test
  public void testRsvpToTwoEventsInSameGroup_AndRemoveRsvpForOneEvent() {

  }

  @Test
  public void testRsvpToValidEvent_userIsNotLoggedIn_authorizationError(){

  }

  @Test
  public void testRsvpWithInvalidEventId_userIsLoggedIn_badRequestError(){

  }

  @Test
  public void testRemoveRsvpToValidEvent_userIsNotLoggedIn_authorizationError(){

  }

  @Test
  public void testRemoveRsvpWithInvalidEventId_userIsLoggedIn_badRequestError(){

  }



}
