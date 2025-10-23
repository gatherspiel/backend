package service.update;

import app.groups.data.Event;
import app.result.error.group.EventNotFoundError;
import app.users.data.User;
import app.groups.data.EventLocation;
import app.result.error.PermissionError;
import app.users.data.UserData;
import database.content.EventRepository;
import database.files.ImageRepository;
import database.permissions.UserPermissionsRepository;

import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
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
      UserPermissionsRepository userPermissionsRepository = new UserPermissionsRepository(connection);

      Set<User> eventEditors = userPermissionsRepository.getEventEditorRoles(eventId);



      boolean currentUserCanEdit =
          user.isSiteAdmin() ||
          eventEditors.contains(user) ||
          userPermissionsRepository.hasGroupEditorRole(user, event.get().getGroupId());
      event.get().setUserCanEditPermission(currentUserCanEdit);
      event.get().setModerators(eventEditors);
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
    Event created = eventRepository.createEvent(event, groupId);

    if(event.getImage() != null){
      ImageRepository imageRepository = new ImageRepository();
      imageRepository.uploadImage(event.getImage(), event.getImageFilePath());
      created.setImage(event.getImage());
      created.setImageFilePath(event.getImageFilePath());
    }
    created.setGroupId(groupId);
    return created;
  }

  public Event updateEvent(Event event) throws Exception{

    if(!groupPermissionService.canEditEvent(event)){
      throw new PermissionError("User does not have permission to modify event");
    }
    Event updated = eventRepository.updateEvent(event);
    if(event.getImage() != null){
      ImageRepository imageRepository = new ImageRepository();
      imageRepository.uploadImage(event.getImage(), event.getImageFilePath());
      updated.setImage(event.getImage());
      updated.setImageFilePath(event.getImageFilePath());
    }

    if(event.getModerators().size()>0){
      eventRepository.clearEventModerators(event);
      for(User moderator: event.getModerators()){
        eventRepository.addEventModerator(event, moderator);
      }
    }

    return updated;
  }

  public void deleteEvent(int eventId, int groupId) throws Exception {
    if(!groupPermissionService.canEditGroup(groupId)){
      throw new PermissionError("User does not have permission to add event to group");
    }
    eventRepository.deleteEvent(eventId, groupId);
  }

  public void addEventModerator(Event event, User moderatorToAdd) throws Exception{
    if(moderatorToAdd.isSiteAdmin()){
      throw new PermissionError("Site admin cannot be event moderator");
    }
    if(!groupPermissionService.canEditEvent(event)){
      throw new PermissionError("User does not have permission to add event moderator");
    }

    eventRepository.addEventModerator(event, moderatorToAdd);
  }

  public void removeEventModerator(Event event, User moderatorToRemove) throws Exception {
    if(!groupPermissionService.canEditEvent(event)){
      throw new PermissionError("User does not have permission to add event moderator");
    }

    eventRepository.removeEventModerator(event, moderatorToRemove);
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
    event.setLocation("Event_"+ UUID.randomUUID()+",Somewhere,VA 22222");
    event.setDescription("Event_"+ UUID.randomUUID());
    event.setUrl("localhost:/1234/"+UUID.randomUUID());
    event.setDay(DayOfWeek.FRIDAY.toString());
    event.setIsRecurring(true);
    event.setStartTime(start);
    event.setEndTime(end);

    return event;
  }

  public static Event createOneTimeEventObjectWithData() throws Exception{
    Event event = new Event();
    event.setName("Event_"+ UUID.randomUUID());
    event.setLocation("Event_"+ UUID.randomUUID()+",Somewhere,VA 22222");
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
