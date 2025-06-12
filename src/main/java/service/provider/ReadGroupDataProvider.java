package service.provider;

import app.data.auth.User;
import database.content.GroupsRepository;
import database.utils.ConnectionProvider;
import service.permissions.GroupPermissionService;
import service.read.ReadGroupService;
import service.read.SearchService;

public class ReadGroupDataProvider {

  protected SearchService searchService;
  protected GroupPermissionService groupPermissionService;
  protected GroupsRepository groupsRepository;

  private ReadGroupDataProvider(
      SearchService searchService,
      GroupPermissionService groupPermissionService,
      GroupsRepository groupsRepository){
    this.searchService = searchService;
    this.groupPermissionService = groupPermissionService;
    this.groupsRepository = groupsRepository;
  }

  public GroupsRepository getGroupsRepository(){
    return groupsRepository;
  }

  public SearchService getSearchService(){
    return searchService;
  }

  public GroupPermissionService getGroupPermissionService(){
    return groupPermissionService;
  }

  public static ReadGroupDataProvider create(){

    var searchService = new SearchService();
    var groupPermissionService = new GroupPermissionService();
    var groupRepository = new GroupsRepository();
    return new ReadGroupDataProvider(searchService,groupPermissionService, groupRepository);
  }
}
