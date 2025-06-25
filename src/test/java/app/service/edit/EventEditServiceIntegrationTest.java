package app.service.edit;

import app.data.Event;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.Group;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.EventEditService;
import service.user.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EventEditServiceIntegrationTest {

  private static final String ADMIN_USERNAME = "unitTest";

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static EventEditService eventEditService;
  private static Connection conn;

  private static User adminUser;
  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {

      conn = testConnectionProvider.getDatabaseConnection();
      eventEditService = new EventEditService();
      DbUtils.createTables(conn);


      UserService createUserService
          = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()));

      admin = createUserService.createAdmin(ADMIN_USERNAME);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testCreateOneEvent() {
    eventEditService.addEvent(new Event(), 1, adminUser);
    //TODO: Update test

  }

  //TODO: Update tests
  @Test
  public void testCreateMultipleEvents(){

  }

  @Test
  public void testCreateMultipleEventsAndFirstOneIsChanged(){

  }

  @Test
  public void testCreateMultipleEvents_GroupPageData_HasCorrectEvents() throws Exception {

    eventEditService.addEvent(new Event(), 1, adminUser);
    eventEditService.addEvent(new Event(), 1, adminUser);
    eventEditService.addEvent(new Event(), 1, adminUser);

    
    ReadGroupService readGroupService = new ReadGroupService(ReadGroupDataProvider.create());
    Group group = readGroupService.getGroup(1, testConnectionProvider).get();

    assertEquals(group.events.length, 3);

    Set<Integer> ids = new HashSet<>();
    for(int i =0; i<group.events.length; i++){
      ids.add(group.events[i].getId());
    }

    var expected = new HashSet<>(Arrays.asList(1,2,3));
    assertEquals(ids, expected);



  }
  @Test
  public void testGroupAdmin_CanEditEvent_ForTheirGroup(){

  }

  @Test
  public void testGroupAdmin_CannotEditEvent_ForOtherGroup(){

  }

  @Test
  public void testStandardUser_CannotEditEvent() {

  }

  @Test
  public void testGroupAdmin_CanDeleteEvent_ForTheirGroup(){

  }

  @Test
  public void testGroupAdmin_CannotDeleteEvent_ForOtherGroup(){

  }

  @Test
  public void testStandardUser_CannotDeleteEvent() {

  }


  @AfterEach
  public void deleteAllEvents() throws Exception{
    String deleteEventQuery =
        "TRUNCATE TABLE events";
    String deleteEventGroupMapQuery =
        "TRUNCATE TABLE events";

    PreparedStatement query1 = conn.prepareStatement(deleteEventQuery);
    PreparedStatement query2 = conn.prepareStatement(deleteEventGroupMapQuery);

    query1.execute();
    query2.execute();
  }




}
