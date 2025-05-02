package service;

import app.result.GroupSearchResult;
import app.result.groupPage.GroupPageData;
import database.utils.ConnectionProvider;

public class GroupService {

  SearchService searchService;

  public GroupService(SearchService searchService){
    this.searchService = searchService;
  }

  public GroupPageData getGroupPageData(ConnectionProvider connectionProvider) throws Exception{

    GroupSearchResult groupSearchResult = searchService.getGroup();

    /*
      TODO

      - Convert groupSearchResult into GroupPageEventData object. For recurring events, show all events that are within
      the next 30 days. For other events, only show events that are within the next 30 days.

      - Update return statement;
     */
    return null;
  }
}
