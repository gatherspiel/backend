package service.provider;

import app.data.auth.User;
import database.utils.ConnectionProvider;
import service.permissions.GroupPermissionService;
import service.read.SearchService;

public class ReadGroupDataProvider extends DataProvider {

  protected SearchService searchService;
  protected GroupPermissionService groupPermissionService;

  private ReadGroupDataProvider(
      User currentUser,
      ConnectionProvider connectionProvider,
      SearchService searchService,
      GroupPermissionService groupPermissionService){
    super(currentUser, connectionProvider);
    this.searchService = searchService;
    this.groupPermissionService = groupPermissionService;
  }

  public SearchService getSearchService(){
    return searchService;
  }

  public GroupPermissionService getGroupPermissionService(){
    return groupPermissionService;
  }

  public static ReadGroupDataProvider create(User currentUser,
                       ConnectionProvider connectionProvider){

    var searchService = new SearchService(currentUser);
    var groupPermissionService = new GroupPermissionService();
    return new ReadGroupDataProvider(currentUser, connectionProvider, searchService,groupPermissionService);
  }
}
