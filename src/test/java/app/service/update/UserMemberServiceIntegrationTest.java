package app.service.update;

import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.groups.data.Group;
import app.users.data.SessionContext;
import app.users.data.UserGroupMemberData;
import app.utils.CreateUserUtils;
import database.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.update.UserMemberService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.TreeSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class UserMemberServiceIntegrationTest {

  private static IntegrationTestConnectionProvider testConnectionProvider;
  private static Connection conn;

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String STANDARD_USER_USERNAME = "testUser";

  private static final TreeSet<Group> EMPTY_GROUP_SET = new TreeSet<>();
  private static final TreeSet<Event> EMPTY_EVENT_SET = new TreeSet<>();

  private static SessionContext adminContext;
  private static SessionContext readOnlyUserContext;
  private static SessionContext standardUserContext;

  private static UserRepository userRepository;

  @BeforeAll
  static void setup(){
    testConnectionProvider = new IntegrationTestConnectionProvider();

    try {
      conn = testConnectionProvider.getDatabaseConnection();
      DbUtils.createTables(conn);

      adminContext = CreateUserUtils.createContextWithNewAdminUser(
          ADMIN_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);
      readOnlyUserContext = CreateUserUtils.createContextWithNewReadonlyUser(
          STANDARD_USER_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);
      standardUserContext = CreateUserUtils.createContextWithNewStandardUser(
          STANDARD_USER_USERNAME+ UUID.randomUUID(),
          testConnectionProvider);

      userRepository = new UserRepository(conn);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing data:" + e.getMessage());
    }
  }

  @Test
  public void testNewUser_isNotPartOfAnyGroups() throws Exception{
    SessionContext newUserContext = CreateUserUtils.createContextWithNewStandardUser(
        STANDARD_USER_USERNAME + UUID.randomUUID(),
        testConnectionProvider);

    UserMemberService userMemberService = newUserContext.createUserMemberService();

    UserGroupMemberData memberData = userMemberService.getUserGroupMemberData();

    assertIterableEquals(EMPTY_GROUP_SET, memberData.getJoinedGroups());
    assertIterableEquals(EMPTY_GROUP_SET, memberData.getModeratingGroups());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getAttendingEvents());
    assertIterableEquals(EMPTY_EVENT_SET, memberData.getHostingEvents());
  }

  @Test
  public void testNewAdmin_IsNotPartOfAnyGroupsAsModeratorOrAdmin(){

  }

  @Test
  public void testNewAdminJoinsGroup_IsNotAGroupModerator(){

  }

  @Test
  public void testStandardUserRsvpToEvent_nextEventTimeIsDisplayed(){

  }

  @Test
  public void testStandardUserRsvpData_doesNotShowPastEventRsvp(){

  }

  @Test
  public void testEventModeratorAddedBeforeLastEvent_ModeratorDataShowsNextEventDate(){

  }

  @Test
  public void test_groupAndEventWithModerator_AndEventRsvp_otherUsersAreNotMembers(){

  }

  @Test
  public void testUserCanJoinAndLeaveGroup_correctUserGroupDataIsVisible(){

  }

  @Test
  public void testGroupCreator_automaticallyIsShownAsGroupModerator_withoutJoiningGroup(){

  }

  @Test
  public void testGroupModerator_leavesGroup_isNoLongerGroupModerator(){

  }


  @Test
  public void testUserIsGroupModerator_andEventModerator_userIsNotShownAsStandardMember(){

  }

  @Test
  public void testUserIsGroupMember_isModeratorForOneEvent_andRsvpForOtherEvent(){

  }

  @Test
  public void testUserJoinsMultipleGroups_groupsAreInAlphabeticalOrder(){

  }

  @Test
  public void testUserIsModeratorForMultipleGroups_groupsAreInAlphabeticalOrder(){

  }

  @Test
  public void testUserIsModeratorForMultipleEvents_moderatorInformationIsInAlphabeticalOrder(){

  }

  @Test
  public void testUserHasRsvpToMultipleEvents_eventsAreInChronologicalOrder(){

  }

  @Test
  public void testUserAttemptsToJoinGroupTwice_secondAttemptIsError(){

  }

  @Test
  public void testUserAttemptsToLeaveGroupTheyAreNotAPartOf_IsError(){

  }

  @Test
  public void testNonLoggedInUserAttemptsToJoinGroup_unauthorizedError(){

  }

  @Test
  public void testNonLoggedInUserAttemptsToLeaveGroup_unauthorizedError(){

  }

  @Test
  public void testNonLoggedInUser_AttemptsToRetrieveUserData_unauthorizedError(){

  }

  @AfterEach
  public void cleanup() throws Exception{
    String deleteEventAdminQuery =
        "TRUNCATE TABLE event_admin_data CASCADE";
    String deleteGroupAdminQuery =
        "TRUNCATE TABLE group_admin_data CASCADE";

    PreparedStatement query1 = conn.prepareStatement(deleteEventAdminQuery);
    PreparedStatement query2 = conn.prepareStatement(deleteGroupAdminQuery);

    query1.execute();
    query2.execute();
  }
}
