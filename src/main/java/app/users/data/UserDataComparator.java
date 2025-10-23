package app.users.data;

import java.util.Comparator;

public class UserDataComparator implements Comparator<UserData> {
  public int compare(UserData user1, UserData user2){
    return user1.getUsername().compareTo(user2.getUsername());
  }
}