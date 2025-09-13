package service.read;

import app.groups.data.Group;
import app.users.data.PermissionName;
import app.users.data.User;
import app.result.group.GroupPageData;
import database.content.GroupsRepository;
import service.data.SearchParameterException;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;

import java.sql.Connection;
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

    boolean canEdit = groupPermissionService.canEditGroup(groupPageData.getId());
    groupPageData.enablePermission(PermissionName.USER_CAN_EDIT, canEdit);
    return groupPageData;
  }
}
