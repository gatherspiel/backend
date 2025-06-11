package service.read;

import app.data.Group;
import app.data.auth.PermissionName;
import app.data.auth.User;
import app.result.GroupSearchResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import database.utils.ConnectionProvider;
import service.ContentService;
import service.permissions.GroupPermissionService;

import java.sql.Connection;
import java.util.LinkedHashMap;

public class SearchService {

  public SearchService(){
  }

  public GroupSearchResult getGroups(
    LinkedHashMap<String, String> searchParams,
    ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    Connection conn = connectionProvider.getDatabaseConnection();

    SearchRepository searchRepository = new SearchRepository();


    GroupSearchResult groups = searchRepository.getGroups(params, conn);
    return groups;
  }


  public Group getSingleGroup(
      LinkedHashMap<String, String> searchParams,
      ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    Connection conn = connectionProvider.getDatabaseConnection();
    SearchRepository searchRepository = new SearchRepository();

    GroupSearchResult groups = searchRepository.getGroups(params, conn);
    if(groups.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }
    return groups.getFirstGroup();
  }
}
