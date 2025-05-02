package service;

import app.data.Group;
import app.result.GroupSearchResult;
import app.result.groupPage.GroupPageData;
import database.utils.ConnectionProvider;

import java.util.LinkedHashMap;

public class GroupService {

  SearchService searchService;

  public GroupService(SearchService searchService){
    this.searchService = searchService;
  }

  public GroupPageData getGroupPageData(LinkedHashMap<String, String> params,
                                        ConnectionProvider connectionProvider) throws Exception{

    GroupSearchResult groupSearchResult = searchService.getGroups(params, connectionProvider);

    if(groupSearchResult.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }

    System.out.println(params);
    Group group = groupSearchResult.getFirstGroup();
    if(group == null){
      throw new Exception("No group found");
    }
    return GroupPageData.createFromSearchResult(group);

  }
}
