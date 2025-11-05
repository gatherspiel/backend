package app.users.data;

import app.groups.data.Event;
import app.groups.data.Group;

import java.util.TreeSet;

public class UserGroupMemberData {

  TreeSet<Group> joinedGroups = new TreeSet<>();
  TreeSet<Group> moderatingGroups = new TreeSet<>();
  TreeSet<Event> attendingEvents = new TreeSet<>();
  TreeSet<Event> hostingEvents = new TreeSet<>();


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

  public void setHostingEvents(TreeSet<Event> hostingEvents){
    this.hostingEvents = hostingEvents;
  }

  public TreeSet<Event> getHostingEvents(){
    return hostingEvents;
  }
}
