package service;

import app.users.data.User;

public abstract class ContentService {
  protected User currentUser;

  protected ContentService(User currentUser){
    this.currentUser = currentUser;
  }
}
