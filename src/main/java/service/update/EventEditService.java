package service.update;

import app.data.event.Event;
import app.data.auth.User;
import app.result.error.PermissionError;
import database.content.EventRepository;
import service.permissions.GroupPermissionService;

import java.sql.Connection;
import java.util.Optional;

public class EventEditService {

  Connection connection;
  EventRepository eventRepository;
  GroupPermissionService groupPermissionService;

  public EventEditService(Connection connection, EventRepository eventRepository, GroupPermissionService groupPermissionService){
    this.connection = connection;
    this.eventRepository = eventRepository;
    this.groupPermissionService = groupPermissionService;
  }

  public Optional<Event> getEvent(int eventId) throws Exception{
    return eventRepository.getEvent(eventId, connection);
  }

  /**
   *
   * @param event
   * @param groupId
   * @param user
   * @return Event with a unique id.
   */
  public Event addEvent(Event event, int groupId, User user) throws Exception{

    if(!groupPermissionService.canEditGroup(user, groupId, connection)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    return eventRepository.addEvent(event, groupId, connection);
  }

  public Event updateEvent(Event event, int groupId, User user) throws Exception{
    if(!groupPermissionService.canEditGroup(user, groupId, connection)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    return eventRepository.addEvent(event, groupId, connection);
  }

  public void deleteEvent(Event event, int groupId, User user) throws Exception {
    if(!groupPermissionService.canEditGroup(user, groupId, connection)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    eventRepository.deleteEvent(event, groupId, connection);
  }

  public static Event createEventObject(
      String eventName,
      String location,
      String summary,
      String url,
      String startTime,
      String endTime) throws Exception
  {
    Event event = new Event();
    event.setName(eventName);
    event.setLocation(location);
    event.setSummary(summary);
    event.setUrl(url);
    event.setStartTime(startTime);
    event.setEndTime(endTime);
    return event;
  }
}
