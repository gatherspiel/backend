package service.read;

import app.groups.data.Group;
import app.result.GroupSearchResult;
import app.result.HomeResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;

import java.sql.Connection;
import java.util.LinkedHashMap;

public class SearchService {

  private final SearchRepository searchRepository;
  public SearchService(Connection conn){
    this.searchRepository = new SearchRepository(conn);
  }

  public HomeResult getGroupsForHomepage(
    LinkedHashMap<String, String> searchParams
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    HomeResult groups = searchRepository.getGroupsForHomepage(params);

    return groups;
  }


  public Group getSingleGroup(
      LinkedHashMap<String, String> searchParams) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);

    GroupSearchResult groups = searchRepository.getGroupsWithDetails(params);
    if(groups.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }
    return groups.getFirstGroup();
  }
}
