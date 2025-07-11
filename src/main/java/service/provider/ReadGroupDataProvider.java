package service.provider;

import app.users.data.User;
import database.content.GroupsRepository;
import service.permissions.GroupPermissionService;
import service.read.SearchService;

import java.sql.Connection;

public class ReadGroupDataProvider {

  protected SearchService searchService;
  protected GroupPermissionService groupPermissionService;
  protected GroupsRepository groupsRepository;
  protected User user;
  private ReadGroupDataProvider(
      SearchService searchService,
      GroupPermissionService groupPermissionService,
      GroupsRepository groupsRepository,
      User user){
    this.searchService = searchService;
    this.groupPermissionService = groupPermissionService;
    this.groupsRepository = groupsRepository;
    this.user = user;
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

  public User getUser(){
    return user;
  }
  public static ReadGroupDataProvider create(Connection conn, User user){
    var searchService = new SearchService(conn);
    var groupPermissionService = new GroupPermissionService(conn, user);
    var groupRepository = new GroupsRepository(conn);
    return new ReadGroupDataProvider(searchService,groupPermissionService, groupRepository, user);
  }
}
