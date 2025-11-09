package service.update;

import app.users.User;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;

public class GroupEventRsvpData {

  public HashMap<Integer, AbstractMap.SimpleEntry<Integer,Boolean>> rsvpData = new HashMap<>();
  public HashMap<Integer,Boolean> userCanRsvp = new HashMap<>();

  public HashSet<User> moderators = new HashSet<>();

  public void setRsvpData(HashMap<Integer, AbstractMap.SimpleEntry<Integer,Boolean>> rsvpData){
    this.rsvpData = rsvpData;
  }

  public void addModerator(User user){
      moderators.add(user);
  }

  public HashSet<User> getModerators(){
    return moderators;
  }

  public void setUserCanRsvp(boolean userCanRsvp,int eventId){
    this.userCanRsvp.put(eventId, userCanRsvp);
  }

  public boolean canRsvpToEvent(int eventId){
    if(!userCanRsvp.containsKey(eventId)){
      return true;
    }else {
      return userCanRsvp.get(eventId);
    }
  }
}
