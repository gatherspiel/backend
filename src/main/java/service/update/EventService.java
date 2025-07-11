package service.update;

import app.groups.data.Event;
import app.users.data.User;
import app.groups.data.EventLocation;
import app.result.error.PermissionError;
import database.content.EventRepository;
import service.permissions.GroupPermissionService;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class EventService {

  Connection connection;
  EventRepository eventRepository;
  GroupPermissionService groupPermissionService;
  User user;

  public EventService(Connection connection, EventRepository eventRepository, GroupPermissionService groupPermissionService, User user){
    this.connection = connection;
    this.eventRepository = eventRepository;
    this.groupPermissionService = groupPermissionService;
    this.user = user;
  }

  public Optional<Event> getEvent(int eventId) throws Exception{
    var event = eventRepository.getEvent(eventId);

    if(event.isPresent()){
      if(groupPermissionService.canEditGroup(event.get().getGroupId())) {
        event.get().setUserCanEditPermission(true);
      } else {
        event.get().setUserCanEditPermission(false);
      }
    }
    return event;
  }

  /**
   *
   * @param event
   * @param groupId
   * @return Event with a unique id.
   */
  public Event createEvent(Event event, int groupId) throws Exception{

    if(!groupPermissionService.canEditGroup(groupId)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    return eventRepository.addEvent(event, groupId);
  }

  public Event updateEvent(Event event, int groupId) throws Exception{
    if(!groupPermissionService.canEditGroup(groupId)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    return eventRepository.updateEvent(event);
  }

  public void deleteEvent(int eventId, int groupId) throws Exception {
    if(!groupPermissionService.canEditGroup(groupId)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    eventRepository.deleteEvent(eventId, groupId);
  }

  public static Event createEventObject(
      String eventName,
      String location,
      String description,
      String url,
      LocalDateTime startTime,
      LocalDateTime endTime) throws Exception
  {
    Event event = new Event();
    event.setName(eventName);
    event.setLocation(location);
    event.getDescription(description);
    event.setUrl(url);
    event.setStartTime(startTime);
    event.setEndTime(endTime);
    return event;
  }

  public static Event createEventObjectWithTestData() throws Exception{
    Event event = new Event();
    event.setName("Event_"+ UUID.randomUUID());
    event.setLocation("Event_"+ UUID.randomUUID());
    event.getDescription("Event_"+ UUID.randomUUID());
    event.setUrl("localhost:/1234/"+UUID.randomUUID());
    event.setStartTime(LocalDateTime.now().plusHours(1));
    event.setEndTime(LocalDateTime.now().plusHours(5));
    event.setEventLocation(generateEventLocation());
    return event;
  }

  private static EventLocation generateEventLocation() throws Exception{
    EventLocation eventLocation = new EventLocation();
    eventLocation.setCity("Crystal City");
    eventLocation.setState("VA");
    eventLocation.setStreetAddress("1750 Crystal Drive");
    eventLocation.setZipCode(22202);
    return eventLocation;
  }
}
