package service.read;

import app.groups.data.Event;
import app.groups.data.Group;
import app.users.data.PermissionName;
import app.users.data.User;
import app.result.group.GroupPageData;
import database.content.EventRepository;
import database.content.GroupsRepository;
import app.result.error.SearchParameterException;
import service.update.GroupPermissionService;

import java.sql.Connection;
import java.util.AbstractMap;
import java.util.HashMap;
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

    HashMap<Integer, AbstractMap.SimpleEntry<Integer,Boolean>> rsvpData =
        new EventRepository(connection).getEventRsvpsForGroup(groupPageData.getId(), user);

    for(Event event: groupPageData.getWeeklyEventData()){
      AbstractMap.SimpleEntry eventRsvpData = rsvpData.get(event.getId());
      event.setUserHasRsvp((Boolean)eventRsvpData.getValue());
      event.setRsvpCount((Integer)eventRsvpData.getKey());
    }
    boolean canEdit = groupPermissionService.canEditGroup(groupPageData.getId());
    groupPageData.enablePermission(PermissionName.USER_CAN_EDIT, canEdit);
    return groupPageData;
  }
}
