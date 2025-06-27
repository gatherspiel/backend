package service.provider;

import database.content.GroupsRepository;
import service.permissions.GroupPermissionService;
import service.read.SearchService;

import java.sql.Connection;

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

  public static ReadGroupDataProvider create(Connection conn){

    var searchService = new SearchService(conn);
    var groupPermissionService = new GroupPermissionService(conn);
    var groupRepository = new GroupsRepository(conn);
    return new ReadGroupDataProvider(searchService,groupPermissionService, groupRepository);
  }
}
