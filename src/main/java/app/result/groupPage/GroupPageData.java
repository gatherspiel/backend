package app.result.groupPage;

import java.util.Comparator;
import java.util.TreeSet;



public class GroupPageData {

  private final int id;
  private final String name;
  private final String url;
  private final String summary;

  private TreeSet<GroupPageEventData> groupPageEventData;

  public GroupPageData(int id, String name, String url, String summary){
    this.id = id;
    this.name = name;
    this.url = url;
    this.summary = summary;

    this.groupPageEventData = new TreeSet<GroupPageEventData>(new GroupPageEventDataComparator());
  }
}
