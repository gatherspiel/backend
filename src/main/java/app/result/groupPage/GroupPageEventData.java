package app.result.groupPage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

class GroupPageEventDataComparator implements Comparator<GroupPageEventData> {

  public int compare(GroupPageEventData eventData1, GroupPageEventData eventData2) {
    return eventData2.getEventDate().compareTo(eventData1.getEventDate());
  }
}

// Event that will be shown on a group page.
public class GroupPageEventData {

  private LocalDate eventDate;

  public LocalDate getEventDate(){
    return eventDate;
  }
}
