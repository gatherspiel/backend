package service.read;

import app.data.Group;
import app.data.auth.PermissionName;
import app.data.auth.User;
import app.result.groupPage.GroupPageData;
import database.utils.ConnectionProvider;
import service.ContentService;
import service.data.SearchParameterException;
import service.permissions.GroupPermissionService;

import java.util.LinkedHashMap;

public class ReadGroupService extends ContentService {

  SearchService searchService;
  GroupPermissionService groupPermissionService;

  public ReadGroupService(SearchService searchService, User currentUser, GroupPermissionService groupPermissionService){
    super(currentUser);
    this.searchService = searchService;
    this.groupPermissionService = groupPermissionService;
  }

  public GroupPageData getGroupPageData(
      LinkedHashMap<String, String> params,
      ConnectionProvider connectionProvider) throws Exception{

    Group group = searchService.getSingleGroup(params, connectionProvider);
    if(group == null){
      throw new SearchParameterException("No group found");
    }
    GroupPageData groupPageData = GroupPageData.createFromSearchResult(group);

    boolean canEdit = groupPermissionService.canEditGroup(currentUser, groupPageData.getId(),connectionProvider);
    groupPageData.enablePermission(PermissionName.USER_CAN_EDIT.toString(), canEdit);
    return groupPageData;
  }
}
