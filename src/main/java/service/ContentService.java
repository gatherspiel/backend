package service;

import app.data.auth.User;

public abstract class ContentService {
  protected User currentUser;

  protected ContentService(User currentUser){
    this.currentUser = currentUser;
  }
}
