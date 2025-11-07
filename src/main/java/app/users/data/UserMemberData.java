package app.users.data;

import app.groups.data.Event;
import app.groups.data.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.TreeSet;

public class UserMemberData {

  private static final Logger logger = LogManager.getLogger(
      UserMemberData.class
  );

  class GroupComparator implements Comparator<Group> {
    public int compare(Group group1, Group group2){
      return group1.getName().compareTo(group2.getName());
    }
  }

  class EventComparator implements Comparator<Event> {
    public int compare(Event event1, Event event2){
      return event1.getStartDateTime().compareTo(event2.getStartDateTime());
    }
  }

  TreeSet<Group> joinedGroups = new TreeSet<>(new GroupComparator());
  TreeSet<Group> moderatingGroups = new TreeSet<>(new GroupComparator());
  TreeSet<Event> attendingEvents = new TreeSet<>(new EventComparator());
  TreeSet<Event> moderatingEvents = new TreeSet<>(new EventComparator());

  public void setJoinedGroups(TreeSet<Group> joinedGroups){
    this.joinedGroups = joinedGroups;
  }

  public TreeSet<Group> getJoinedGroups(){
    return joinedGroups;
  }

  public void setModeratingGroups(TreeSet<Group> moderatingGroups){
    this.moderatingGroups = moderatingGroups;
  }

  public TreeSet<Group> getModeratingGroups(){
    return moderatingGroups;
  }

  public void setAttendingEvents(TreeSet<Event> attendingEvents){
    this.attendingEvents = attendingEvents;
  }

  public TreeSet<Event> getAttendingEvents(){
    return attendingEvents;
  }

  public void setModeratingEvents(TreeSet<Event> moderatingEvents){
    this.moderatingEvents = moderatingEvents;
  }

  public TreeSet<Event> getModeratingEvents(){
    return moderatingEvents;
  }

  @JsonIgnore
  public void addGroupAsMember(Group group){
    joinedGroups.add(group);
  }

  @JsonIgnore
  public void addGroupAsModerator(Group group){
    moderatingGroups.add(group);
  }

  @JsonIgnore
  public void addEventAsMember(Event event){
    int size1 = attendingEvents.size();
    attendingEvents.add(event);
    int size2 = attendingEvents.size();

    if(size1 == size2){
      logger.warn("User has RSVP to multiple events starting at the same time. Only one of them will be displayed");
    }
  }

  @JsonIgnore
  public void addEventAsModerator(Event event){
    int size1 = moderatingEvents.size();
    moderatingEvents.add(event);
    int size2 = moderatingEvents.size();

    if(size1 == size2){
      logger.warn("User is moderator to multiple events starting at the same time. Only one of them will be displayed");
    }
  }
}
