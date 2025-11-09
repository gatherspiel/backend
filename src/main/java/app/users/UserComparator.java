package app.users;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
  public int compare(User user1, User user2){
    int idDiff = Integer.compare(user1.getId(), user2.getId());
    if(idDiff != 0){
      return idDiff;
    }

    return user1.getUserData().getUsername().compareTo(user2.getUserData().getUsername());
  }
}