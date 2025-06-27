package service.read;

import app.groups.data.Group;
import app.users.data.PermissionName;
import app.users.data.User;
import app.groups.data.GroupPageData;
import database.content.GroupsRepository;
import database.utils.ConnectionProvider;
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

  public ReadGroupService(ReadGroupDataProvider dataProvider, Connection connection) {
    this.searchService = dataProvider.getSearchService();
    this.groupPermissionService = dataProvider.getGroupPermissionService();
    this.groupsRepository = dataProvider.getGroupsRepository();
    this.connection = connection;
  }

  public Optional<Group> getGroup(int groupId) throws Exception{
    return this.groupsRepository.getGroup(groupId);
  }
  public GroupPageData getGroupPageData(
      User currentUser,
      LinkedHashMap<String, String> params,
      ConnectionProvider connectionProvider) throws Exception{

    Group group = searchService.getSingleGroup(params, connectionProvider);
    if(group == null){
      throw new SearchParameterException("No group found with parameters:"+params);
    }
    GroupPageData groupPageData = GroupPageData.createFromSearchResult(group);

    boolean canEdit = groupPermissionService.canEditGroup(currentUser, groupPageData.getId());
    groupPageData.enablePermission(PermissionName.USER_CAN_EDIT.toString(), canEdit);
    return groupPageData;
  }
}
