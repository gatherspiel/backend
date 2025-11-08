package service.read;

import app.groups.Event;
import app.groups.Group;
import app.users.PermissionName;
import app.users.User;
import app.result.group.GroupPageData;
import database.content.EventRepository;
import database.content.GroupsRepository;
import app.result.error.SearchParameterException;
import service.update.GroupEventRsvpData;
import service.update.GroupPermissionService;

import java.sql.Connection;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Optional;

public class ReadGroupService{

  SearchService searchService;
  GroupPermissionService groupPermissionService;
  GroupsRepository groupsRepository;
  Connection connection;
  User user;

  public ReadGroupService(ReadGroupDataProvider dataProvider, Connection connection) {
    this.searchService = dataProvider.getSearchService();
    this.groupPermissionService = dataProvider.getGroupPermissionService();
    this.groupsRepository = dataProvider.getGroupsRepository();
    this.connection = connection;
    this.user = dataProvider.getUser();
  }

  public Optional<Group> getGroupWithOneTimeEvents(int groupId) throws Exception{
    return this.groupsRepository.getGroupWithOneTimeEvents(groupId);
  }

  public GroupPageData getGroupPageData(
      LinkedHashMap<String, String> params
      ) throws Exception{

    Optional<Group> group = searchService.getSingleGroup(params);
    if(group.isEmpty()){
      throw new SearchParameterException("No group found with parameters:"+params);
    }
    GroupPageData groupPageData = GroupPageData.createFromSearchResult(group.get());

    GroupEventRsvpData rsvpData =
        new EventRepository(connection).getEventRsvpsForGroup(groupPageData.getId(), user);

    for(Event event: groupPageData.getWeeklyEventData()){
      AbstractMap.SimpleEntry eventRsvpData = rsvpData.rsvpData.get(event.getId());
      if(eventRsvpData != null){
        event.setUserHasRsvp((Boolean)eventRsvpData.getValue());
        event.setRsvpCount((Integer)eventRsvpData.getKey());
        event.setUserCanUpdateRsvp(rsvpData.canRsvpToEvent(event.getId()));
      }
      event.setModerators(rsvpData.getModerators());
      if(!user.isLoggedInUser()){
        event.setUserCanUpdateRsvp(false);
      }
    }
    boolean canEdit = groupPermissionService.canEditGroup(groupPageData.getId());
    groupPageData.enablePermission(PermissionName.USER_CAN_EDIT, canEdit);
    return groupPageData;
  }
}
