package service.read;

import app.result.GroupSearchResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import database.utils.ConnectionProvider;
import service.ContentItemService;

import java.sql.Connection;
import java.util.LinkedHashMap;

public class SearchService extends ContentItemService {


  //TODO: Add group edit permissions based on email
  public GroupSearchResult getGroups(
    LinkedHashMap<String, String> searchParams,
    ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    Connection conn = connectionProvider.getDatabaseConnection();

    SearchRepository searchRepository = new SearchRepository();
    return searchRepository.getGroups(params, conn);
  }


}
