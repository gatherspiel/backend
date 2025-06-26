package service.update;

import app.data.Event;
import app.data.auth.User;
import database.content.EventRepository;

import java.sql.Connection;
import java.util.Optional;

public class EventEditService {

  Connection connection;
  EventRepository eventRepository;
  public EventEditService(Connection connection, EventRepository eventRepository){
    this.connection = connection;
    this.eventRepository = eventRepository;
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
    //TODO: Verify permissions
    return eventRepository.addEvent(event, groupId, connection);
  }

  public Event updateEvent(Event event, int groupId, User user) throws Exception{
    //TODO: Verify permissions
    return eventRepository.addEvent(event, groupId, connection);
  }

  public void deleteEvent(int eventId, User user) throws Exception {

  }

  public static Event createEventObject(String eventName, String day, String location, String summary, String url) throws Exception{
    Event event = new Event();
    event.setName(eventName);
    event.setDay(day);
    event.setLocation(location);
    event.setSummary(summary);
    event.setUrl(url);
    return event;
  }
}
