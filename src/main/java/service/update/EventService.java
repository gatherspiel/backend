package service.update;

import app.groups.data.Event;
import app.users.data.User;
import app.groups.data.EventLocation;
import app.result.error.PermissionError;
import database.content.EventRepository;
import service.permissions.GroupPermissionService;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    var event = eventRepository.getOneTimeEvent(eventId);

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
    return eventRepository.addOneTimeEvent(event, groupId);
  }

  public Event updateEvent(Event event, int groupId) throws Exception{
    if(!groupPermissionService.canEditGroup(groupId)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    return eventRepository.updateOneTimeEvent(event);
  }

  public void deleteEvent(int eventId, int groupId) throws Exception {
    if(!groupPermissionService.canEditGroup(groupId)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    eventRepository.deleteOneTimeEvent(eventId, groupId);
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
    event.setDescription(description);
    event.setUrl(url);
    event.setStartDate(startTime.toLocalDate());
    event.setStartTime(startTime.toLocalTime());
    event.setEndDate(endTime.toLocalDate());
    event.setEndTime(endTime.toLocalTime());
    return event;
  }

  public static Event createRecurringEventObjectWithData(LocalTime start, LocalTime end) throws Exception {
    Event event = new Event();
    event.setName("Event_"+ UUID.randomUUID());
    event.setLocation("Event_"+ UUID.randomUUID());
    event.setDescription("Event_"+ UUID.randomUUID());
    event.setUrl("localhost:/1234/"+UUID.randomUUID());

    event.setIsRecurring(true);
    event.setStartTime(start);
    event.setEndTime(end);

    return event;
  }

  public static Event createOneTimeEventObjectWithData() throws Exception{
    Event event = new Event();
    event.setName("Event_"+ UUID.randomUUID());
    event.setLocation("Event_"+ UUID.randomUUID());
    event.setDescription("Event_"+ UUID.randomUUID());
    event.setUrl("localhost:/1234/"+UUID.randomUUID());
    event.setStartTime(LocalTime.now());
    event.setStartDate(LocalDate.now());
    event.setEndTime(LocalTime.now().plusHours(5));
    event.setEndDate(LocalDate.now());
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
